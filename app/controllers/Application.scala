package controllers

import play.api.mvc.{Action, Controller}
import play.api.templates.Html
import models._

object Application extends Controller {
	def index = Action {
		Ok(views.html.userlist())
	}

	def bill = Action {
		Ok(views.html.userlist())
	}

  def view = Action {
	  Ok(views.html.bill(Message.list, Message.form))
  }

  def postMsg = Action { implicit request =>
	Message.form.bindFromRequest.fold(
		errors => BadRequest(views.html.bill(Message.list, errors)),
		{
			case (name, content) =>
				Message.post(name, content)
				Redirect(routes.Application.view)
		}
	)
  }
}
