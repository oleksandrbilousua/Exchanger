package com.bilous.exchanger.util

import com.bilous.exchanger.model.Rates
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class RatesSerializer : JsonTransformingSerializer<Rates>(Rates.serializer()) {

    override fun transformDeserialize(element: JsonElement): JsonElement {
        element as? JsonObject ?: error("RatesSerializer applicable only for objects")

        val array =  buildJsonArray {
            element.forEach { entry ->
               add( buildJsonObject {
                    put("currency",entry.key)
                    put("rate", entry.value)
                })
            }
        }
        return buildJsonObject {
            put("list", array)
        }
    }
}