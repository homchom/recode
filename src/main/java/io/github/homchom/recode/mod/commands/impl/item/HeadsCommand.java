package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.commands.HeadsMenu;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

public class HeadsCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("heads").executes(ctx -> {
            if (!Config.getBoolean("headsEnabled")) {
                mc.player.chat("/hypercube:heads");
                return 1;
            }
            if (this.isCreative(mc)) {
                HeadsMenu headMenu = HeadsMenu.getInstance();
                headMenu.scheduleOpenGui(headMenu, "");
            }
            return 1;
        }).then(ArgBuilder.argument("query", StringArgumentType.greedyString()).executes(ctx -> {
            if (!Config.getBoolean("headsEnabled")) {
                mc.player.chat("/hypercube:heads " + ctx.getArgument("query", String.class));
                return 1;
            }
            if (this.isCreative(mc)) {
                String query = ctx.getArgument("query", String.class);
                HeadsMenu headMenu = HeadsMenu.getInstance();
                headMenu.scheduleOpenGui(headMenu, query);
            }
            return 1;
        })));
    }

    @Override
    public String getDescription() {
        return "[blue]/heads [keyword][reset]\n"
                + "\n"
                + "[red]*Disabled by default, you can enable it in config*[reset]\n"
                + "Opens a menu to browse player heads from minecraft-heads.com. Click on a head item to get it in your inventory.";
    }

    @Override
    public String getName() {
        return "/heads";
    }
}
