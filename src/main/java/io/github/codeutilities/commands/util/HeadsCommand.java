package io.github.codeutilities.commands.util;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class HeadsCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("heads").executes(ctx -> {
            if (this.isCreative(mc)) {
                CustomHeadSearchGui instance = CustomHeadSearchGui.getInstance();
                instance.loadGui();
                CodeUtilities.openGuiAsync(instance);
            }
            return 1;
        }));
    }
}
