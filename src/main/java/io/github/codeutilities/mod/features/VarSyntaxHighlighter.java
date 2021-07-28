package io.github.codeutilities.mod.features;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.util.TextUtil;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

public class VarSyntaxHighlighter {

    public static Text highlight(String msg) {
        ItemStack item = CodeUtilities.MC.player.getMainHandStack();

        String type = "";

        try {
            if (item.getItem() != Items.AIR) {
                CompoundTag vals = item.getOrCreateSubTag("PublicBukkitValues");
                if (vals.contains("hypercube:varitem")) {
                    String var = vals.getString("hypercube:varitem");
                    JsonObject json = CodeUtilities.JSON_PARSER.parse(var).getAsJsonObject();
                    type = json.get("id").getAsString();
                }

            }
        } catch (Exception ignored) {
        }

        if (msg.startsWith("/var ") || msg.startsWith("/num ")
            || Objects.equals(type, "var") || Objects.equals(type, "num")) {

            if (msg.startsWith("/num ") || msg.startsWith("/var ")) {
                msg = msg.substring(5);
            }

            StringBuilder o = new StringBuilder();

            int depth = 0;
            boolean percent = false;

            for (char c : msg.toCharArray()) {
                if (c == '%') {
                    percent = true;
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
                    if (!(c+"").matches("\\w") && percent) {
                        percent = false;
                        depth--;
                        o.append(color(depth));
                    }
                }
                o.append(c);
            }

            return TextUtil.colorCodesToTextComponent("§bHighlighted:§r " + o);


        } else if (msg.startsWith("/txt ") || Objects.equals(type, "txt")) {
            Pattern p = Pattern.compile(
                "(&[a-f0-9klmnor]|&x&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9]&[a-f0-9])");
            if (msg.startsWith("/txt ")) {
                msg = msg.substring(5);
            }
            int lastIndex = 0;
            StringBuilder out = new StringBuilder();
            Matcher matcher = p.matcher(msg);
            while (matcher.find()) {
                out.append(msg, lastIndex, matcher.start())
                    .append(matcher.group().replaceAll("&","§"));

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

    private static String color(int depth) {
        return Arrays.asList(
            "&x&f&f&f&f&f&f",
            "&x&f&f&d&6&0&0",
            "&x&3&3&f&f&0&0",
            "&x&0&0&f&f&e&0",
            "&x&5&e&7&7&f&7",
            "&x&c&a&6&4&f&a",
            "&x&f&f&4&2&4&2"
        ).get(((depth % 7) + 7) % 7).replaceAll("&","§");
        //complex bracket thing because apparently java's remainder can otherwise give negative nums
    }

}
