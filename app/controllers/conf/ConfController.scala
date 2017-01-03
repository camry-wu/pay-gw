package controllers.conf

import play.api.mvc.{Action, Controller}
import play.api.templates.Html
import models._

object ConfController extends Controller {
	def listPayChannel= Action {
		Ok(views.html.conf.payChannel())
	}
}
