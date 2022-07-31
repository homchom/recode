package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.TemplatesMenu;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class TemplatesCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("templates")
                .executes(ctx -> {
                    if (this.isCreative(mc)) {
                        TemplatesMenu templateStorageUI = new TemplatesMenu();
                        templateStorageUI.scheduleOpenGui(templateStorageUI);
                    } else {
                        return -1;
                    }
                    return 1;
                })
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/templates[reset]\n"
                + "\n"
                + "Shows a list of recently used code templates. Click on the item to get it in your inventory.";
    }

    @Override
    public String getName() {
        return "/templates";
    }
}
