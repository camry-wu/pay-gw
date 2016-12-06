package models

import java.util.UUID

import play.api.libs.json._

import anorm._
import anorm.ParameterValue
import anorm.ParameterValue._
import anorm.SqlParser._
import scala.concurrent.Future

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
		/*
		val users = List[User] (
			new User(1L, "pubId", "openId", "openIdSrc", "mobile", now, now, true, 1),
			new User(2L, "pubId-2", "openId-2", "openIdSrc", "mobile-2", now, now, true, 1)
		);
		users
		*/
		DB.withConnection { implicit connection =>
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
					WHERE OpenId LIKE '%' || {openId} || '%';
				"""
			).on(
				'openId -> openId
			).as(users *)
		}
	}

	def countAll() = Future {
		DB.withConnection { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) count
					FROM User u;
				"""
			).apply()

			try {
				Some(result.head[Long]("count"))
			} catch {
				case e:Exception => None
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
