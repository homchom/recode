package io.github.homchom.recode.sys.hypercube.templates;

import com.google.gson.JsonObject;
import io.github.homchom.recode.Recode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateUtils {

    public static final int VERSION = 1;

    public static void applyRawTemplateNBT(ItemStack stack, String name, String author, String codeData) {
        applyRawTemplateNBT(stack, new TextComponent(name), author, codeData, VERSION);
    }

    public static void applyRawTemplateNBT(ItemStack stack, Component name, String author, String codeData, int version) {
        CompoundTag publicBukkitNBT = new CompoundTag();
        CompoundTag itemNBT = new CompoundTag();
        CompoundTag codeNBT = new CompoundTag();

        codeNBT.putString("name", name.getContents());
        codeNBT.putString("author", author);
        codeNBT.putString("code", codeData);
        codeNBT.putInt("version", version);

        // Apply the template data to the item.
        publicBukkitNBT.putString("hypercube:codetemplatedata", codeNBT.toString());

        // Assign the bukkit container to the item. (Contains the template data)
        itemNBT.put("PublicBukkitValues", publicBukkitNBT);
        stack.setTag(itemNBT);
        stack.setHoverName(name);
    }


    public static void compressTemplateNBT(ItemStack stack, String name, String author, String template) {
        try {
            byte[] b64 = CompressionUtil.toBase64(CompressionUtil.toGZIP(template.getBytes(StandardCharsets.UTF_8)));
            String exported = new String(b64);
            applyRawTemplateNBT(stack, name, author, exported);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JsonObject fromItemStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
        String template = publicBukkitNBT.getString("hypercube:codetemplatedata");
        return Recode.JSON_PARSER.parse(template).getAsJsonObject();
    }

    public static boolean isTemplate(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return false;
        }

        CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
        if (publicBukkitNBT == null) {
            return false;
        }

        return publicBukkitNBT.getString("hypercube:codetemplatedata").length() > 0;
    }
}
