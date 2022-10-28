package io.github.homchom.recode.mod.features;

import com.google.gson.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.*;

public class VarSyntaxHighlighter {

    private static final List<String> percentcodes = Arrays.asList(
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
    );

    public static final List<String> txtPreviews = Arrays.asList(
        "/lore add ",
        "/addlore ",
        "/relore ",
        "/rename ",
        "/lore set N",
        "/i lore set N",
        "/i lore add ",
        "/item lore set N",
        "/item lore add ",
        "/ils N",
        "/sll N",
        "/p name ",
        "/plot name "
    );

    public static Component highlight(String msg) {
        ItemStack item = LegacyRecode.MC.player.getMainHandItem();

        String type = "";

        try {
            if (item.getItem() != Items.AIR) {
                CompoundTag vals = item.getOrCreateTagElement("PublicBukkitValues");
                if (vals.contains("hypercube:varitem")) {
                    String var = vals.getString("hypercube:varitem");
                    JsonObject json = JsonParser.parseString(var).getAsJsonObject();
                    type = json.get("id").getAsString();
                }

            }
        } catch (Exception ignored) {
        }

        boolean doTagsAndCount = true;

        if (msg.startsWith("/variable ")) {
            msg = msg.replaceFirst("/variable", "/var");
        } else
        if (msg.startsWith("/number ")) {
            msg = msg.replaceFirst("/number", "/num");
        } else
        if (msg.startsWith("/text ")) {
            msg = msg.replaceFirst("/text", "/txt");
        } else {

            for (String o : VarSyntaxHighlighter.txtPreviews) {
                boolean num = false;
                if (o.endsWith("N")) {
                    o = o.replace("N","");
                    num = true;
                }
                if (msg.startsWith(o)) {
                    doTagsAndCount = false;
                    msg = msg.substring(o.length());
                    if (num) {
                        if (!msg.contains(" ")) return null;
                        msg = msg.substring(msg.indexOf(" "));
                    } else msg = " " + msg;
                    msg = "/txt" + msg;
                    break;
                }
            }
        }

        if (msg.startsWith("/") && doTagsAndCount) {
            if (msg.endsWith(" -l") || msg.endsWith(" -s") || msg.endsWith(" -g")) {
                msg = msg.substring(0,msg.length()-3);
            }
            if (msg.matches(".+( \\d+)")) {
                Matcher m = Pattern.compile(".+( \\d+)").matcher(msg);
                m.find();
                msg = msg.substring(0,msg.length()-m.group(1).length());
            }
        }

        if (msg.startsWith("/var ") || msg.startsWith("/num ")
            || Objects.equals(type, "var") || Objects.equals(type, "num")) {

            if (msg.startsWith("/num ")) {
                msg = msg.substring(5);
                if (!msg.contains("%") && !msg.matches("-?\\d*(\\.\\d*)?")) {
                    return TextUtil.colorCodesToTextComponent("§cNot a valid number!");
                }
            } else if (msg.startsWith("/var ")) {
                msg = msg.substring(5);
            }

            String o = highlightString(msg);

            return TextUtil.colorCodesToTextComponent("§bHighlighted:§r " + o);


        } else if (msg.startsWith("/txt ") || Objects.equals(type, "txt")) {
            Pattern p = Pattern.compile(
                    "(&[a-f0-9klmnor]|&x&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9])",
                    Pattern.CASE_INSENSITIVE);
            if (msg.startsWith("/txt ")) {
                msg = msg.substring(5);
            }

            msg = highlightString(msg);

            int lastIndex = 0;
            StringBuilder out = new StringBuilder();
            Matcher matcher = p.matcher(msg);
            while (matcher.find()) {
                out.append(msg, lastIndex, matcher.start())
                    .append(matcher.group().replaceAll("&", "§"));

                lastIndex = matcher.end();
            }
            if (lastIndex < msg.length()) {
                out.append(msg, lastIndex, msg.length());
            }
            return TextUtil.colorCodesToTextComponent("§bPreview:§r " + out);
        } else {
            return null;
        }
    }

    public static String highlightString(String msg) {
        Matcher percentm = Pattern.compile("%[a-zA-Z]+\\(?").matcher(msg);

        while (percentm.find()) {
            boolean valid = false;

            for (String code : percentcodes) {
                if (percentm.group().startsWith(code)) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                if (percentcodes.contains(percentm.group().replace("(", ""))) {
                    return "§c" + percentm.group().replace("(", "") + " doesnt support brackets!";
                } else if (percentcodes.contains(percentm.group() + "(")) {
                    return "§c" + percentm.group() + " needs brackets!";
                } else {
                    return "§cInvalid Text Code: " + percentm.group().replace("(",")");
                }
            }
        }

        int openb = StringUtils.countMatches(msg, "(");
        int closeb = StringUtils.countMatches(msg, ")");

        if (openb != closeb) {
            return "§cInvalid Brackets! " + openb + " ( and " + closeb + " )";
        }

        StringBuilder o = new StringBuilder();

        int depth = 0;
        boolean percent = false;
        String ptext = "";

        for (char c : msg.toCharArray()) {
            if (percent) ptext+=c;
            if (c == '%') {
                percent = true;
                ptext="%";
                depth++;
                o.append(color(depth));
            } else if (c == '(') {
                if (!percent) {
                    depth++;
                    o.append(color(depth));
                }
                o.append(c);
                depth++;
                o.append(color(depth));
                percent = false;
                continue;
            } else if (c == ')') {
                depth--;
                o.append(color(depth));
                o.append(c);
                depth--;
                o.append(color(depth));
                percent = false;
                continue;
            } else {
                boolean valid = false;
                for (String code : percentcodes) {
                    if (code.startsWith(ptext)) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    ptext = "";
                    percent = false;
                    depth--;
                    o.append(color(depth));
                }
            }
            o.append(c);
        }

        return o.toString();
    }

    private static String color(int depth) {
        return Arrays.asList(
            "&x&f&f&f&f&f&f",
            "&x&f&f&d&6&0&0",
            "&x&3&3&f&f&0&0",
            "&x&0&0&f&f&e&0",
            "&x&5&e&7&7&f&7",
            "&x&c&a&6&4&f&a",
            "&x&f&f&4&2&4&2"
        ).get(((depth % 7) + 7) % 7).replaceAll("&", "§");
        //complex bracket thing because apparently java's remainder can otherwise give negative nums
    }

}
