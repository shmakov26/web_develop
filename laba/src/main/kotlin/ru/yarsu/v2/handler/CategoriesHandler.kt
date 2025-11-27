package ru.yarsu.v2.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.Category
import ru.yarsu.CategoryList
import ru.yarsu.Color
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithCategory
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.parsRgbToColor
import ru.yarsu.v2.jsonResponseLens
import ru.yarsu.v2.serializers.CategoryListSerializer
import ru.yarsu.v2.utils.putCategory
import ru.yarsu.v2.utils.validateBodyCategory
import java.util.UUID
import kotlin.collections.isNotEmpty

class CategoryHandler(
    private val categoryList: List<Category>,
    private val userList: List<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categorySerializer = CategoryListSerializer()
        val category = mutableListOf<CategoryList>()
        val sortedList = WorkFlowWithCategory(categoryList).getSortedCategoryList()
        for (item in sortedList) {
            category.add(
                CategoryList(
                    id = item.id,
                    description = item.description,
                    color = item.color,
                    owner = item.owner,
                    ownerName = (userList.find { it.id == item.owner }?.login ?: "Общая"),
                ),
            )
        }
        return Response(Status.OK)
            .contentType(ContentType.APPLICATION_JSON)
            .body(categorySerializer.categoryList(category))
    }
}

class PutCategory(
    private var categoryList: MutableList<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categoryId: String = request.path("category-id") ?: return Response(Status.BAD_REQUEST)
        val jsonResponse = jsonResponseLens<Any>()

        val description = request.form("Description")
        val colorStr = request.form("Color")

        val errors = validateBodyCategory(description, colorStr)
        if (errors.isNotEmpty()) {
            return jsonResponse.invoke(errors, Status.BAD_REQUEST)
        }

        var color: Color? = null
        if (colorStr != null) {
            color =
                if (colorStr.contains("#")) {
                    parsRgbToColor(colorStr)
                } else {
                    Color.valueOf(colorStr)
                }
        }

        val workFlowWithCategory = WorkFlowWithCategory(categoryList)

        val uuidCategory: UUID =
            UUID.fromString(
                categoryId,
            )
        try {
            val category = workFlowWithCategory.getCategoryByUUID(uuidCategory)

            val index = categoryList.indexOfFirst { it.id == category.id }

            val newCategory = putCategory(category, description!!, color ?: category.color) // сформировали новый элемент категории

            categoryList[index] = newCategory

            return Response(Status.NO_CONTENT)
        } catch (e: NullPointerException) {
            return jsonResponse.invoke(
                mutableMapOf("CategoryId" to uuidCategory.toString(), "Error" to e.message.toString()),
                Status.NOT_FOUND,
            )
        }
    }
}

class DeleteCategory(
    private val taskList: MutableList<TaskModel>,
    private val categoryList: MutableList<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categoryId: String = request.path("category-id") ?: return Response(Status.BAD_REQUEST)

        val jsonResponse = jsonResponseLens<Any>()

        val workFlowWithCategory = WorkFlowWithCategory(categoryList)

        try {
            val uuidCategory = UUID.fromString(categoryId)

            val category = workFlowWithCategory.getCategoryByUUID(uuidCategory)

            categoryList.removeIf { it.id == category.id }
            taskList.removeIf { it.category == uuidCategory }

            return Response(Status.NO_CONTENT)
        } catch (e: NullPointerException) {
            return jsonResponse.invoke(mutableMapOf("CategoryId" to categoryId, "Error" to e.message.toString()), Status.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            return jsonResponse.invoke(
                mutableMapOf(
                    "Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено $categoryId",
                ),
                Status.BAD_REQUEST,
            )
        }
    }
}
