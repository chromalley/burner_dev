package net.model

import java.net.URL
import spray.http.{Uri, HttpHeader}

object AST {

	private [model] val EventTypeFieldKey = "type"
	private [model] val InboundMediaType  = "inboundMedia"
	private [model] val InboundTextType   = "inboundText"
	private [model] val VoicemailType     = "voicemail"

	sealed trait Event
	case class InboundMedia(payload: URL, fromNumber: String, toNumber: String) extends Event
	case class InboundText(payload: String, fromNumber: String, toNumber: String) extends Event
	case class Voicemail(payload: String, fromNumber: String, toNumber: String) extends Event

	case class DropboxSettings(authHeader: HttpHeader, base: Uri, folderPath: String)

	sealed trait ImageLookupResult
	case object Exists extends ImageLookupResult
	case object DoesNotExist extends ImageLookupResult

	case class UploadFileStatusResponse(status: UploadFileStatus, job: String)

	sealed trait UploadFileStatus
	case object Pending extends UploadFileStatus
	case object Downloading extends UploadFileStatus
	case object Complete extends UploadFileStatus
	case object Failed extends UploadFileStatus

	object UploadFileStatus {
		def read(x: String): Option[UploadFileStatus] = x match {
			case "PENDING" 	   => Some(Pending)
			case "DOWNLOADING" => Some(Downloading)
			case "COMPLETE"    => Some(Complete)
			case "FAILED"      => Some(Failed)
			case _		       => None
		}
	}
}