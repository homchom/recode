package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.*;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.*;
import net.minecraft.item.ItemStack;

public class GiveCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("dfgive")
                .then(ArgBuilder.argument("item", ItemStackArgumentType.itemStack())
                        .then(ArgBuilder.argument("count", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    giveItem(mc, ctx.getArgument("item", ItemStackArgument.class)
                                                    .createStack(1, false),
                                            ctx.getArgument("count", Integer.class));
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            giveItem(mc, ctx.getArgument("item", ItemStackArgument.class)
                                    .createStack(1, false), 1);
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("clipboard")
                        .executes(ctx -> {
                            String clipboard;
                            try {
                                clipboard = mc.keyboard.getClipboard();
                            } catch (Exception e) {
                                ChatUtil.sendMessage("Unable to get Clipboard", ChatType.FAIL);
                                return -1;
                            }
                            if (clipboard.startsWith("/")) {
                                clipboard = clipboard.substring(1);
                            }

                            if (clipboard.startsWith("give ")) {
                                clipboard = clipboard.substring(5);
                            }

                            if (clipboard.startsWith("@p ") || clipboard.startsWith("@s ")) {
                                clipboard = clipboard.substring(3);
                            }

                            mc.player.sendChatMessage("/give " + clipboard);
                            return 1;
                        })
                )
        );
    }

    private void giveItem(MinecraftClient mc, ItemStack item, int count) {
        item.setCount(count);
        if (mc.player.isCreative()) {
            if (count >= 1) {
                if (count <= item.getMaxCount()) {
                    ItemUtil.giveCreativeItem(item, true);
                } else {
                    ChatUtil.sendMessage("Maximum item count for " + item.getName() + " is " + item.getMaxCount() + "!", ChatType.FAIL);
                }
            } else {
                ChatUtil.sendMessage("Minimum item count is 1!", ChatType.FAIL);
            }
        } else {
            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
        }
    }
}
