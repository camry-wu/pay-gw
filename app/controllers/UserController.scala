
package controllers

import models.UserArray
import models.User
import utils._

import play.api.Logger
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

object UserController extends Controller {

	val logger = Logger(this.getClass())

	def view = Action {
		Ok(views.html.user(0))
	}

	def get(oid:Long) = Action.async {
		User.getById(oid) map {
			case Some(user:User) => Ok(Json.toJson(user))
			case _ => NoContent
		}
	}

	def list(limit: Int, offset: Int, openId: String) = Action.async {	request =>
		User.list(limit, offset, openId) map {
			case users:UserArray => Ok(Json.toJson(users))
			case _ => NoContent
		}
	}

	def find(openId:String) = Action.async {
		User.findByOpenId(openId) map {
			case users:UserArray => Ok(Json.toJson(users))
			case _ => NoContent
		}
	}

	def post = Action.async { implicit request =>
		val form = Form(
			"openId" -> text
		)

		form.bindFromRequest match {
			case form:Form[String] if !form.hasErrors => {
				val openId = form.get

				User.create(openId, "openIdSrc", "career") map {
					case Some(oid:Long) => Created(Json.obj("created" -> oid))
					case _ => InternalServerError(Json.obj("created" -> false))
				}
			}
			case _ => Future { BadRequest(Json.obj("reason" -> "Could not bind POST data to form.")) }
		}
	}

	def put(oid:Long) = Action.async { implicit request =>

		val form = Form(
			"name" -> text
		)

		form.bindFromRequest match {
			case form:Form[String] if !form.hasErrors => {
				val mobile = form.get

				User.update(oid, mobile, "career") map {
					case rows:Int if rows > 0 => Accepted(Json.obj("updated" -> true))
					case _ => InternalServerError(Json.obj("updated" -> false))
				}
			}
			case _ => Future { BadRequest(Json.obj("reason" -> "Could not bind POST data to form.")) }
		}
	}

	def delete(oid:Long) = Action.async {
		User.delete(oid) map {
			case rows:Int if rows > 0 => Accepted(Json.obj("deleted" -> true))
			case _ => InternalServerError(Json.obj("deleted" -> false))
		}
	}

}
