package io.github.homchom.recode.mod.commands.impl.text

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.brigadier.CommandDispatcher
import io.github.homchom.recode.mod.commands.Command
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.sys.player.chat.ChatType
import io.github.homchom.recode.sys.player.chat.ChatUtil
import io.github.homchom.recode.util.item.getDFValueItemData
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandBuildContext
import net.minecraft.world.item.Items

private const val COMMAND_NAME = "copyval"
private const val MUST_HOLD_AIR_ITEM = "You need to hold an item that is not air."
private const val MUST_USE_VALUE_ITEM = "You need to hold a value item, such as a location."
private const val INVALID_VALUE_ITEM_DATA = "The held item's value data is malformed. ('data' tag invalid)"
private const val INVALID_VALUE_ITEM_ID = "The held item's value data is malformed. ('id' tag invalid)"
private const val UNSUPPORTED_VALUE_ITEM = "This type of value item is unsupported for this command."
private const val SUCCESSFULLY_COPIED = "Value copied to clipboard!"

// Numbers are stored as strings normally, this checks if a number string is a literal value or not
private val NUMBER_LITERAL_REGEX = Regex("\\d+(?:.\\d{0,3})?|.\\d{1,3}")

/**
 * This command will copy text to your clipboard based on the held DF Value.
 * The value will be converted into text using a user-defined format in the mod config.
 * This is primarily meant to be used for Text -> DiamondFire languages, allowing easy copying of locations in particular.
 */
class CopyValueCommand : Command() {
    override fun register(
        mc: Minecraft,
        cd: CommandDispatcher<FabricClientCommandSource>,
        context: CommandBuildContext
    ) {
        cd.register(ArgBuilder.literal(COMMAND_NAME).executes cmd@{ _ ->
            val heldItem = mc.player!!.mainHandItem

            // Disallow air items entirely
            if (heldItem.item == Items.AIR) {
                ChatUtil.sendMessage(MUST_HOLD_AIR_ITEM, ChatType.FAIL)
                return@cmd -1
            }

            // Get valueitem data or error if it doesn't exist
            val compound = heldItem.getDFValueItemData()
            if (compound == null) {
                ChatUtil.sendMessage(MUST_USE_VALUE_ITEM, ChatType.FAIL)
                return@cmd -1
            }

            // Technically .asString can return "12" and whatnot for a number tag, but that doesn't matter in this case.
            val id = compound.get("id")?.asJsonPrimitive?.asString
            val result = when (id) {
                // Location item
                "loc" -> {
                    val dataObject = (compound.get("data") as? JsonObject)?.get("loc") as? JsonObject
                    val x = (dataObject?.get("x") as? JsonPrimitive)?.asDouble
                    val y = (dataObject?.get("y") as? JsonPrimitive)?.asDouble
                    val z = (dataObject?.get("z") as? JsonPrimitive)?.asDouble
                    val pitch = (dataObject?.get("pitch") as? JsonPrimitive)?.asDouble
                    val yaw = (dataObject?.get("yaw") as? JsonPrimitive)?.asDouble

                    if (dataObject != null && x != null && y != null && z != null && pitch != null && yaw != null) {
                        val expression = Config.getString("itemTextFormatLocation") ?: "[%f, %f, %f, %f, %f]"
                        expression.format(x, y, z, pitch, yaw)
                    } else {
                        null
                    }
                }

                // Vector item
                "vec" -> {
                    val dataObject = compound.get("data") as? JsonObject
                    val x = (dataObject?.get("x") as? JsonPrimitive)?.asDouble
                    val y = (dataObject?.get("y") as? JsonPrimitive)?.asDouble
                    val z = (dataObject?.get("z") as? JsonPrimitive)?.asDouble

                    if (dataObject != null && x != null && y != null && z != null) {
                        val expression = Config.getString("itemTextFormatVector") ?: "<%f, %f, %f>"
                        expression.format(x, y, z)
                    } else {
                        null
                    }
                }

                // String
                "txt" -> {
                    val dataObject = compound.get("data") as? JsonObject
                    val name = (dataObject?.get("name") as? JsonPrimitive)?.asString

                    if (dataObject != null && name != null) {
                        // TODO Could be replaced with a more powerful thing? (Such as a list of regex post-process replacement rules? Feels overkill)
                        val delimiter = Config.getString("itemTextFormatStringDelimiter") ?: "\""
                        val escape = Config.getString("itemTextFormatStringEscape") ?: "\\"
                        val expression = Config.getString("itemTextFormatString") ?: "\"%s\""

                        if (delimiter.isNotEmpty() && escape.isNotEmpty()) {
                            val newName = name
                                .replace(escape, "$escape$escape")
                                .replace(delimiter, "$escape$delimiter")

                            expression.format(newName)
                        } else {
                            expression.format(name)
                        }
                    } else {
                        null
                    }
                }

                // Styled Text
                "comp" -> {
                    val dataObject = compound.get("data") as? JsonObject
                    val name = (dataObject?.get("name") as? JsonPrimitive)?.asString

                    if (dataObject != null && name != null) {
                        val delimiter = Config.getString("itemTextFormatTextDelimiter") ?: "\""
                        val escape = Config.getString("itemTextFormatTextEscape") ?: "\\"
                        val expression = Config.getString("itemTextFormatText") ?: "T\"%s\""

                        if (delimiter.isNotEmpty() && escape.isNotEmpty()) {
                            val newName = name
                                .replace(escape, "$escape$escape")
                                .replace(delimiter, "$escape$delimiter")

                            expression.format(newName)
                        } else {
                            expression.format(name)
                        }
                    } else {
                        null
                    }
                }

                // Number
                "num" -> {
                    val dataObject = compound.get("data") as? JsonObject
                    val name = (dataObject?.get("name") as? JsonPrimitive)?.asString // Yes, asString

                    if (dataObject != null && name != null) {
                        val expression = if (NUMBER_LITERAL_REGEX.matches(name)) {
                            Config.getString("itemTextFormatNumberLiteral") ?: "%s"
                        } else {
                            Config.getString("itemTextFormatNumber") ?: "%s"
                        }

                        expression.format(name)
                    } else {
                        null
                    }
                }

                // General
                null -> {
                    ChatUtil.sendMessage(INVALID_VALUE_ITEM_ID, ChatType.FAIL)
                    return@cmd -1
                }

                else -> {
                    ChatUtil.sendMessage(UNSUPPORTED_VALUE_ITEM, ChatType.FAIL)
                    return@cmd -1
                }
            }

            if (result != null) {
                mc.keyboardHandler.clipboard = result
                ChatUtil.sendMessage(SUCCESSFULLY_COPIED, ChatType.SUCCESS)
                return@cmd 1
            }

            ChatUtil.sendMessage(INVALID_VALUE_ITEM_DATA, ChatType.FAIL)
            return@cmd -1
        })
    }

    override fun getDescription(): String {
        return """
            [blue]/$COMMAND_NAME [text][reset]
            
            Copies the held DF value item to clipboard using the format defined in the mod config.
            """.trimIndent()
    }

    override fun getName() = "/$COMMAND_NAME"

}