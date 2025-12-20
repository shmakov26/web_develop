package ru.yarsu

enum class Role(
    val nameRussia: String,
    val altName: String,
) {
    USER("Зарегистрированный пользователь", "User"),
    CATEGORYMANAGER("Менеджер категорий", "CategoryManager"),
    USERMANAGER("Менеджер пользователей приложения", "UserManager"),
}

fun parsStrToRole(str: String): Role =
    when (str) {
        "User" -> Role.USER
        "CategoryManager" -> Role.CATEGORYMANAGER
        "UserManager" -> Role.USERMANAGER
        else -> throw IllegalArgumentException(
            "Поле Role передано некорректно, ожидается роль из списка."
        )
    }
