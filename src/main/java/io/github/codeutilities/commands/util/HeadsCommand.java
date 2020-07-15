package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class HeadsCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("heads").executes(ctx -> {
            CodeUtilities.openGuiAsync(new CustomHeadSearchGui());
            if (!MinecraftClient.getInstance().player.isCreative()) {
                CodeUtilities.chat("You need to be in creative to get heads.", ChatType.FAIL);
            }
            return 1;
        }));
    }
}
