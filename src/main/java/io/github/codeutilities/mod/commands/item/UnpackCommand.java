package io.github.codeutilities.mod.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import io.github.codeutilities.sys.util.misc.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class UnpackCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("unpack")
                .executes(ctx -> {
                    if (this.isCreative(mc)) {
                        ItemStack handItem = mc.player.getMainHandStack();
                        if (!handItem.getOrCreateTag().getCompound("BlockEntityTag").isEmpty()) {

                            int items = 0;
                            for (ItemStack stack : ItemUtil.fromItemContainer(handItem)) {
                                if (!stack.isEmpty()) {
                                    items++;
                                    ItemUtil.giveCreativeItem(stack, true);
                                }
                            }

                            if (items == 0) {
                                ChatUtil.sendMessage("There are no items stored in this container!", ChatType.FAIL);
                            } else {
                                if (items == 1) {
                                    ChatUtil.sendMessage("Unpacked §b" + items + "§r item!", ChatType.SUCCESS);
                                } else {
                                    ChatUtil.sendMessage("Unpacked §b" + items + "§r items!", ChatType.SUCCESS);
                                }
                            }
                        } else {
                            ChatUtil.sendMessage("There are no items stored in this item!", ChatType.FAIL);
                        }
                    } else {
                        return -1;
                    }
                    return 1;
                })
        );
    }
}
