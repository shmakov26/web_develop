package ru.yarsu

import java.util.UUID

class WorkFlowWithCategory(
    private val categoryData: List<Category>,
) {
    fun getCategoryByUUID(uuid: UUID): Category? = categoryData.find { it.id == uuid }

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
                    ),
                )
            },
        )
        return totalSortedFilteredCategoryList
    }
}
