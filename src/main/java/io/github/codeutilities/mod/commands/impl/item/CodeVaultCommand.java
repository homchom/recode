package io.github.codeutilities.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.mod.features.commands.CodeVaultMenu;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CodeVaultCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("codevault")
            .executes(ctx -> {
                if (isCreative(mc)) {
                    CodeVaultMenu menu = new CodeVaultMenu();
                    menu.scheduleOpenGui(menu);
                }
                return 1;
            })
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/codevault[reset]\n"
                + "\n"
                + "Browses the code templates uploaded by other people. Click the item in the menu to get the code template.\n"
                + "To add your own code templates, join the plot [green]Code Vault[reset] (ID:43780) and upload the templates there.";
    }

    @Override
    public String getName() {
        return "/codevault";
    }
}
