package me.reasonless.codeutilities.commands.misc;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.MinecraftColors;
import me.reasonless.codeutilities.util.TemplateJson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;

public class WebViewCommand {
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) {
		ItemStack stack = mc.player.getMainHandStack();
		CompoundTag tag = stack.getTag();

		try {
			CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
			String template = publicBukkitNBT.getString("hypercube:codetemplatedata");
			TemplateJson templateJson = new Gson().fromJson(template, TemplateJson.class);
			LiteralText text = new LiteralText(MinecraftColors.DARK_GREEN + " - " + MinecraftColors.GREEN + "To open this template in web-view click this message!");
			//text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.values()[0], String.format("https://derpystuff.gitlab.io/code/?template=%s", templateJson.code)));
			mc.player.sendMessage(text, false);

		}catch(NullPointerException e) {
			mc.player.sendMessage(new LiteralText(MinecraftColors.DARK_RED + " - " + MinecraftColors.RED + "The item you are holding is not a valid template."), false);
			return 1;
		}
		return 1;
	}


}
	
