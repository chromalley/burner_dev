package net.service

import akka.actor._

class BurnerServiceActor extends BurnerService with Actor {
	override def actorRefFactory = context

	override def receive = runRoute(rootRoute)
}