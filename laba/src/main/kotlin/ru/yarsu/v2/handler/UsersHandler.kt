package ru.yarsu.v2.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType
import ru.yarsu.User
import ru.yarsu.v2.serializers.UsersSerializer

class UsersHandler(
    var userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val usersSerializer = UsersSerializer()
        val totalUser = mutableListOf<User>()

        val sortedListUser = userList.sortedWith(compareBy<User> { it.login })
        for (users in sortedListUser) {
            totalUser.add(
                User(
                    id = users.id,
                    login = users.login,
                    registrationDateTime = users.registrationDateTime,
                    email = users.email,
                ),
            )
        }
        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(usersSerializer.usersList(totalUser))
    }
}
