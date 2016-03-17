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

trait BurnerService extends HttpService {

	def eventRoute(implicit ec: ExecutionContext) = path("event") {
		post {
	    	entity(as[Event]) { e => 
	    		// detach here for Future? (re-read SO thread - http://stackoverflow.com/q/31364405/409976)
	    		// Johannes replied ~8 months ago
				onComplete(handleEvent(e)) { 
					case Success(_) => 
						complete(StatusCodes.OK)
					case Failure(err) =>
						complete(StatusCodes.InternalServerError -> err.toString)
				}
	    	}
		}
	}

	def reportRoute(implicit ec: ExecutionContext) = path("report") {		
		get { 
			complete { 
				convertMap(imageVoting)
			}
		}
	}

	private def convertMap(votingMap: AtomicLongMap[ImageName]): JsObject = {
		val map: Map[ImageName, java.lang.Long] = 
			votingMap.asMap.asScala.toMap
		val jsonMap: Map[ImageName, JsValue] = map.map {
			case (key, value) => (key -> JsNumber(value))
		}
		JsObject(jsonMap)
	}

	def rootRoute = 
		eventRoute(actorRefFactory.dispatcher) ~ reportRoute(actorRefFactory.dispatcher)

	private def handleEvent(event: Event): Future[Option[Long]] = {
		event match {
			case InboundMedia(picture, _ , _) =>
				// TODO: upload to dropbox
				Future.successful(Some(imageVoting.getAndIncrement(picture)))
			case _ =>
				Future.successful(None) 
		}		
	}
}