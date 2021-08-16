package io.github.codeutilities.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.mod.features.commands.ItemEditorMenu;
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
                        ItemEditorMenu itemEditorGui = new ItemEditorMenu(item);
                        itemEditorGui.scheduleOpenGui(itemEditorGui);
                        return 1;
                    } else {
                        return -1;
                    }
                })
        );
    }

    @Override
    public String getDescription() {
        return "/edititem\n\nOpen a very simple gui for editing the item nbt & name";
    }

    @Override
    public String getName() {
        return "/edititem";
    }
}
