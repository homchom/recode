package io.github.codeutilities.commands.util;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.TemplateJson;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;

public class WebviewCommand {
	static MinecraftClient mc = MinecraftClient.getInstance();

	public static void run() {
		if (mc.player.isCreative()) {
			ItemStack item = mc.player.getMainHandStack();
			if (item.getItem() != Items.AIR) {
				try {
                    CompoundTag tag = item.getTag();
					CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
					String template = publicBukkitNBT.getString("hypercube:codetemplatedata");
					TemplateJson templateJson = new Gson().fromJson(template, TemplateJson.class);
					LiteralText text = new LiteralText("§b&nClick this message to view this code template in web!");
					text.styled((style) -> {return style.withClickEvent(new ClickEvent(ClickEvent.Action.values()[0], String.format("https://derpystuff.gitlab.io/code/?template=%s", templateJson.code)));});
					mc.player.sendMessage(text, false);
				}catch (NullPointerException e) {
					CodeUtilities.chat("§cThe item you are holding is not a code template!");
				}
			}else {
				CodeUtilities.chat("§cYou have to hold an item in your hand!");
			}
		}else {
			CodeUtilities.chat("§cYou need to be in creative mode to use this command!");
		}
	}

	public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
		cd.register(ArgumentBuilders.literal("webview")
			.executes(ctx -> {
				try {
					run();
					return 1;
				}catch (Exception err) {
					CodeUtilities.chat("§cError while executing command.");
					err.printStackTrace();
					return -1;
				}
			})
		);
	}
}
