package io.github.codeutilities.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TemplateUtils {

    public static void rawTemplateNBT(ItemStack stack, String name, String author, String codeData) {
        CompoundTag publicBukkitNBT = new CompoundTag();
        CompoundTag itemNBT = new CompoundTag();
        CompoundTag codeNBT = new CompoundTag();

        codeNBT.putString("name", name);
        codeNBT.putString("author", author);
        codeNBT.putString("code", codeData);
        codeNBT.putInt("version", 1);

        // Apply the template data to the item.
        publicBukkitNBT.putString("hypercube:codetemplatedata", codeNBT.toString());

        // Assign the bukkit container to the item. (Contains the template data)
        itemNBT.put("PublicBukkitValues", publicBukkitNBT);
        stack.setTag(itemNBT);

    }


    public static void compressTemplateNBT(ItemStack stack, String name, String author, String template) {
        try {
            byte[] b64 = CompressionUtil.toBase64(CompressionUtil.toGZIP(template.getBytes(StandardCharsets.UTF_8)));
            String exported = new String(b64);
            rawTemplateNBT(stack, name, author, exported);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JsonObject fromItemStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
        String template = publicBukkitNBT.getString("hypercube:codetemplatedata");
        return new JsonParser().parse(template).getAsJsonObject();
    }

    public static boolean isTemplate(ItemStack stack) {
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
