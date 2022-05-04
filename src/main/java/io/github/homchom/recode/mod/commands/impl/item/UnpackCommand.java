package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.player.chat.*;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class UnpackCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("unpack")
                .executes(ctx -> {
                    if (this.isCreative(mc)) {
                        ItemStack handItem = mc.player.getMainHandItem();
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

    @Override
    public String getDescription() {
        return "[blue]/unpack[reset]\n\nExtracts the items in a container you are holding.";
    }

    @Override
    public String getName() {
        return "/unpack";
    }
}
