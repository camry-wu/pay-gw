package controllers.conf

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.templates.Html

import models.conf.BizChannelArray
import models.conf.BizChannel

import controllers._

object ConfController extends Controller {
	def index= Action {
		Ok(views.html.conf.payChannel())
	}

	def listPayChannel(limit: Int, offset: Int, keyword: String) = Action.async { request =>
		BizChannel.list(limit, offset, keyword) map {
			case channels:BizChannelArray => Ok(Json.toJson(channels))
			case _ => NoContent
		}
	}
}