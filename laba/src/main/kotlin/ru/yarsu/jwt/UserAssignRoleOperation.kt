package ru.yarsu.jwt

import ru.yarsu.User

class UserAssignRoleOperation {
    fun getUserRole(user: User?): Permissions {
        if (user == null) {
            return Permissions.UNKNOWN
        }
        val role = user.role
        return when (role.ordinal) {
            0 -> {
                Permissions.USER
            }
            1 -> {
                Permissions.CATEGORY_MANAGER
            }
            else -> {
                Permissions.USER_MANAGER
            }
        }
    }
}
