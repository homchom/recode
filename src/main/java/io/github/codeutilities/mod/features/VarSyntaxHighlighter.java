package io.github.codeutilities.mod.features;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.util.TextUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

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
        "%var(",
        "%math("
    );

    public static final List<String> txtPreviews = Arrays.asList(
        "/lore add ",
        "/addlore ",
        "/rename ",
        "/lore set N",
        "/i lore set N",
        "/i lore add ",
        "/item lore set N",
        "/item lore add ",
        "/ils N",
        "/sll N"
    );

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

            Matcher percentm = Pattern.compile("%[a-zA-Z]+\\(?").matcher(msg);

            while (percentm.find()) {
                if (!percentcodes.contains(percentm.group())) {
                    if (percentcodes.contains(percentm.group().replace("(", ""))) {
                        return TextUtil.colorCodesToTextComponent(
                            "§c" + percentm.group().replace("(", "") + " doesnt support brackets!");
                    } else if (percentcodes.contains(percentm.group() + "(")) {
                        return TextUtil.colorCodesToTextComponent(
                            "§c" + percentm.group() + " needs brackets!");
                    } else {
                        return TextUtil.colorCodesToTextComponent(
                            "§cInvalid Text Code: " + percentm.group().replace("(",")"));
                    }
                }
            }

            int openb = StringUtils.countMatches(msg, "(");
            int closeb = StringUtils.countMatches(msg, ")");

            if (openb != closeb) {
                return TextUtil.colorCodesToTextComponent(
                    "§cInvalid Brackets! " + openb + " ( and " + closeb + " )");
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
                    if (!(c + "").matches("[a-zA-Z]") && percent) {
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
