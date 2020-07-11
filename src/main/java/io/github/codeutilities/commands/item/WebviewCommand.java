package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.CodeUtilities;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class WebviewCommand {
	static MinecraftClient mc = MinecraftClient.getInstance();

	public static void run() {
		
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
