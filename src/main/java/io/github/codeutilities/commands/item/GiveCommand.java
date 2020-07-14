package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

public class GiveCommand {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static void run(ItemStack item, int count) {
        item.setCount(count);
        assert mc.player != null;
        if (mc.player.isCreative()) {
            if (count >= 1) {
                if (count <= item.getMaxCount()) {
                    CodeUtilities.giveCreativeItem(item);
                } else {
                    CodeUtilities.chat(
                        "Maximum item count for " + item.getName() + "is " + item.getMaxCount()
                            + "!", ChatType.FAIL);
                }
            } else {
                CodeUtilities.chat("Minimum item count is 1!", ChatType.FAIL);
            }
        } else {
            CodeUtilities
                .chat("You need to be in creative for this command to work.", ChatType.FAIL);
        }
    }

    public static void clipboard() {
        String clipboard;
        try {
            clipboard = mc.keyboard.getClipboard();
        } catch (Exception e) {
            CodeUtilities.chat("Unable to get Clipboard", ChatType.FAIL);
            return;
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

        assert mc.player != null;
        mc.player.sendChatMessage("/give " + clipboard);

    }

    public static void register(CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("give")
            .then(ArgumentBuilders.argument("item", ItemStackArgumentType.itemStack())
                .then(ArgumentBuilders.argument("count", IntegerArgumentType.integer(1, 127))
                    .executes(ctx -> {
                        try {
                            GiveCommand
                                .run(ctx.getArgument("item", ItemStackArgument.class)
                                        .createStack(1, false),
                                    ctx.getArgument("count", Integer.class));
                            return 1;
                        } catch (Exception err) {
                            CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
                            err.printStackTrace();
                            return -1;
                        }
                    })
                )
                .executes(ctx -> {
                    try {
                        GiveCommand
                            .run(ctx.getArgument("item", ItemStackArgument.class)
                                    .createStack(1, false),
                                1);
                        return 1;
                    } catch (Exception err) {
                        CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
                        err.printStackTrace();
                        return -1;
                    }
                })
            )
            .then(ArgumentBuilders.literal("clipboard")
                .executes(ctx -> {
                    try {
                        GiveCommand.clipboard();
                        return 1;
                    } catch (Exception err) {
                        CodeUtilities.chat("Error while executing command.", ChatType.FAIL);
                        err.printStackTrace();
                        return -1;
                    }
                })
            )
        );
    }

}
