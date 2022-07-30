package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.item.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class GiveCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("dfgive")
                .then(ArgBuilder.argument("item", ItemArgument.item(context))
                        .then(ArgBuilder.argument("count", IntegerArgumentType.integer())
                                .executes(ctx -> {
                                    giveItem(mc, ctx.getArgument("item", ItemInput.class)
                                                    .createItemStack(1, false),
                                            ctx.getArgument("count", Integer.class));
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            giveItem(mc, ctx.getArgument("item", ItemInput.class)
                                    .createItemStack(1, false), 1);
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("clipboard")
                        .executes(ctx -> {
                            String clipboard;
                            try {
                                clipboard = mc.keyboardHandler.getClipboard();
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

                            this.sendCommand(mc, "/dfgive " + clipboard);
                            return 1;
                        })
                )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/dfgive <item|clipboard> [count][reset]\n"
            + "\n"
            + "Gives you an item, like in Minecraft /give.\n"
            + "Use [yellow]/dfgive clipboard[reset] if you have a long /give command copied in your clipboard.";
    }

    @Override
    public String getName() {
        return "/dfgive";
    }

    private void giveItem(Minecraft mc, ItemStack item, int count) {
        item.setCount(count);
        if (this.isCreative(mc)) {
            if (count >= 1) {
                if (count <= item.getMaxStackSize()) {
                    ItemUtil.giveCreativeItem(item, true);
                } else {
                    MutableComponent text1 = Component.literal("Maximum item count for ");
                    MutableComponent text2 = Component.literal(" is " + item.getMaxStackSize() + "!");
                    ChatUtil.sendMessage(text1.append(item.getHoverName()).append(text2), ChatType.FAIL);
                }
            } else {
                ChatUtil.sendMessage("Minimum item count is 1!", ChatType.FAIL);
            }
        }
    }
}
