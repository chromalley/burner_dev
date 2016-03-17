package net.model

object AST {

	private [model] val EventTypeFieldKey = "type"
	private [model] val InboundMediaType  = "inboundMedia"
	private [model] val InboundTextType   = "inboundText"
	private [model] val VoicemailType     = "voicemail"

	sealed trait Event
	case class InboundMedia(payload: String, fromNumber: String, toNumber: String) extends Event
	case class InboundText(payload: String, fromNumber: String, toNumber: String) extends Event
	case class Voicemail(payload: String, fromNumber: String, toNumber: String) extends Event
}