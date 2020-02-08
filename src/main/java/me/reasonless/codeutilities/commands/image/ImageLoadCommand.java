package me.reasonless.codeutilities.commands.image;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.images.ImageConverter;
import me.reasonless.codeutilities.images.ImageToTemplate;
import me.reasonless.codeutilities.util.MinecraftColors;
import me.reasonless.codeutilities.util.TemplateNBT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.io.File;

public class ImageLoadCommand {
    public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) throws Exception {
        File f = new File("CodeUtilities/Images/" + StringArgumentType.getString(ctx, "location") + (StringArgumentType.getString(ctx, "location").endsWith(".png") ? "" : ".nbs"));

        if(f.exists()) {
            String[] strings = ImageConverter.convert(f);
            for (String string : strings) {
                mc.player.sendMessage(new LiteralText(string));
            }

            ImageToTemplate toTemplate = new ImageToTemplate(ImageConverter.convert(f), StringArgumentType.getString(ctx, "location"));

            ItemStack stack = new ItemStack(Items.NOTE_BLOCK);
            TemplateNBT.setTemplateNBT(stack, StringArgumentType.getString(ctx, "location"), mc.player.getName().asString(), toTemplate.convert());
            mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);

            mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_GREEN + " - " + MinecraftColors.GREEN + "Image loaded! Change the first Set Variable to the location!"));
        }else {
            mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "That image doesn't exist."));
        }
        return 1;
    }
}
