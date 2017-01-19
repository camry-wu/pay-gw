import anorm._
import anorm.SqlParser._
import utils._

import org.joda.time._
import org.joda.time.format._
import play.api.libs.json._

package object models {

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
