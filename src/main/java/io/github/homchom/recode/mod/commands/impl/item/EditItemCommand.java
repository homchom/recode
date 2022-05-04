package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.ItemEditorMenu;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.*;

public class EditItemCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("edititem")
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandItem();
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
        return "[blue]/edititem[reset]\n"
                + "\n"
                + "Opens a menu to edit item data.";
    }

    @Override
    public String getName() {
        return "/edititem";
    }
}
