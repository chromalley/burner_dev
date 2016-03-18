package net

import spray.json._
import DefaultJsonProtocol._
import net.model.AST._
import java.net.URL
import scala.util._

package object model {

	implicit val eventReader = new RootJsonReader[Event] {
		override def read(json: JsValue): Event = json match {
			case JsObject(fields) => 
				extractEvent(fields)
			case unexpectedJson =>
				deserializationError(s"Expected JsObject for `Event`, but got a ${unexpectedJson}.")
		}
	}

	implicit val urlReader = new JsonFormat[URL] {
		override def read(x: JsValue): URL = x match {
			case JsString(str) => 
				Try { new URL(str) } match {
					case Success(url) => 
						url
					case Failure(e)   => 
						deserializationError(s"Expected URL, but got $x")
			}
			case unexpectedJson => 
				deserializationError(s"Expected String for URL, but got $unexpectedJson")
		}

		override def write(url: URL): JsValue = 
			JsString(url.toString)
	}

	// Although we only need a RootJsonReader[X] for the following 3 
	// `Event`'s, I chose to use `jsonFormat3(X.apply)`  for conciseness.
	val inboundTextFormat: RootJsonFormat[InboundText] = 
		jsonFormat3(InboundText.apply)

	val inboundMediaFormat: RootJsonFormat[InboundMedia] = 
		jsonFormat3(InboundMedia.apply)

	val voicemailFormat: RootJsonFormat[Voicemail] = 
		jsonFormat3(Voicemail.apply)

	private def extractEvent(fields: Map[String, JsValue]): Event = {
		fields.get(EventTypeFieldKey) match {
			case Some(JsString(x)) if x == InboundMediaType => 
				inboundMediaFormat.read(JsObject(fields - EventTypeFieldKey))
			case Some(JsString(x)) if x == InboundTextType => 
				inboundTextFormat.read(JsObject(fields - EventTypeFieldKey))
			case Some(JsString(x)) if x == VoicemailType => 
				voicemailFormat.read(JsObject(fields - EventTypeFieldKey))
			case _ =>
				deserializationError(s"Expected Json Object with key-value `type` pair for an `Event`, but got ${fields})")
		}
	}

	implicit val uploadFileStatusFormat = new JsonFormat[UploadFileStatus] {
		override def read(x: JsValue): UploadFileStatus = x match {
			case JsString(str) => 
				UploadFileStatus.read(str).getOrElse(
					deserializationError(s"Expected valid Upload File Status, but got ${str}")
				)
			case unexpectedJson =>
				deserializationError(s"Expected String for UploadFileStatus, but got ${unexpectedJson}")
		}

		override def write(x: UploadFileStatus): JsValue = 
			JsString {
				x match {
					case Pending     => "PENDING"
					case Complete    => "COMPLETE"
					case Downloading => "DOWNLOADING"
					case Failed  	 => "FAILED"
				}
			}
	}

	implicit val uploadFileStatusResponseFormat: RootJsonFormat[UploadFileStatusResponse] = 
		jsonFormat2(UploadFileStatusResponse.apply)

}