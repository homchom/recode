package me.reasonless.codeutilities.commands.misc;

import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.MinecraftColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;


public class UnpackCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
        ItemStack stack = mc.player.getMainHandStack();


        CompoundTag tag = stack.getTag();

        if(tag == null) {
            mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "The item you are holding is not a valid item!"));
            return 1;
        }

        CompoundTag shulkerTag = tag.getCompound("BlockEntityTag");

        if(shulkerTag == null) {
            mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "The item you are holding is not a valid item!"));
            return 1;
        }

        System.out.println(shulkerTag.getKeys());

        CompoundTag items = shulkerTag.getCompound("Items");

        System.out.println(shulkerTag.getList("Items", 0));

        return 1;
    }
}
