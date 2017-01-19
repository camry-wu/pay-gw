package models

import play.api._
import play.api.data._
import play.api.data.Forms._

import utils._

case class Message(name: String, content: String)

object Message {
	var list: List[Message] = Nil


	def post(name: String, content: String) {
		list :::= List(Message(name, content))
	}

	// 表单及验证
	val form = Form(tuple(
		"name" -> nonEmptyText,
		"content" -> nonEmptyText
	))
}
