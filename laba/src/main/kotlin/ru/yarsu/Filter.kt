package ru.yarsu

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestKey
import org.http4k.lens.bearerToken
import ru.yarsu.jwt.JwtTools
import ru.yarsu.jwt.Permissions
import ru.yarsu.jwt.UserAssignRoleOperation
import ru.yarsu.jwt.UserContext
import ru.yarsu.v3.jsonResponseLens

val userContextLens = RequestKey.required<UserContext>("user-context")

val permissionsLens = RequestKey.required<Permissions>("permissions")

fun authenticationFilter(
    jwtTools: JwtTools,
    userLookup: (login: String) -> User?,
): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            val token = request.bearerToken()
            if (token == null) {
                next(userContextLens(UserContext(null), request))
            } else {
                val decodedToken = jwtTools.validateJWTToken(token)
                if (decodedToken == null) {
                    next(userContextLens(UserContext(null), request))
                } else {
                    val login = decodedToken.subject
                    val endTime = decodedToken.expiresAt
                    if (login == null) {
                        next(userContextLens(UserContext(null), request))
                    } else if (endTime == null) {
                        next(userContextLens(UserContext(null), request))
                    } else {
                        val user = userLookup(login)
                        if (user == null) {
                            next(userContextLens(UserContext(null), request))
                        } else {
                            next(userContextLens(UserContext(user), request))
                        }
                    }
                }
            }
        }
    }

fun assignPermissionsFilter(userAssignRoleOperation: UserAssignRoleOperation): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            val userContext = userContextLens(request)
            val permissions = userAssignRoleOperation.getUserRole(userContext.user)
            next(request.with(permissionsLens of permissions))
        }
    }

fun requirePermission(check: (Permissions) -> Boolean): Filter =
    Filter { next: HttpHandler ->
        { request: Request ->
            val permissions = permissionsLens(request)

            if (check(permissions)) {
                next(request)
            } else {
//                val jsonResponse = jsonResponseLens<Map<String, String>>()
                Response(Status.UNAUTHORIZED)
            }
        }
    }
