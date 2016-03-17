package net

import akka.actor.ActorSystem
import com.google.common.util.concurrent.AtomicLongMap
import net.service._
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http


object Main {

	type ImageName = String

	// credit: http://stackoverflow.com/a/3339811/409976
	private [net] val imageVoting: AtomicLongMap[ImageName] = 
		AtomicLongMap.create()
	
	def main(args: Array[String]): Unit = {
	    implicit val system = ActorSystem()

        val service = system.actorOf(Props[BurnerServiceActor], "burner-service")

        IO(Http) ! Http.Bind(service, interface = "localhost", port = 9000)
	}

}