package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.CodeVaultMenu;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class CodeVaultCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
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
