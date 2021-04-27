package io.github.codeutilities.commands.util;


import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.menus.codeutils.CodeUtilitiesMenu;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CodeUtilitiesCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("codeutilities")
            .executes(ctx -> {
                CodeUtilitiesMenu codeUtilitiesMenu = new CodeUtilitiesMenu();
                codeUtilitiesMenu.scheduleOpenGui(codeUtilitiesMenu);
                return 1;
            })
        );
    }
}
