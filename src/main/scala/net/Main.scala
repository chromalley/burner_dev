package net

import akka.actor.ActorSystem
import com.google.common.util.concurrent.AtomicLongMap
import net.service._
import akka.actor.{ActorSystem, Props}
import akka.io.IO
import net.model.AST._
import spray.can.Http
import spray.http.{Uri, HttpHeader}
import spray.http.HttpHeaders.RawHeader
import spray.client.pipelining._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

	type ImageName = String

	// Keeps track of votes for images
	// credit: http://stackoverflow.com/a/3339811/409976
	private [net] val imageVoting: AtomicLongMap[ImageName] = 
		AtomicLongMap.create()

	def main(args: Array[String]): Unit = {

		args.toList match {
			case oauthToken :: Nil => 
				runWebService(authHeader(oauthToken))
			case badArgs =>
				sys.error(s"Expected OauthToken but got ${badArgs}.")
		}
	}

	private def runWebService(authHeader: HttpHeader)(implicit ec: ExecutionContext): Unit = {
	    implicit val system = ActorSystem()
	    implicit val ec: ExecutionContext = system.dispatcher

    	val service = system.actorOf(Props(new BurnerServiceActor(authHeader, sendReceive)), "burner-service")

		IO(Http) ! Http.Bind(service, interface = "localhost", port = 9000)
	}

	private def authHeader(oauthToken: String): HttpHeader =
		RawHeader("Authorization", s"Bearer $oauthToken")

}