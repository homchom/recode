package io.github.homchom.recode.mod.commands.impl.other;


import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.features.commands.recode.RecodeUI;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class RecodeCommand extends Command {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("recode")
                .executes(ctx -> {
                    RecodeUI gui = new RecodeUI();
                    gui.scheduleOpenGui(gui);
                    return 1;
                })
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/recode[reset]\n\nShows information about this mod, such as this help menu, mod contributors, etc.";
    }

    @Override
    public String getName() {
        return "/recode";
    }
}