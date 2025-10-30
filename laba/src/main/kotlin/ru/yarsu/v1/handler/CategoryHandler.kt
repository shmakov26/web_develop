package ru.yarsu.v1.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.findSingle
import org.http4k.core.queries
import org.http4k.lens.contentType
import ru.yarsu.Category
import ru.yarsu.WorkFlowWithCategory
import ru.yarsu.pagination
import ru.yarsu.v1.serializers.CategorySerializer

class CategoryHandler(
    private val categoryList: List<Category>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val workFlowWithCategory = WorkFlowWithCategory(categoryList)

        val categoryListSerializer = CategorySerializer()
        try {
            val result: List<Category> = workFlowWithCategory.getSortedCategoryList()
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(categoryListSerializer.categoryList(result))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(categoryListSerializer.serializeError(e.message.toString()))
        }
    }
}
