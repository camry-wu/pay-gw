import anorm._
import anorm.SqlParser._

import org.joda.time._
import org.joda.time.format._

package object models {

	type Date = java.util.Date

	def DB = play.api.db.DB

	def Logger = play.api.Logger

	implicit def current = play.api.Play.current

	implicit def global = scala.concurrent.ExecutionContext.Implicits.global

	type DateTime = org.joda.time.DateTime

	import java.util.TimeZone

	import java.text.SimpleDateFormat

	val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

	def now = new DateTime(utc(new Date))

	def utc(date:Date) = {
		val tz = TimeZone.getDefault()
		var ret = new Date( date.getTime() - tz.getRawOffset() )

		// if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
		if ( tz.inDaylightTime( ret )){
			val dstDate = new Date( ret.getTime() - tz.getDSTSavings() )

			// check to make sure we have not crossed back into standard time
			// this happens when we are on the cusp of DST (7pm the day before the change for PDT)
			if ( tz.inDaylightTime( dstDate )){
				ret = dstDate
			}
		}

		ret
	}

	def asDateTime(date:Date) =
		new DateTime(date)

	def asDateTime(date:Option[Date]) = 
		if(date.isDefined)
			Some(new DateTime(date.get))
		else
			None

	def asDate(date:DateTime) = 
		date.toDate

	def asDate(date:Option[DateTime]) = 
		if(date.isDefined)
			Some(date.get.toDate)
		else
			None

	object AnormExtension {
		val dateFormatGeneration: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmssSS");

		implicit def rowToDateTime: Column[DateTime] = Column.nonNull1 { (value, meta) =>
			val MetaDataItem(qualified, nullable, clazz) = meta
				value match {
					case l: java.lang.Long => Right(new DateTime(l))
					case ts: java.sql.Timestamp => Right(new DateTime(ts.getTime))
					case d: java.sql.Date => Right(new DateTime(d.getTime))
					case str: java.lang.String => Right(dateFormatGeneration.parseDateTime(str))  
					case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass) )
				}
		}

		implicit val dateTimeToStatement = new ToStatement[DateTime] {
			def set(s: java.sql.PreparedStatement, index: Int, aValue: DateTime): Unit = {
				s.setTimestamp(index, new java.sql.Timestamp(aValue.withMillisOfSecond(0).getMillis()) )
			}
		}

		implicit def rowToString: Column[String] = Column.nonNull1 { (value, meta) =>
			val MetaDataItem(qualified, nullable, clazz) = meta
				value match {
					case d: java.lang.Long => Right("%d".format(d))
					case str: java.lang.String => Right("%s".format(str))
					case _ => Left(TypeDoesNotMatch("Cannot convert " + value + ":" + value.asInstanceOf[AnyRef].getClass + " to String for column " + qualified) )
				}
		}
	}
}
