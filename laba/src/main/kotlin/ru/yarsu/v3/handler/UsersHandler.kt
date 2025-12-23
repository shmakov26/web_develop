package ru.yarsu.v3.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.Category
import ru.yarsu.ShowUser
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithUsers
import ru.yarsu.jwt.Permissions
import ru.yarsu.permissionsLens
import ru.yarsu.userContextLens
import ru.yarsu.v3.jsonResponseLens
import ru.yarsu.v3.serializers.UsersSerializer
import java.util.UUID

class UsersHandler(
    var userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val usersSerializer = UsersSerializer()
        val totalUser = mutableListOf<ShowUser>()

        val sortedListUser = userList.sortedWith(compareBy<User> { it.login })
        for (users in sortedListUser) {
            totalUser.add(
                ShowUser(
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

class DeleteUser(
    private val taskList: MutableList<TaskModel>,
    private val categoryList: MutableList<Category>,
    private val userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val user = userContextLens(request).user ?: return Response(Status.UNAUTHORIZED)
        val permissions = permissionsLens(request)

        val userId: String = request.path("user-id") ?: return Response(Status.BAD_REQUEST)

        val workFlowWithUsers = WorkFlowWithUsers(userList)

        val jsonResponse = jsonResponseLens<Any>()
        try {
            val uuidUser = UUID.fromString(userId)
            val user1 = workFlowWithUsers.getUserByUUID(uuidUser)
            if ((user.id != user1.id) && (permissions != Permissions.USER_MANAGER)) {
                return Response(Status.UNAUTHORIZED)
            }

            val tasks =
                taskList
                    .filter { it.author == uuidUser }
                    .sortedWith(compareBy { it.id })

            val categories =
                categoryList
                    .filter { it.owner == uuidUser }
                    .sortedWith(compareBy { it.id })

            if ((tasks.isNotEmpty()) && (categories.isNotEmpty())) {
                return jsonResponse.invoke(
                    mutableMapOf(
                        "Tasks" to
                            tasks.map {
                                mapOf(
                                    "Id" to it.id,
                                    "Title" to it.title,
                                )
                            },
                        "Categories" to
                            categories.map {
                                mapOf(
                                    "Id" to it.id,
                                    "Description" to it.description,
                                )
                            },
                    ),
                    Status.FORBIDDEN,
                )
            }
//            else if ((tasks.isEmpty()) && (categories.isNotEmpty())) {
//                return jsonResponse.invoke(
//                    mutableMapOf(
//                        "Categories" to
//                            categories.map {
//                                mapOf(
//                                    "Id" to it.id,
//                                    "Description" to it.description,
//                                )
//                            },
//                    ),
//                    Status.FORBIDDEN,
//                )
//            } else if (tasks.isNotEmpty()) {
//                return jsonResponse.invoke(
//                    mutableMapOf(
//                        "Tasks" to
//                            tasks.map {
//                                mapOf(
//                                    "Id" to it.id,
//                                    "Title" to it.title,
//                                )
//                            },
//                    ),
//                    Status.FORBIDDEN,
//                )
//            }
            else {
                userList.removeIf { it.id == uuidUser }
            }

            return Response(Status.NO_CONTENT)
        } catch (e: NullPointerException) {
            return jsonResponse.invoke(mutableMapOf("UserId" to userId, "Error" to e.message.toString()), Status.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            return jsonResponse.invoke(
                mutableMapOf(
                    "Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено $userId",
                ),
                Status.BAD_REQUEST,
            )
        }
    }
}
