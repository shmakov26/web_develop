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

fun parsRgbToColor(rgb: String): Color =
    when (rgb) {
        "#000000" -> Color.BLACK
        "#FFFFFF" -> Color.WHITE
        "#FF0000" -> Color.RED
        "#00FF00" -> Color.GREEN
        "#0000FF" -> Color.BLUE
        "#FFFF00" -> Color.YELLOW
        "#00FFFF" -> Color.CYAN
        "#FF00FF" -> Color.MAGENTA
        "#C0C0C0" -> Color.SILVER
        "#808080" -> Color.GRAY
        "#800000" -> Color.MAROON
        "#808000" -> Color.OLIVE
        "#008000" -> Color.DARKGREEN
        "#800080" -> Color.PURPLE
        "#008080" -> Color.TEAL
        else -> throw IllegalArgumentException(
            "Поле Color передано некорректно, ожидается цвет или RGB из списка.",
        )
    }
