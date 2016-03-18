package net.service

import akka.actor._
import net.model.AST._
import spray.http._
import scala.concurrent.Future

class BurnerServiceActor(authHeader: HttpHeader, 
						 pipeline: HttpRequest => Future[HttpResponse]) extends BurnerService 
          																with DropboxServiceImpl 
          																with Actor {
	override def actorRefFactory = context

	override def receive = runRoute(rootRoute(authHeader, pipeline))
}