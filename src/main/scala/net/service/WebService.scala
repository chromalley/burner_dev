package net.service

import akka.actor.ActorSystem
import spray.routing.HttpService
import spray.http.StatusCodes

trait WebService extends HttpService {
	
	implicit def actorRef = ActorSystem()

	override val route = {
		 path("foobar") {
		 	complete(StatusCodes.OK)
		 }
	}
}