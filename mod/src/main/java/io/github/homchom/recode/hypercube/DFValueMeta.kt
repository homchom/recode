@file:JvmName("DFValues")

package io.github.homchom.recode.hypercube

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import io.github.homchom.recode.game.getStringOrNull
import io.github.homchom.recode.game.lore
import io.github.homchom.recode.logError
import net.kyori.adventure.text.Component
import net.minecraft.world.item.ItemStack

fun ItemStack.dfValueMeta(): DFValueMeta? {
        val raw = tag?.publicBukkitValues
            ?.getStringOrNull("hypercube:varitem")
            ?: return null

        fun invalid(): Nothing? {
            logError("Invalid or unrecognized DF value item metadata: $raw")
            return null
        }

        // TODO: improve the following by using kotlinx.serialization
        // (if we decide to switch to FLK as an external dependency)

        val type: String
        val data: JsonObject
        try {
            val json = JsonParser.parseString(raw).asJsonObject
            type = json["id"]?.asString ?: return invalid()
            data = json["data"]?.asJsonObject ?: return invalid()
        } catch (e: JsonSyntaxException) {
            return invalid()
        } catch (e: IllegalStateException) {
            return invalid()
        }

        return when {
            data.has("name") && data.size() == 1 -> {
                val name = data["name"]?.asString ?: return invalid()
                DFValueMeta.Primitive(type, name)
            }
            type == "var" -> {
                val name = data["name"]?.asString ?: return invalid()
                if (!data.has("scope")) return invalid()
                DFValueMeta.Variable(name, lore().firstOrNull())
            }
            else -> DFValueMeta.Compound(type, data)
        }
    }

sealed interface DFValueMeta {
    val type: String

    data class Primitive(override val type: String, val expression: String) : DFValueMeta

    data class Variable(val expression: String, val scope: Component?) : DFValueMeta {
        override val type get() = "var"
    }

    data class Compound(override val type: String, val data: JsonObject) : DFValueMeta
}