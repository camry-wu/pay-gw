
package models.conf

import java.util.UUID

import org.joda.time.format._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import anorm._
import anorm.ParameterValue
import anorm.ParameterValue._
import anorm.SqlParser._
import scala.concurrent.Future

import utils._
import models._
import AnormExtension._

/**
 * 公告表，并且也保存配置修改后的最新统一版本.
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

/**
 * 公告列表，用于 json 转换.
 */
case class AnnouncementArray(total: Long, rows: Seq[Announcement])

/**
 * 公告列表伴生对象，提供隐式参数.
 */
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

/**
 * Announcement 伴生对象，提供缺省的 CRUD 操作.
 */
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

	// 隐式参数，将 Announcement 转成 json 格式
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

	// 隐式参数，将 json 转成 Announcement 格式
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

	// 隐式参数
	implicit val jsonFormat = Json.format[Announcement]

	// 从数据库 rs 中解析出 Announcement 对象
	val announcements =
		int("Oid") ~
    	int("Type") ~
    	str("Content") ~
    	date("StartTime") ~
    	date("EndTime") ~
    	date("InsertTime") ~
    	int("IsActive") ~
    	int("Version") map {
			case oid~annType~content~startTime~endTime~insertTime~isActive~version =>
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

	/**
	 * 根据 keyword 查询公告列表.
	 */
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

	/**
	 * 查询全部公告数量.
	 */
	def countAll() = Future {
		internalCountAll()
	}

	/**
	 * 查询全部公告数量.
	 */
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

	/**
	 * 查询符合 keyword 查询条件的公告数量.
	 */
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

	/**
	 * 创建一个新的公告.
	 */
	def create(ann: Announcement) = Future {
		val insertTime = now

		DB.withConnection("pay") { implicit connection =>
			SQL(
				"""
					INSERT INTO Announcement (
						Type,
						Content,
						StartTime,
						EndTime,
						InsertTime,
						IsActive,
						Version
					) VALUES (
						{annType},
						{content},
						{startTime},
						{endTime},
						{insertTime},
						1,
						1
					);
				"""
			).on(
				'annType -> ann.annType,
				'content -> ann.content,
				'startTime -> ann.startTime.getOrElse(null),
				'endTime -> ann.endTime.getOrElse(null),
				'insertTime -> insertTime
			).executeInsert()
		}
	}
}
