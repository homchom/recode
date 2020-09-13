package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.ItemEditorGui;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class EditItemCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("edititem")
            .executes(ctx -> {
                ItemStack item = mc.player.getMainHandStack();
                if (item.getItem() == Items.AIR) {
                    ChatUtil
                        .sendMessage("You need to hold an item that is not air!", ChatType.FAIL);
                    return -1;
                }
                if (!mc.player.isCreative()) {
                    ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                    return -1;
                }
                CodeUtilities.openGuiAsync(new ItemEditorGui(item));
                return 1;
            })
        );
    }
}
