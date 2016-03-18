package net.service

import spray.routing.HttpService
import net.model.AST._
import spray.httpx.SprayJsonSupport._
import spray.json._
import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._
import spray.httpx.marshalling._
import com.google.common.util.concurrent.AtomicLongMap
import net.Main._
import scala.concurrent.Future
import spray.http.StatusCodes
import scala.concurrent.ExecutionContext
import scala.collection.JavaConverters._
import scala.util._
import spray.http.{HttpRequest, HttpResponse, HttpHeader}
import java.net.URL

object BurnerService {

	private def convertMap(votingMap: AtomicLongMap[ImageName]): JsObject = {
		val map: Map[ImageName, java.lang.Long] = 
			votingMap.asMap.asScala.toMap
		val jsonMap: Map[ImageName, JsValue] = map.map {
			case (key, value) => (key -> JsNumber(value))
		}
		JsObject(jsonMap)
	}
}

trait BurnerService extends DropboxService with HttpService {

	import BurnerService._

	def eventRoute(authHeader: HttpHeader,
				   pipeline: HttpRequest => Future[HttpResponse])(implicit ec: ExecutionContext) = 
	   path("event") {
			post {
		    	entity(as[Event]) { e => 
		    		// detach here for Future? (re-read SO thread - http://stackoverflow.com/q/31364405/409976)
					onComplete(handleEvent(e, authHeader, pipeline)) { 
						case Success(_) => 
							complete(StatusCodes.OK)
						case Failure(err) =>
							complete(StatusCodes.InternalServerError -> err.toString)
					}
		    	}
			}
		}

	def reportRoute(authHeader: HttpHeader,
					pipeline: HttpRequest => Future[HttpResponse])(implicit ec: ExecutionContext) = 
		path("report") {		
			get { 
				complete { 
					convertMap(imageVoting)
				}
			}
		}

	def rootRoute(authHeader: HttpHeader, 
		          pipeline: HttpRequest => Future[HttpResponse]) =
		eventRoute(authHeader, pipeline)(actorRefFactory.dispatcher) ~ 
		reportRoute(authHeader, pipeline)(actorRefFactory.dispatcher)

	private def handleEvent(event: Event,
							authHeader: HttpHeader, 
        					pipeline: HttpRequest => Future[HttpResponse])
							(implicit ec: ExecutionContext): Future[Unit] = {
		event match {
			case InboundMedia(picture, _ , _) =>
				handleMedia(picture, authHeader, pipeline)
				// Future.successful(Some(imageVoting.getAndIncrement(picture.getFile)))
			case InboundText(message,_,_) =>
				handleText(message, authHeader, pipeline)
			case Voicemail(_,_,_) =>
				Future.successful( () ) 
		}		
	}

	private def handleMedia(picture: URL, 
							authHeader: HttpHeader,
		                    pipeline: HttpRequest => Future[HttpResponse])
							(implicit ec: ExecutionContext): Future[Unit] = {
		val fileName = picture.getPath
	getFile(fileName, authHeader, pipeline).flatMap {
				case Exists => 
					Future.successful( () )
				case DoesNotExist => 
					uploadFile(picture, authHeader, pipeline)
			}	
	}

	private def handleText(message: String, 
		                   authHeader: HttpHeader,
		                   pipeline: HttpRequest => Future[HttpResponse])
						  (implicit ec: ExecutionContext): Future[Unit] = 
		getFile(message, authHeader, pipeline).flatMap {
			case Exists => 
				imageVoting.getAndIncrement(message)
				Future.successful( () )
			case DoesNotExist => 
				Future.successful( () )				
		}	

}