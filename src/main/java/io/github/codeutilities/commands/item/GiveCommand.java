package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

public class GiveCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("give")
                .then(ArgBuilder.argument("item", ItemStackArgumentType.itemStack())
                        .then(ArgBuilder.argument("count", IntegerArgumentType.integer(1, 127))
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

                            this.sendChatMessage(mc, "/give " + clipboard);
                            return 1;
                        })
                )
        );
    }

    private void giveItem(MinecraftClient mc, ItemStack item, int count) {
        item.setCount(count);
        if (this.isCreative(mc)) {
            if (count >= 1) {
                if (count <= item.getMaxCount()) {
                    ItemUtil.giveCreativeItem(item, true);
                } else {
                    ChatUtil.sendMessage("Maximum item count for " + item.getName() + " is " + item.getMaxCount() + "!", ChatType.FAIL);
                }
            } else {
                ChatUtil.sendMessage("Minimum item count is 1!", ChatType.FAIL);
            }
        }
    }
}
