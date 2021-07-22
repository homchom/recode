package io.github.codeutilities.mod.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import io.github.codeutilities.sys.util.misc.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

public class GiveCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
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

                            if (clipboard.startsWith("df")) {
                                clipboard = clipboard.substring(2);
                            }

                            if (clipboard.startsWith("give ")) {
                                clipboard = clipboard.substring(5);
                            }

                            if (clipboard.startsWith("@p ") || clipboard.startsWith("@s ")) {
                                clipboard = clipboard.substring(3);
                            }

                            this.sendChatMessage(mc, "/dfgive " + clipboard);
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
                    LiteralText text1 = new LiteralText("Maximum item count for ");
                    LiteralText text2 = new LiteralText(" is " + item.getMaxCount() + "!");
                    ChatUtil.sendMessage(text1.append(item.getName()).append(text2), ChatType.FAIL);
                }
            } else {
                ChatUtil.sendMessage("Minimum item count is 1!", ChatType.FAIL);
            }
        }
    }
}
