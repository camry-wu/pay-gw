package controllers.conf

import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.templates.Html

import models.conf.BizChannelArray
import models.conf.BizChannel

import models.conf.AnnouncementArray
import models.conf.Announcement

import utils._

case class AnnouncementForm(
	annType			: Int,
	content			: String,
	startTime		: Option[String],
	endTime			: Option[String]
)

/**
 * 管理配置页面.
 *	渠道、公告
 */
object ConfController extends Controller {
	/**
	 * 公告 form
	 */
	val announcementForm = Form(
		mapping(
			"annType" -> number(min=1, max=2),
			"content" -> nonEmptyText,
			"startTime" -> optional(text),
			"endTime" -> optional(text)
		)
		(AnnouncementForm.apply)
		(AnnouncementForm.unapply)
	)

	/**
	 * 配置首页（渠道配置管理页）.
	 */
	def index = Action {
		Ok(views.html.conf.bizChannel())
	}

	/**
	 * 测试页面，用于测试各种效果和功能.
	 */
	def test = Action {
		Ok(views.html.conf.test())
	}

	/**
	 * 配置首页（渠道配置管理页）.
	 */
	def bizChannelMgr = Action {
		Ok(views.html.conf.bizChannel())
	}

	/**
	 * 查询渠道 json 列表.
	 */
	def listBizChannel(limit: Int, offset: Int, keyword: String) = Action.async { request =>
		BizChannel.list(limit, offset, keyword) map {
			case channels:BizChannelArray => Ok(Json.toJson(channels))
			case _ => NoContent
		}
	}

	/**
	 * 公告配置管理页.
	 */
	def announcementMgr = Action {
		Ok(views.html.conf.announcement())
	}

	/**
	 * 查询公告 json 列表.
	 */
	def listAnnouncement(limit: Int, offset: Int, keyword: String) = Action.async { request =>
		Announcement.list(limit, offset, keyword) map {
			case anns:AnnouncementArray => Ok(Json.toJson(anns))
			case _ => NoContent
		}
	}

	/**
	 * 新建公告.
	 */
	def newAnnouncement = Action.async { implicit request =>
		announcementForm.bindFromRequest match {
			case announcementForm:Form[AnnouncementForm] if !announcementForm.hasErrors => {
				val annform = announcementForm.get
				val ann = Announcement(0, annform.annType, annform.content, str2DateTime(annform.startTime), str2DateTime(annform.endTime), Some(now.asInstanceOf[DateTime]), true, 1)

				Announcement.create(ann) map {
					case Some(oid:Long) => Created(Json.obj("created" -> oid))
					case _ => InternalServerError(Json.obj("created" -> false))
				}
			}
			case _ => Future { BadRequest(Json.obj("reason" -> "无法读取表单.")) }
		}
	}
}
