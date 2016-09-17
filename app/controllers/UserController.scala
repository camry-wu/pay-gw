package controllers

import javax.inject.Inject

import scala.collection.mutable.ListBuffer;

import anorm._
import play.api.db.DB;

import play.api.libs.json.Json;
import play.api.libs.json.JsPath;
import play.api.libs.json.Writes;

import play.api.mvc.{Action, Controller}
import play.api.templates.Html
import models._

//@Singleton
class UserController @Inject()(db: Database) extends Controller {
    implicit val userWrites: Writes[User] = (
        (JsPath \ "oid").write[Int] and
        (JsPath \ "pubId").write[String] and
        (JsPath \ "openId").write[String] and
        (JsPath \ "career").write[String]
    ) (unlift(User:unapply))

    implicit val userResponseWriters: Writes[UserResponse] = (
        (JsPath \ "currentPage").write[Int] and
        (JsPath \ "totalPages").write[Int] and
        (JsPath \ "data").write[List[User]]
    ) (unlift(UserResponse:unapply))

    def user = Action {
        Ok(views.html.user("play-angular-bootstrap-demo"));
    }

    def list = Action {
        var list = new ListBuffer[User]()
        UserResponse.saveUser(new User(1, "", "openId", "carrer"))
        /*
        val conn = db.getConnection()
        try {
            val stmt = conn.createStatement
            val rs = stmt.executeQuery("select * from user where id < 4")
            while (rs.next()) {
                val openId: String = rs.getString("openId")
                var oid: Int = rs.getInt("oid")
                var user = new User(oid, "", openId, "")
                UserResponse.saveUser(user)
            }
        } finally {
            conn.close();
        }
        */

        UserResponse.getUserResponse = new UserResponse(1, 10, UserResponse.userList)
        val json = Json.toJson(UserResponse.getUserResponse)
        Ok(json)
    }

    def listJson = Action {
        val json = Json.toJson(Place.list)
        Ok(json)
    }
}
