
package models.conf

import java.util.UUID

import org.joda.time.format._
//import play.api.Logger
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import anorm._
import anorm.ParameterValue
import anorm.ParameterValue._
import anorm.SqlParser._
import scala.concurrent.Future

import models._
import models.AnormExtension._

/**
 * 渠道配置表.
 */
case class Announcement (

	/**
	 * 主键.
	 */
	oid				: Long,

	/**
	 * 类型(type 是个关键字).
	 *  1: 公告, 2: 配置
	 */
	annType			: Int,

	/**
	 * 内容.
	 */
	content			: String,

	/**
	 * 开始时间.
	 */
	startTime		: Option[DateTime],

	/**
	 * 结束时间.
	 */
	endTime			: Option[DateTime],

	/**
	 * 插入时间.
	 */
	insertTime		: Option[DateTime],

	/**
	 * 逻辑删除标志位.
	 */
	isActive		: Boolean,

	/**
	 * 版本.
	 */
	version			: Int
)

case class AnnouncementArray(total: Long, rows: Seq[Announcement])

object AnnouncementArray {
	implicit val announcementConfArrayJsonWrites = new Writes[AnnouncementArray] {
		def writes(dataArr: AnnouncementArray) = Json.obj(
			"total" -> dataArr.total,
			"rows" -> dataArr.rows
		)
	}

	implicit val announcementConfArrayReads : Reads[AnnouncementArray] = (
		(__ \ "total").read[Long] and
		(__ \ "rows").read[Seq[Announcement]]
	)(AnnouncementArray.apply _)
}

object Announcement extends ((
	Long,
	Int,
	String,
	Option[DateTime],
	Option[DateTime],
	Option[DateTime],
	Boolean,
	Int
) => Announcement) {
	val logger = Logger(this.getClass())

	implicit object dateTimeJsonWrites extends Writes[DateTime] {
		val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
		def writes(o: DateTime) = JsString(dateFormat.print(o.getMillis))
	}

	implicit val announcementConfJsonWrites: Writes[Announcement] = (
		(__ \ "oid").write[Long] and
		(__ \ "annType").write[Int] and
		(__ \ "content").write[String] and
		(__ \ "startTime").write[Option[DateTime]] and
		(__ \ "endTime").write[Option[DateTime]] and
		(__ \ "insertTime").write[Option[DateTime]] and
		(__ \ "isActive").write[Boolean] and
		(__ \ "version").write[Int]
	)(unlift(Announcement.unapply))

	implicit val announcementConfJsonReads : Reads[Announcement] = (
		(__ \ "oid").read[Long] and
		(__ \ "annType").read[Int] and
		(__ \ "content").read[String] and
		(__ \ "startTime").read[Option[DateTime]] and
		(__ \ "endTime").read[Option[DateTime]] and
		(__ \ "insertTime").read[Option[DateTime]] and
		(__ \ "isActive").read[Boolean] and
		(__ \ "version").read[Int]
	)(Announcement.apply _)

	implicit val jsonFormat = Json.format[Announcement]

	val announcements =
		int("Oid") ~
    	int("Type") ~
    	str("Content") ~
    	date("StartTime") ~
    	date("EndTime") ~
    	date("InsertTime") ~
    	int("IsActive") ~
    	int("Version") map {
			case	oid~annType~content~startTime~endTime~insertTime~isActive~version =>
				Announcement(
					oid,
					annType,
					content,
					Option(new DateTime(startTime)),
					Option(new DateTime(endTime)),
					Option(new DateTime(insertTime)),
					(isActive == 1),
					version)
    	}

	def list(limit:Int, offset: Int, keyword: String) = Future {
		val likeKeyword = "%" + keyword + "%";
		val total:Long = internalCountAll(likeKeyword).getOrElse(0L)
		val data = DB.withConnection("pay") { implicit connection =>
			SQL(
				"""
					SELECT
						Oid,
						Type,
						Content,
						StartTime,
						EndTime,
						InsertTime,
						IsActive,
						Version
					FROM Announcement
					WHERE Content LIKE {keyword}
					Order By InsertTime desc
					Limit {offset},{limit}
				"""
			).on(
				'keyword -> likeKeyword,
				'limit -> limit,
				'offset -> offset
			).as(announcements *)
		}

		new AnnouncementArray(total, data)
	}

	def countAll() = Future {
		internalCountAll()
	}

	def internalCountAll() = {
		DB.withConnection("pay") { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) c
					FROM Announcement;
				"""
			).fold[Long](0L){ (c, row) => c + row[Long](1) }

			result match {
				case Right(count) => Some(count)
				case Left(e) => None
			}
		}
	}

	def internalCountAll(keyword: String) = {
		DB.withConnection("pay") { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) c
					FROM Announcement p
					WHERE p.Content LIKE {keyword};
				"""
			).on(
				'keyword -> keyword
			).fold[Long](0L){ (c, row) => c + row[Long](1) }

			result match {
				case Right(count) => Some(count)
				case Left(e) => None
			}
		}
	}
}
