package ru.yarsu

enum class Color(
    val strDFormat: String,
    val rgb: String,
) {
    BLACK("Чёрный", "#000000"),
    WHITE("Белый", "#FFFFFF"),
    RED("Красный", "#FF0000"),
    GREEN("Зелёный", "#00FF00"),
    BLUE("Синий", "#0000FF"),
    YELLOW("Желтый", "#FFFF00"),
    CYAN("Голубой", "#00FFFF"),
    MAGENTA("Пурпурный", "#FF00FF"),
    SILVER("Серебряный", "#C0C0C0"),
    GRAY("Серый", "#808080"),
    MAROON("Бордовый", "#800000"),
    OLIVE("Оливковый", "#808000"),
    DARKGREEN("Тёмно зелёный",	"#008000"),
    PURPLE("Фиолетовый", "#800080"),
    TEAL("Бирюзовый", "#008080"),
}
