package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.commands.ColorsMenu;
import io.github.homchom.recode.multiplayer.CommandQueue;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;

public class ColorsCommand extends Command {
    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        cd.register(ArgBuilder.literal("colors").executes((ctx) -> {
            if (Config.getBoolean("colorReplacePicker")) {
                CommandQueue.unsafelySendCommand("hypercube:colors");
            } else {
                ColorsMenu colorsGui = new ColorsMenu();
                colorsGui.scheduleOpenGui(colorsGui, "");
            }
            return 1;
        })
                .then(ArgBuilder.argument("Saturation(%)", IntegerArgumentType.integer(0, 100)).executes((ctx) -> {
                    int saturation = IntegerArgumentType.getInteger(ctx, "Saturation(%)");
                    CommandQueue.unsafelySendCommand("hypercube:colors " + saturation);
                    return 1;
                })));
    }

    @Override
    public String getDescription() {
        return """
                [blue]/colors [saturation][reset]

                Shows the color picker menu.
                When Saturation value is specified, it will show you the old color picker in chat.""";
    }

    @Override
    public String getName() {
        return "/colors";
    }
}