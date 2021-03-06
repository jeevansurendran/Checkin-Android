package com.checkin.app.checkin.data

import android.util.Log
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.objectbox.converter.PropertyConverter
import org.json.JSONObject
import java.io.IOException


object Converters {
    val objectMapper = jacksonObjectMapper().apply {
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Serialize Date object to ISO-8601 standard
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unknown properties when deserializing data
    }

    private val TAG = Converters::class.java.simpleName

    fun getJsonNode(json: String): JsonNode? {
        try {
            return objectMapper.readTree(json)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    fun <T> getObjectFromJson(json: String, typeReference: TypeReference<T>): T? = try {
        objectMapper.readValue<T>(json, typeReference)
    } catch (exception: IOException) {
        Log.e(TAG, "JSON invalid! $json", exception)
        null
    }

    fun <T> getObjectFromJson(json: String, typeReference: JavaType): T? = try {
        objectMapper.readValue<T>(json, typeReference)
    } catch (exception: IOException) {
        Log.e(TAG, "JSON invalid! $json", exception)
        null
    }

    class ListConverter<T> : PropertyConverter<List<T>, String> {
        override fun convertToEntityProperty(databaseValue: String): List<T>? {
            val type = object : TypeReference<List<T>>() {}
            return getObjectFromJson(databaseValue, type)
        }

        override fun convertToDatabaseValue(entityProperty: List<T>): String {
            try {
                return objectMapper.writeValueAsString(entityProperty)
            } catch (e: JsonProcessingException) {
                e.printStackTrace()
            }
            return ""
        }
    }

    class JsonObjectSerializer : JsonSerializer<JSONObject?>() {
        override fun serialize(value: JSONObject?, gen: JsonGenerator?, serializers: SerializerProvider?) {
            gen?.writeRawValue(value?.toString())
        }
    }
}
