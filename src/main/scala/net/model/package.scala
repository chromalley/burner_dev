package net

import spray.json._
import DefaultJsonProtocol._
import net.model.AST._

package object model {

	implicit val eventReader = new RootJsonReader[Event] {
		override def read(json: JsValue): Event = json match {
			case JsObject(fields) => 
				extractEvent(fields)
			case unexpectedJson =>
				deserializationError(s"Expected JsObject for `Event`, but got a ${unexpectedJson}.")
		}
	}

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

}