package net.service

import scala.concurrent.Future
import java.net.URL
import scala.concurrent.ExecutionContext
import net.model.AST._
import spray.http.{HttpRequest, HttpResponse, HttpHeader}

trait DropboxService {
	
	def uploadFile(url: URL, 
				   authHeader: HttpHeader,
				   pipeline: HttpRequest => Future[HttpResponse])(implicit ec: ExecutionContext): Future[Unit]

	def getFile(name: String, 
		        authHeader: HttpHeader,
		        pipeline: HttpRequest => Future[HttpResponse])(implicit ec: ExecutionContext): Future[ImageLookupResult]

}