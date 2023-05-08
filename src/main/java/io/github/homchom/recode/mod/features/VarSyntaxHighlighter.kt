package io.github.homchom.recode.mod.features

import com.google.gson.JsonParser
import io.github.homchom.recode.LegacyRecode
import io.github.homchom.recode.logError
import io.github.homchom.recode.server.CommandAliasGroup
import io.github.homchom.recode.sys.util.TextUtil
import io.github.homchom.recode.util.collections.verticalFlatten
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.regex.Pattern

// TODO: move to module, refactor and make better/faster, apply kotlin idioms
object VarSyntaxHighlighter {
    @JvmStatic
    val textPreviews = listOf(
        +CommandAliasGroup.RENAME,
        +CommandAliasGroup.ITEM_LORE_ADD,
        +CommandAliasGroup.ITEM_LORE_SET,
        listOf("relore"),
        +CommandAliasGroup.PLOT_NAME
    ).verticalFlatten().map { "/$it " }

    private val percentCodes = listOf(
        "%default",
        "%damager",
        "%killer",
        "%shooter",
        "%victim",
        "%projectile",
        "%uuid",
        "%selected",
        "%random(",
        "%round(",
        "%index(",
        "%entry(",
        "%var(",
        "%math("
    )

    @JvmStatic
    fun highlight(msg: String): Component? {
        var mutableMsg = msg
        val item = LegacyRecode.MC.player!!.mainHandItem
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
            mutableMsg = mutableMsg.replaceFirst("/variable".toRegex(), "/var")
        } else if (mutableMsg.startsWith("/number ")) {
            mutableMsg = mutableMsg.replaceFirst("/number".toRegex(), "/num")
        } else if (mutableMsg.startsWith("/text ")) {
            mutableMsg = mutableMsg.replaceFirst("/text".toRegex(), "/txt")
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
        return if (mutableMsg.startsWith("/var ") || mutableMsg.startsWith("/num ")
            || type == "var" || type == "num"
        ) {
            if (mutableMsg.startsWith("/num ")) {
                mutableMsg = mutableMsg.substring(5)
                if (!mutableMsg.contains("%") && !mutableMsg.matches(Regex("-?\\d*(\\.\\d*)?"))) {
                    return TextUtil.colorCodesToTextComponent("§cNot a valid number!")
                }
            } else if (mutableMsg.startsWith("/var ")) {
                mutableMsg = mutableMsg.substring(5)
            }
            val o = highlightString(mutableMsg)
            TextUtil.colorCodesToTextComponent("§bHighlighted:§r $o")
        } else if (mutableMsg.startsWith("/txt ") || type == "txt") {
            val p = Pattern.compile(
                "(&[a-f0-9klmnor]|&x&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9])",
                Pattern.CASE_INSENSITIVE
            )
            if (mutableMsg.startsWith("/txt ")) {
                mutableMsg = mutableMsg.substring(5)
            }
            mutableMsg = highlightString(mutableMsg)
            var lastIndex = 0
            val out = StringBuilder()
            val matcher = p.matcher(mutableMsg)
            while (matcher.find()) {
                out.append(mutableMsg, lastIndex, matcher.start())
                    .append(matcher.group().replace("&".toRegex(), "§"))
                lastIndex = matcher.end()
            }
            if (lastIndex < mutableMsg.length) {
                out.append(mutableMsg, lastIndex, mutableMsg.length)
            }
            TextUtil.colorCodesToTextComponent("§bPreview:§r $out")
        } else {
            null
        }
    }

    fun highlightString(msg: String): String {
        val percentm = Pattern.compile("%[a-zA-Z]+\\(?").matcher(msg)
        while (percentm.find()) {
            var valid = false
            for (code in percentCodes) {
                if (percentm.group().startsWith(code)) {
                    valid = true
                    break
                }
            }
            if (!valid) {
                return if (percentCodes.contains(percentm.group().replace("(", ""))) {
                    "§c" + percentm.group().replace("(", "") + " doesnt support brackets!"
                } else if (percentCodes.contains(percentm.group() + "(")) {
                    "§c" + percentm.group() + " needs brackets!"
                } else {
                    "§cInvalid Text Code: " + percentm.group().replace("(", ")")
                }
            }
        }
        val openb = StringUtils.countMatches(msg, "(")
        val closeb = StringUtils.countMatches(msg, ")")
        if (openb != closeb) {
            return "§cInvalid Brackets! $openb ( and $closeb )"
        }
        val o = StringBuilder()
        var depth = 0
        var percent = false
        var ptext: String? = ""
        for (c in msg.toCharArray()) {
            if (percent) ptext += c
            if (c == '%') {
                percent = true
                ptext = "%"
                depth++
                o.append(color(depth))
            } else if (c == '(') {
                if (!percent) {
                    depth++
                    o.append(color(depth))
                }
                o.append(c)
                depth++
                o.append(color(depth))
                percent = false
                continue
            } else if (c == ')') {
                depth--
                o.append(color(depth))
                o.append(c)
                depth--
                o.append(color(depth))
                percent = false
                continue
            } else {
                var valid = false
                for (code in percentCodes) {
                    if (code.startsWith(ptext!!)) {
                        valid = true
                        break
                    }
                }
                if (!valid) {
                    ptext = ""
                    percent = false
                    depth--
                    o.append(color(depth))
                }
            }
            o.append(c)
        }
        return o.toString()
    }

    private fun color(depth: Int): String {
        return Arrays.asList(
            "&x&f&f&f&f&f&f",
            "&x&f&f&d&6&0&0",
            "&x&3&3&f&f&0&0",
            "&x&0&0&f&f&e&0",
            "&x&5&e&7&7&f&7",
            "&x&c&a&6&4&f&a",
            "&x&f&f&4&2&4&2"
        )[(depth % 7 + 7) % 7].replace("&".toRegex(), "§")
        //complex bracket thing because apparently java's remainder can otherwise give negative nums
    }
}