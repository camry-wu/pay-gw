package models

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

import utils._
import models._
import AnormExtension._

case class User(
	oid			: Long,
	pubId		: String,
	openId		: String,
	openIdSrc	: String,
	mobile		: String,			// 添加第一个地址时顺便写入
	insertTime	: DateTime = now,
	lastModify	: DateTime = now,
	isActive	: Boolean,
	version		: Int
)

case class UserArray(total: Long, rows: Seq[User])

object UserArray {
	implicit val userArrayJsonWrites = new Writes[UserArray] {
		def writes(dataArr: UserArray) = Json.obj(
			"total" -> dataArr.total,
			"rows" -> dataArr.rows
		)
	}

	implicit val userArrayReads : Reads[UserArray] = (
		(__ \ "total").read[Long] and
		(__ \ "rows").read[Seq[User]]
	)(UserArray.apply _)
}

object User extends ((
	Long,
	String,
	String,
	String,
	String,
	DateTime,
	DateTime,
	Boolean,
	Int
) => User) {
	val logger = Logger(this.getClass())

	implicit val userJsonWrites: Writes[User] = (
		(__ \ "oid").write[Long] and
		(__ \ "pubId").write[String] and
		(__ \ "openId").write[String] and
		(__ \ "openIdSrc").write[String] and
		(__ \ "mobile").write[String] and
		(__ \ "insertTime").write[DateTime] and
		(__ \ "lastModify").write[DateTime] and
		(__ \ "isActive").write[Boolean] and
		(__ \ "version").write[Int]
	)(unlift(User.unapply))

	implicit val userJsonReads : Reads[User] = (
		(__ \ "oid").read[Long] and
		(__ \ "pubId").read[String] and
		(__ \ "openId").read[String] and
		(__ \ "openIdSrc").read[String] and
		(__ \ "mobile").read[String] and
		(__ \ "insertTime").read[DateTime] and
		(__ \ "lastModify").read[DateTime] and
		(__ \ "isActive").read[Boolean] and
		(__ \ "version").read[Int]
	)(User.apply _)

	implicit val jsonFormat = Json.format[User]

	val users =
		int("Oid") ~
    	str("PubId") ~
    	str("OpenId") ~
    	str("OpenIdSrc") ~
    	get[Option[String]]("Mobile") ~
    	date("InsertTime") ~
    	date("LastModify") ~
    	int("IsActive") ~
    	int("Version") map {
			case	oid~pubId~openId~openIdSrc~mobile~insertTime~lastModify~isActive~version =>
				User(oid,pubId,openId,openIdSrc,mobile.getOrElse(""),new DateTime(insertTime),new DateTime(lastModify),(isActive==1),version)
    	}

	def getById(oid:Long) = Future {
		DB.withConnection { implicit connection =>
			val ps = Seq[anorm.ParameterValue](oid)
			SQL(
				"""
					SELECT
						Oid,
						PubId,
						OpenId,
						OpenIdSrc,
						Mobile,
						InsertTime,
						LastModify,
						IsActive,
						Version
					FROM User
					WHERE Oid = {oid};
				"""
			).on(
				'oid -> ps(0)
			).as(users.singleOpt)
		}
	}

	def findByOpenId(openId:String) = Future {
		val likeOpenId = "%" + openId + "%";
		val total:Long = internalCountAll(likeOpenId).getOrElse(0L)
		val data = DB.withConnection { implicit connection =>
			SQL(
				"""
					SELECT
						Oid,
						PubId,
						OpenId,
						OpenIdSrc,
						Mobile,
						InsertTime,
						LastModify,
						IsActive,
						Version
					FROM User
					WHERE OpenId LIKE {openId};
				"""
			).on(
				'openId -> likeOpenId
			).as(users *)
		}

		new UserArray(total, data)
	}

	def list(limit:Int, offset: Int, openId: String) = Future {
		val likeOpenId = "%" + openId + "%";
		val total:Long = internalCountAll(likeOpenId).getOrElse(0L)
		logger.info("select [%s] count: %d".format(likeOpenId, total))
		val data = DB.withConnection { implicit connection =>
			SQL(
				"""
					SELECT
						Oid,
						PubId,
						OpenId,
						OpenIdSrc,
						Mobile,
						InsertTime,
						LastModify,
						IsActive,
						Version
					FROM User
					WHERE OpenId LIKE {openId}
					Order By InsertTime desc
					Limit {offset},{limit}
				"""
			).on(
				'openId -> likeOpenId,
				'limit -> limit,
				'offset -> offset
			).as(users *)
		}

		new UserArray(total, data)
	}

	def countAll() = Future {
		internalCountAll()
	}

	def internalCountAll() = {
		DB.withConnection { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) c
					FROM User u;
				"""
			).fold[Long](0L){ (c, row) => c + row[Long](1) }

			result match {
				case Right(count) => Some(count)
				case Left(e) => None
			}
		}
	}

	def internalCountAll(openId: String) = {
		DB.withConnection { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) as c
					FROM User u
					WHERE u.OpenId LIKE {openId};
				"""
			).on(
				'openId -> openId
			).fold[Long](0L){ (c, row) => c + row[Long](1) }

			result match {
				case Right(count) => { Some(count) }
				case Left(e) =>  { None }
			}
		}
	}

	// 注意，还要加入版本验证
	def update(oid:Long, mobile:String, career:String) = Future {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					UPDATE User
					SET
						Mobile = {mobile},
						Career = {career}
					WHERE Oid = {oid};
				"""
			).on(
				'oid -> oid,
				'mobile -> mobile,
				'career -> career
			).executeUpdate()
		}
	}

	def delete(oid:Long) = Future {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					DELETE
					FROM User
					WHERE Oid = {oid};
				"""
			).on(
				'oid -> oid
			).executeUpdate()
		}
	}

	def deleteAll() = Future {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					DELETE
					FROM User;
				"""
			).executeUpdate()
		}
	}

	// ????????????
	def resetAutoIncrement() = Future {
		DB.withConnection { implicit connection =>
			SQL(
				"""
					UPDATE sqlite_sequence
					SET seq = 0
					WHERE name = 'User';
				"""
			).executeUpdate()
		}
	}

	def wipeTable() = {
		for {
			del <- deleteAll()
			reset <- resetAutoIncrement()
		} yield {
			reset
		}
	}

	def create(openId:String, openIdSrc:String, career:String) = Future {
		val insertTime = now
		val lastModify = now
		val pubId = UUID.randomUUID.toString();

		DB.withConnection { implicit connection =>
			SQL(
				"""
					INSERT INTO User (
						PubId,
						OpenId,
						OpenIdSrc,
						Career,
						InsertTime,
						LastModify
					) VALUES (
						{pubId},
						{openId},
						{openIdSrc},
						{career},
						{insertTime},
						{lastModify}
					);
				"""
			).on(
				'pubId -> pubId,
				'openId -> openId,
				'openIdSrc -> openIdSrc,
				'career -> career,
				'insertTime -> insertTime,
				'lastModify -> lastModify
			).executeInsert()
		}
	}

}
