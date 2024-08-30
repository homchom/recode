package io.github.homchom.recode.sys.networking.websocket.client.type;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;

public class NbtItem extends SocketItem {

    @Override
    public String getIdentifier() {
        return "nbt";
    }

    @Override
    public ItemStack getItem(String data) throws Exception {
        ItemStack stack;
        try {
            stack = ItemStack.of(new TagParser(new StringReader(data)).readStruct());
        } catch (RuntimeException | CommandSyntaxException e) {
            throw new IOException("Failed to parse provided NBT data.");
        }

        return stack;
    }
}
