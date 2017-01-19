
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

import utils._
import models._
import models.AnormExtension._

/**
 * 渠道配置表.
 */
case class BizChannel (

	/**
	 * 主键.
	 */
	oid				: Long,

	/**
	 * 渠道号.
	 */
	channelId		: String,

	/**
	 * 渠道名称.
	 */
	channelName		: String,

	/**
	 * 渠道域名.
	 */
	channelDomain	: String,

	/**
	 * 渠道内网IP.
	 */
	chaInternalIP	: String,

	/**
	 * 渠道公网IP.
	 */
	chaInternetIP	: String,

	/**
	 * 子渠道号.
	 */
	subChannelId	: String,


	/**
	 * 子渠道名称.
	 */
	subChannelName	: String,

	/**
	 * 应用号.
	 */
	appId			: String,

	/**
	 * 应用名称.
	 */
	appName			: String,

	/**
	 * 应用登录key.
	 */
	appLoginKey		: String,

	/**
	 * 应用登录秘钥.
	 */
	appLoginSecretKey	: String,

	/**
	 * 秘钥.
	 */
	secretKey		: String,

	/**
	 * 插入时间.
	 */
	insertTime		: Option[DateTime],

	/**
	 * 最后修改时间.
	 */
	lastModify		: Option[DateTime],

	/**
	 * 逻辑删除标志位.
	 */
	isActive		: Boolean,

	/**
	 * 版本.
	 */
	version			: Int
)

case class BizChannelArray(total: Long, rows: Seq[BizChannel])

object BizChannelArray {
	implicit val payChannelConfArrayJsonWrites = new Writes[BizChannelArray] {
		def writes(dataArr: BizChannelArray) = Json.obj(
			"total" -> dataArr.total,
			"rows" -> dataArr.rows
		)
	}

	implicit val payChannelConfArrayReads : Reads[BizChannelArray] = (
		(__ \ "total").read[Long] and
		(__ \ "rows").read[Seq[BizChannel]]
	)(BizChannelArray.apply _)
}

object BizChannel extends ((
	Long,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	String,
	Option[DateTime],
	Option[DateTime],
	Boolean,
	Int
) => BizChannel) {
	val logger = Logger(this.getClass())

	implicit val payChannelConfJsonWrites: Writes[BizChannel] = (
		(__ \ "oid").write[Long] and
		(__ \ "channelId").write[String] and
		(__ \ "channelName").write[String] and
		(__ \ "channelDomain").write[String] and
		(__ \ "chaInternalIP").write[String] and
		(__ \ "chaInternetIP").write[String] and
		(__ \ "subChannelId").write[String] and
		(__ \ "subChannelName").write[String] and
		(__ \ "appId").write[String] and
		(__ \ "appName").write[String] and
		(__ \ "appLoginKey").write[String] and
		(__ \ "appLoginSecretKey").write[String] and
		(__ \ "secretKey").write[String] and
		(__ \ "insertTime").write[Option[DateTime]] and
		(__ \ "lastModify").write[Option[DateTime]] and
		(__ \ "isActive").write[Boolean] and
		(__ \ "version").write[Int]
	)(unlift(BizChannel.unapply))

	implicit val payChannelConfJsonReads : Reads[BizChannel] = (
		(__ \ "oid").read[Long] and
		(__ \ "channelId").read[String] and
		(__ \ "channelName").read[String] and
		(__ \ "channelDomain").read[String] and
		(__ \ "chaInternalIP").read[String] and
		(__ \ "chaInternetIP").read[String] and
		(__ \ "subChannelId").read[String] and
		(__ \ "subChannelName").read[String] and
		(__ \ "appId").read[String] and
		(__ \ "appName").read[String] and
		(__ \ "appLoginKey").read[String] and
		(__ \ "appLoginSecretKey").read[String] and
		(__ \ "secretKey").read[String] and
		(__ \ "insertTime").read[Option[DateTime]] and
		(__ \ "lastModify").read[Option[DateTime]] and
		(__ \ "isActive").read[Boolean] and
		(__ \ "version").read[Int]
	)(BizChannel.apply _)

	implicit val jsonFormat = Json.format[BizChannel]

	val payChannelConfs =
		int("Oid") ~
    	str("ChannelId") ~
    	str("ChannelName") ~
    	get[Option[String]]("ChannelDomain") ~
    	get[Option[String]]("ChaInternalIP") ~
    	get[Option[String]]("ChaInternetIP") ~
    	get[Option[String]]("SubChannelId") ~
    	get[Option[String]]("SubChannelName") ~
    	get[Option[String]]("AppId") ~
    	get[Option[String]]("AppName") ~
    	get[Option[String]]("AppLoginKey") ~
    	get[Option[String]]("AppLoginSecretKey") ~
    	get[Option[String]]("SecretKey") ~
    	date("InsertTime") ~
    	date("LastModify") ~
    	int("IsActive") ~
    	int("Version") map {
			case	oid~channelId~channelName~channelDomain~chaInternalIP~chaInternetIP~subChannelId~subChannelName~appId~appName~appLoginKey~appLoginSecretKey~secretKey~insertTime~lastModify~isActive~version =>
				BizChannel(
					oid,
					channelId,
					channelName,
					channelDomain.getOrElse(""),
					chaInternalIP.getOrElse(""),
					chaInternetIP.getOrElse(""),
					subChannelId.getOrElse(""),
					subChannelName.getOrElse(""),
					appId.getOrElse(""),
					appName.getOrElse(""),
					appLoginKey.getOrElse(""),
					appLoginSecretKey.getOrElse(""),
					secretKey.getOrElse(""),
					Option(new DateTime(insertTime)),
					Option(new DateTime(lastModify)),
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
						ChannelId,
						ChannelName,
						ChannelDomain,
						ChaInternalIP,
						ChaInternetIP,
						SubChannelId,
						SubChannelName,
						AppId,
						AppName,
						AppLoginKey,
						AppLoginSecretKey,
						SecretKey,
						InsertTime,
						LastModify,
						IsActive,
						Version
					FROM BizChannel
					WHERE ChannelId LIKE {keyword}
					OR ChannelName LIKE {keyword}
					Order By LastModify desc
					Limit {offset},{limit}
				"""
			).on(
				'keyword -> likeKeyword,
				'limit -> limit,
				'offset -> offset
			).as(payChannelConfs *)
		}

		new BizChannelArray(total, data)
	}

	def countAll() = Future {
		internalCountAll()
	}

	def internalCountAll() = {
		DB.withConnection("pay") { implicit connection =>
			val result = SQL(
				"""
					SELECT COUNT(1) c
					FROM BizChannel;
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
					FROM BizChannel p
					WHERE p.ChannelId LIKE {keyword}
					OR p.ChannelName LIKE {keyword};
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

/*
public class BizChannel implements Serializable {
    // 对应的支付方式配置表列表
    private List<BizChannelPayMethod> list		: 
    
    public List<BizChannelPayMethod> getPayMethodList() {
        return list		: 
    }

    public void setPayMethodList(List<BizChannelPayMethod> list) {
        this.list = list		: 
    }
}
*/
