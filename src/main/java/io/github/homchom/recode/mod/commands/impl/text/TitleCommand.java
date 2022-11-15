package io.github.homchom.recode.mod.commands.impl.text;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.sys.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.argument;
import static io.github.homchom.recode.mod.commands.arguments.ArgBuilder.literal;

public class TitleCommand extends Command {

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd, CommandBuildContext context) {
        reg("previewtitle",mc,cd);
        reg("titlepreview",mc,cd);
    }

    @Override
    public String getDescription() {
        return "[blue]/previewtitle [text][reset]\n"
                + "[blue]/titlepreview [text][reset]\n"
                + "\n"
                + "Previews the title text. If no text is specified, the name of the item you are holding will show up.";
    }

    @Override
    public String getName() {
        return "/previewtitle";
    }

    public void reg(String name, Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal(name)
            .then(argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    Component msg = TextUtil.colorCodesToTextComponent(
                        ctx.getArgument("message", String.class)
                            .replace("&", "§"));

                    mc.gui.setTitle(msg);
                    mc.gui.setTimes(20, 60, 20);
                    return 1;
                })
            )
            .executes(ctx -> {
                mc.gui.setTitle(mc.player.getMainHandItem().getHoverName());
                mc.gui.setTimes(20, 60, 20);
                return 1;
            })
        );
    }
}
