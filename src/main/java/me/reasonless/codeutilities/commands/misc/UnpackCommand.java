package me.reasonless.codeutilities.commands.misc;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;


public class UnpackCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        ItemStack stack = mc.player.getMainHandStack();


        CompoundTag tag = stack.getTag();

        if(tag == null) {
            mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_RED + " - " + ChatFormatting.RED + "The item you are holding is not a valid item!"));
            return 1;
        }

        CompoundTag shulkerTag = tag.getCompound("BlockEntityTag");

        if(shulkerTag == null) {
            mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_RED + " - " + ChatFormatting.RED + "The item you are holding is not a valid item!"));
            return 1;
        }

        System.out.println(shulkerTag.getKeys());

        CompoundTag items = shulkerTag.getCompound("Items");

        System.out.println(shulkerTag.getList("Items", 0));

        return 1;
    }
}
