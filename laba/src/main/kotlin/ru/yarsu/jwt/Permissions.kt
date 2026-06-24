package ru.yarsu.jwt

data class Permissions(
    val createNewTask: Boolean = false,
    val getInformationTask: Boolean = false,
    val editTask: Boolean = false,
    val workWithTasksAndCategoryGet: Boolean = false,
    val editAndDeleteCategory: Boolean = false,
    val getAllUsers: Boolean = false,
    val deleteUser: Boolean = false,
    val category: Boolean = false,
) {
    companion object {
        val USER =
            Permissions(
                createNewTask = true,
                getInformationTask = true,
                editTask = true,
                workWithTasksAndCategoryGet = true,
                editAndDeleteCategory = true,
                deleteUser = true,
            )
        val CATEGORY_MANAGER =
            Permissions(
                createNewTask = true,
                getInformationTask = true,
                editTask = true,
                workWithTasksAndCategoryGet = true,
                editAndDeleteCategory = true,
                deleteUser = true,
                category = true,
            )
        val USER_MANAGER =
            Permissions(
                getInformationTask = true,
                editTask = true,
                editAndDeleteCategory = true,
                getAllUsers = true,
                deleteUser = true,
            )
        val UNKNOWN =
            Permissions()
    }
}
