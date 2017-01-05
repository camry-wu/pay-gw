package controllers.conf

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.templates.Html

import models.conf.PayChannelConfArray
import models.conf.PayChannelConf

import controllers._

object ConfController extends Controller {
	def index= Action {
		Ok(views.html.conf.payChannel())
	}

	def listPayChannel(limit: Int, offset: Int, keyword: String) = Action.async { request =>
		PayChannelConf.list(limit, offset, keyword) map {
			case channels:PayChannelConfArray => Ok(Json.toJson(channels))
			case _ => NoContent
		}
	}
}
