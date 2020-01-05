package me.reasonless.codeutilities.commands.nbs;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;

import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import me.reasonless.codeutilities.util.TemplateNBT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

public class NBSPlayerCommand {
	public static int execute(MinecraftClient mc, CommandContext<CottonClientCommandSource> ctx) throws Exception {
		ItemStack stack = new ItemStack(Items.JUKEBOX);

		TemplateNBT.setTemplateNBTGZIP(stack, "Music Player", "Reasonless", "H4sIAAAAAAAAAO1XXUvDMBT9KyMgKOxhm+BDZQ9DnAq+beqDjBHT2AXTpCR3ujH6302yr7rYdh8d88GnNsm9N+eee9LbzNAbl+RDo+B1hliIgvkY1RfPAL2PBTFDrCJjZGyAxgtr8+Zm5l5DwJGxCzFgOysTYFKYlS7mmpoFuxygB127Z2FIhQ1JFibhVOCYkc1N07SONJeAgtZVOkiXsRGXOOxJEaG0noNZUxh+YlUOe260xGxQUBufighGZl4TmbgJSTDPwGm4jbNxxDj24zQyLk2XwCrjdj52RROKYU/oQgJ9ZBp2Ap8bSReEaXph8iXQV+O1Ajqcy6+aBVm7GWERuV1WxHSlusVk5PPhicFzWTOqMPmgloOQKUqMi4HiFAdTl8sy5HHkoxPOKipASSn9GsAE/EjtLHk/uetZrH06KSDjYD32Za8aRsqI/ZdkURmqUWR5MbcU5fVBojyMDiY0qHFMRUXHdCc2fm0VzXw27ihYZT5jbhR7JEKAuWB/gorWaalIGJDdev8Rubg8LRfrczI8MwbnViYXh3Oz1fHb+GHqJOZzGR6/8pUmWqalfXIkUoCSvDzH0l9SP4mCdsnsHWHZL/ssprUn4drACv0LduNNnPlN0jkUdUjC5fzmkNMid/c49TVhn4+vf3MYpN9bPSmotg0AAA==");
		stack.setCustomName(new LiteralText(ChatFormatting.DARK_PURPLE + "MUSIC PLAYER " + ChatFormatting.GRAY + " - " + ChatFormatting.WHITE + " Song Loader"));

		ItemStack stack2 = new ItemStack(Items.JUKEBOX);
		TemplateNBT.setTemplateNBTGZIP(stack, "", "", "H4sIAAAAAAAAAM1XXWvbMBT9K0YQaFgYSQZ7MORhrO02GKMs2fowStAsxRGVJSPJ2ULxf9+VHHd2HH8lzZan2Jbu0bnnfilP6CeXwaNG/o8nxAjys3c02v36aJWIAF6xCmET7DE02u2GJ/cls1oaHMI+gg22X2VsmBSwcou5prBgl330SXsfGSFUWMhgt4VsBY5YsH9omo6Q5tIgf/o2fUhzbBRzvJ1LEaJ0VMM5wJwva4gXgLjEpBlIU7PcYNXuf7Ypdx7cofBxQOgKJ9x4ljETVh4dyNguJULjDSUFH8eORBFTJFEVc1IwmThVnmWcncsPwxxYzh0wMf9XzBWNKTbtxA8eOQB3rjgVoVkPS4RLp3/xFiyiusBB4eCR2kMJUzQAC0hnl7Jm6xTYkaolzVYXljXjeu29mdfDcyFV1FB3UhgleU3VPR85NzL+2iJhR6gbQRrZB1xm3acL/dOqBKJF1aWXyUHmTGijkogK04t+C9bSVZ/tHcMG2EkFtn6ULFTyd5K841z+8j4zbbz3ayxCKOCCVLdS3eBgXVWoMlQqJi/XBk7Lp5gZ58CJAXEwx8biqESflnX9QI2N0nfMIXpn0krLBBpBH62025/DZPbQmQRW1LtWrjCzAPjT1+MR2kArsk+9xepUW3uKzamZW0ILm2YXo9hRSTo57NmdM6t1LXYpttyZncvBErM7d6lMSuOk80jqO4Kr4jY0PWav53nXszcV75tgptjs7rF73+dZ3+qcwX8bgyUqr2Z95vd5O27rPbee+YumyoAJQn9fafhrcg0rI+/N8HKTp3PMHtI/+1VogG0OAAA=");
		stack2.setCustomName(new LiteralText(ChatFormatting.DARK_PURPLE + "MUSIC PLAYER " + ChatFormatting.GRAY + " - " + ChatFormatting.WHITE + " Song Player"));

		mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);
		mc.interactionManager.clickCreativeStack(stack2, 35 + mc.player.inventory.selectedSlot);


		mc.player.sendMessage(new LiteralText(ChatFormatting.GOLD + " - " + ChatFormatting.YELLOW + "You have been given a music player."));
		mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_PURPLE + " - " + ChatFormatting.LIGHT_PURPLE + "Check our discord on how to use it!"));
		mc.player.sendMessage(new LiteralText(ChatFormatting.DARK_AQUA + " - " + ChatFormatting.AQUA + "" + ChatFormatting.UNDERLINE + "https://discord.gg/QBmXaGb"));

		return 1;
	}
}
