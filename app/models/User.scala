package models

case class User(oid: Long, pubId: String, openId: String, career: String)

case class UserResponse(currentPage: Int, totalPages: Int, data: List[User])

object UserResponse {
    var getUserResponse: UserResponse = null;

    var userList: List[User] = List()

    def saveUser(place: User) = {
        userList = userList ::: List(place)
    }
}
