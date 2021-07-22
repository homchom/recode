package io.github.codeutilities.mod.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.sys.commands.Command;
import io.github.codeutilities.sys.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.util.chat.ChatType;
import io.github.codeutilities.sys.util.chat.ChatUtil;
import io.github.codeutilities.sys.util.gui.menus.ItemEditorGui;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EditItemCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("edititem")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandStack();
                    if (item.getItem() == Items.AIR) {
                        ChatUtil.sendMessage("You need to hold an item that is not air!", ChatType.FAIL);
                        return -1;
                    }
                    if (this.isCreative(mc)) {
                        ItemEditorGui itemEditorGui = new ItemEditorGui(item);
                        itemEditorGui.scheduleOpenGui(itemEditorGui);
                        return 1;
                    } else {
                        return -1;
                    }
                })
        );
    }
}
