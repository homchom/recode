package me.reasonless.codeutilities.commands;

import com.google.gson.Gson;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
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
			LiteralText text = new LiteralText(ChatFormatting.DARK_GREEN + " - " + ChatFormatting.GREEN + "To open this template in web-view click this message!");
			text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.field_11749, String.format("https://derpystuff.gitlab.io/code/?template=%s", templateJson.code)));
			mc.player.sendMessage(text);

		}catch(NullPointerException e) {
			mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_RED + " - " + ChatFormatting.RED + "The item you are holding is not a valid template."));
			return 1;
		}
		return 1;
	}


}
