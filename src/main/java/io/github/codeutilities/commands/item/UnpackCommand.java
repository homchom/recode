package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ItemUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class UnpackCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("unpack")
                .executes(ctx -> {
                    if (mc.player.isCreative()) {
                        ItemStack handItem = mc.player.getMainHandStack();
                        if (!handItem.getOrCreateTag().getCompound("BlockEntityTag").isEmpty()) {

                            int items = 0;
                            for (ItemStack stack : ItemUtil.fromItemContainer(handItem)) {
                                if (!stack.isEmpty()) {
                                    items++;
                                    ItemUtil.giveCreativeItem(stack);
                                }
                            }

                            if (items == 0) {
                                CodeUtilities.chat("There are no items stored in this container!", ChatType.FAIL);
                            } else {
                                if (items == 1) {
                                    CodeUtilities.chat("Unpacked §b" + items + "§a item!", ChatType.SUCCESS);
                                } else {
                                    CodeUtilities.chat("§aUnpacked §b" + items + "§a items!", ChatType.SUCCESS);
                                }
                            }
                        } else {
                            CodeUtilities.chat("TThere are no items stored in this item!", ChatType.FAIL);
                        }
                    } else {
                        CodeUtilities.chat("You need to be in creative mode to use this command!", ChatType.FAIL);
                    }
                    return 1;
                })
        );
    }
}
