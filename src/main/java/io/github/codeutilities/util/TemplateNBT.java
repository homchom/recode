package io.github.codeutilities.util;

import java.nio.charset.StandardCharsets;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class TemplateNBT {

    public static void setTemplateNBT(ItemStack stack, String name, String author,
        String template) {
        try {
            byte[] b64 = GzFormat
                .encryptBase64(GzFormat.compress(template.getBytes(StandardCharsets.UTF_8)));
            String exported = new String(b64);
            final CompoundTag nbt = new CompoundTag();

            nbt.putString("author", author);
            nbt.putString("name", name);
            nbt.putInt("version", 1);
            nbt.putString("code", exported);

            final CompoundTag itemNbt = new CompoundTag();
            final CompoundTag publicBukkitNbt = new CompoundTag();

            publicBukkitNbt.putString("hypercube:codetemplatedata", nbt.toString());
            itemNbt.put("PublicBukkitValues", publicBukkitNbt);

            stack.setTag(itemNbt);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setTemplateNBTGZIP(ItemStack stack, String name, String author,
        String template) {
        try {
            final CompoundTag nbt = new CompoundTag();

            nbt.putString("author", author);
            nbt.putString("name", name);
            nbt.putInt("version", 1);
            nbt.putString("code", template);

            final CompoundTag itemNbt = new CompoundTag();
            final CompoundTag publicBukkitNbt = new CompoundTag();

            publicBukkitNbt.putString("hypercube:codetemplatedata", nbt.toString());
            itemNbt.put("PublicBukkitValues", publicBukkitNbt);

            stack.setTag(itemNbt);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
