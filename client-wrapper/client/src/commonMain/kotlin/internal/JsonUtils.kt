package qbittorrent.internal

import kotlinx.serialization.json.*

internal val emptyArray = buildJsonArray {}

internal fun MutableMap<String, JsonElement>.resetRemoved(key: String) {
    put("${key}_removed", emptyArray)
}

internal fun MutableMap<String, JsonElement>.dropRemoved(key: String) {
    val removeKeys = get("${key}_removed").toStringList()
    if (removeKeys.isNotEmpty()) {
        val items = checkNotNull(get(key)).jsonObject.toMutableMap()
        removeKeys.forEach(items::remove)
        put(key, JsonObject(items))
    }
}

internal fun MutableMap<String, JsonElement>.dropRemovedStrings(key: String) {
    val removeKeys = get("${key}_removed").toStringList()
    if (removeKeys.isNotEmpty()) {
        val tags =
            checkNotNull(get(key))
                .jsonArray
                .map { it.jsonPrimitive.content }
                .filterNot(removeKeys::contains)
                .toMutableList()
        put(key, JsonArray(tags.map(::JsonPrimitive)))
    }
}

internal fun MutableMap<String, JsonElement>.merge(
    newJson: JsonObject,
    nestedObjectKeys: List<String>,
): MutableMap<String, JsonElement> {
    forEach { (key, currentValue) ->
        val update =
            when (val newValue = newJson[key] ?: return@forEach) {
                is JsonPrimitive,
                is JsonArray -> newValue
                is JsonObject -> {
                    val actualObject =
                        (if (currentValue is JsonNull) newValue else currentValue).mutateJson()
                    if (nestedObjectKeys.contains(key)) {
                        (newValue.keys - actualObject.keys).forEach { newHash ->
                            actualObject[newHash] = checkNotNull(newValue[newHash]).jsonObject
                        }
                    }
                    JsonObject(actualObject.merge(newValue.jsonObject, emptyList()))
                }
            }

        put(key, update)
    }
    return this
}

internal fun JsonElement?.toStringList(): List<String> {
    return this?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty()
}

internal fun JsonElement?.mutateJson(): MutableMap<String, JsonElement> {
    return this?.jsonObject?.toMutableMap() ?: mutableMapOf()
}
