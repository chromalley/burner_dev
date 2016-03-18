package net.service

import scala.concurrent.Future
import java.net.URL
import scala.concurrent.ExecutionContext
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import net.model.AST._
import spray.http.{HttpRequest, HttpResponse, HttpHeader, Uri, StatusCodes}
import Uri.Path
import spray.client.pipelining._
import spray.httpx.unmarshalling._

object DropboxServiceImpl {

	private val GetFile = Uri("https://content.dropboxapi.com/1/files/auto")

	private def get(relativePath: String) = {
		val getFilePath = GetFile.path
		val newPath = getFilePath ++ Path(s"/${relativePath}")
		GetFile.withPath(newPath)
	}
	private val UploadFile = Uri("https://api.dropboxapi.com/1/save_url/auto/")

	private val UrlPathKey = "url"

	private def update(fileName: String, url: URL) = {
		val updateFilePath = UploadFile.path
		val newPath = updateFilePath ++ Path(s"/${fileName}")
		UploadFile.withPath(newPath).withQuery(UrlPathKey -> url.toString)
	}

	private def handleGetFile(httpResponse: HttpResponse): Future[ImageLookupResult] = {
		if(httpResponse.status.isSuccess) {
			Future.successful( Exists )
		}
		else if(httpResponse.status == StatusCodes.NotFound) {
			Future.successful( DoesNotExist )
		}
		else {
			Future.failed(new RuntimeException("Failed to get file."))
		}
	}

	private def handleUploadFile(file: String, httpResponse: HttpResponse): Future[Unit] = {
		println(httpResponse.status)
		println(httpResponse.entity.asString)
		httpResponse.entity.as[UploadFileStatusResponse] match {
			case Right(UploadFileStatusResponse(Complete, _))    => Future.successful( () )
			case Right(UploadFileStatusResponse(Pending, _))     => Future.successful( () )
			case Right(UploadFileStatusResponse(Downloading, _)) => Future.successful( () )
			case failedResult 					   			     => 
				Future.failed(new RuntimeException(s"Failed to upload file: ${file} due to ${failedResult}"))
		}
	}
}

trait DropboxServiceImpl extends DropboxService {
	
	import DropboxServiceImpl._

	override def uploadFile(url: URL, 
		                    authHeader: HttpHeader,
		                    pipeline: HttpRequest => Future[HttpResponse])
							(implicit ec: ExecutionContext): Future[Unit] = {
								val fileName = url.getPath
								val request = Post(update(fileName, url))
								println(request)
								val result: Future[HttpResponse] = pipeline(request ~> authHeader)
								result.flatMap(handleUploadFile(fileName, _))
							}
	
	override def getFile(name: String, 
					 	 authHeader: HttpHeader,
					 	 pipeline: HttpRequest => Future[HttpResponse])
						(implicit ec: ExecutionContext): Future[ImageLookupResult] = {
							val request = Get(get(name))
							val result: Future[HttpResponse] = pipeline(request ~> authHeader)
							result.flatMap(handleGetFile)
						}
}