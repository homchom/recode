package io.github.homchom.recode.mod.features

import com.google.gson.JsonParser
import io.github.homchom.recode.logError
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.CommandAliasGroup
import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.ui.ExpressionHighlighter
import io.github.homchom.recode.ui.text
import io.github.homchom.recode.util.Computation
import io.github.homchom.recode.util.collections.verticalFlatten
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items

// TODO: move, refactor and make better/faster, apply kotlin idioms
object VarSyntaxHighlighter {
    @JvmStatic
    val textPreviews = listOf(
        +CommandAliasGroup.RENAME,
        +CommandAliasGroup.ITEM_LORE_ADD,
        +CommandAliasGroup.ITEM_LORE_SET,
        listOf("relore"),
        +CommandAliasGroup.PLOT_NAME
    ).verticalFlatten().map { "/$it " }

    // TODO: decouple from mc.player (will fix timing issue)
    @JvmStatic
    fun highlight(msg: String): Component? {
        var mutableMsg = msg
        val item = mc.player!!.mainHandItem
        var type = ""
        try {
            if (item.item !== Items.AIR) {
                val vals = item.getOrCreateTagElement("PublicBukkitValues")
                if (vals.contains("hypercube:varitem")) {
                    val `var` = vals.getString("hypercube:varitem")
                    val json = JsonParser.parseString(`var`).asJsonObject
                    type = json["id"].asString
                }
            }
        } catch (e: Exception) {
            logError("Unknown variable syntax highlighting error")
            e.printStackTrace()
        }
        var doTagsAndCount = true
        if (mutableMsg.startsWith("/variable ")) {
            mutableMsg = mutableMsg.replaceFirst("/variable", "/var")
        } else if (mutableMsg.startsWith("/number ")) {
            mutableMsg = mutableMsg.replaceFirst("/number", "/num")
        } else if (mutableMsg.startsWith("/text ")) {
            mutableMsg = mutableMsg.replaceFirst("/text", "/txt")
        } else {
            for (preview in textPreviews) {
                var mutablePreview = preview
                val num = if (mutablePreview.endsWith("N")) {
                    mutablePreview = mutablePreview.replace("N", "")
                    true
                } else false
                if (mutableMsg.startsWith(mutablePreview)) {
                    doTagsAndCount = false
                    mutableMsg = mutableMsg.substring(mutablePreview.length)
                    mutableMsg = if (num) {
                        if (!mutableMsg.contains(" ")) return null
                        mutableMsg.substring(mutableMsg.indexOf(" "))
                    } else " $mutableMsg"
                    mutableMsg = "/txt$mutableMsg"
                    break
                }
            }
        }
        if (mutableMsg.startsWith("/") && doTagsAndCount) {
            if (mutableMsg.endsWith(" -l") || mutableMsg.endsWith(" -s") || mutableMsg.endsWith(" -g")) {
                mutableMsg = mutableMsg.substring(0, mutableMsg.length - 3)
            }
            val matchResult = Regex(".+( \\d+)").matchEntire(mutableMsg)
            if (matchResult != null) {
                mutableMsg = mutableMsg.removeRange(matchResult.groups[1]!!.range)
            }
        }

        val parseMiniMessage = when {
            mutableMsg.startsWith("/var ") || mutableMsg.startsWith("/num ")
                    || type == "var" || type == "num" -> false
            mutableMsg.startsWith("/txt ") || type == "txt" -> true
            else -> return null
        }
        val string = mutableMsg.substring(5)
        return when (val comp = ExpressionHighlighter.highlightString(string, parseMiniMessage)) {
            is Computation.Success -> comp()
            is Computation.Failure -> text {
                color(ColorPalette.RED) { literal(comp()) }
            }
        }
    }
}