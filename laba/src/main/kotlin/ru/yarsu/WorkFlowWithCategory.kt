package ru.yarsu

import java.util.UUID

class WorkFlowWithCategory(
    private val categoryData: List<Category>,
) {
    fun getCategoryByUUID(uuid: UUID): Category {
        val categoryBy = categoryData.find { it.id == uuid }
        if (categoryBy == null) {
            throw NullPointerException("Категория не найдена")
        }
        return categoryBy
    }

    fun getSortedCategoryList(): List<Category> {
        val sortedFilteredCategory =
            categoryData.sortedWith(
                compareBy<Category> { it.description }.thenBy { it.id },
            )

        val totalSortedFilteredCategoryList = mutableListOf<Category>()

        sortedFilteredCategory.forEach(
            { category ->
                totalSortedFilteredCategoryList.add(
                    Category(
                        id = category.id,
                        description = category.description,
                        color = category.color,
                        owner = category.owner,
                    ),
                )
            },
        )
        return totalSortedFilteredCategoryList
    }
}
