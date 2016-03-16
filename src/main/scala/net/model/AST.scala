package net.model

object AST {
	
	sealed trait Event
	case class Mms(x: Int) extends Event
	case class Sms(x: Int) extends Event
	case class Voicemail(x: Int) extends Event

}