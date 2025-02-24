package com.pokeskies.skiesjoinmessages.config

import com.google.gson.*
import com.pokeskies.skiesjoinmessages.utils.Utils
import java.lang.reflect.Type

class FirstJoinConfig(
    val mode: Mode = Mode.STORAGE,
    val message: List<String> = emptyList(),
) {
    enum class Mode(val identifier: String) {
        STORAGE("storage"),
        STATS("stats"),
        HYBRID("hybrid");

        companion object {
            fun valueOfAnyCase(identifier: String): Mode? {
                for (type in Mode.entries) {
                    if (identifier.equals(type.identifier, true)) return type
                }
                return null
            }
        }

        internal class FirstJoinModeAdaptor : JsonSerializer<Mode>, JsonDeserializer<Mode> {
            override fun serialize(src: Mode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
                return JsonPrimitive(src.identifier)
            }

            override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Mode {
                val storageType = valueOfAnyCase(json.asString)

                if (storageType == null) {
                    Utils.printError("Could not deserialize First Join Mode '${json.asString}' using STORAGE as backup!")
                    return STORAGE
                }

                return storageType
            }
        }
    }

    override fun toString(): String {
        return "FirstJoinConfig(mode=$mode, message=$message)"
    }
}
