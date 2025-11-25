package ru.yarsu.v2.serializers

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ru.yarsu.TaskForListImportance
import java.io.StringWriter

class ListTimeSerializer : BaseSerializer() {
    fun listTimeSerialize(list: List<TaskForListImportance>): String {
        val stringWriter = StringWriter()
        val mapper = jacksonObjectMapper()
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        mapper
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setDefaultPropertyInclusion(
                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS),
            ).writer(printer)
            .writeValue(stringWriter, list)
        return stringWriter.toString()
    }
}
