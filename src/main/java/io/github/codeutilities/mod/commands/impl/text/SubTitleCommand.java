package io.github.codeutilities.mod.commands.impl.text;

import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.argument;
import static io.github.codeutilities.mod.commands.arguments.ArgBuilder.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.sys.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class SubTitleCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        reg("previewsubtitle",mc,cd);
        reg("subtitlepreview",mc,cd);
    }

    @Override
    public String getDescription() {
        return "/previewsubtitle [text]\n"
            + "/subtitlepreview [text]\n"
            + "\n"
            + "Displays the text in a subtitle or if no text is provided will use the name of the item you're holding.";
    }

    @Override
    public String getName() {
        return "/previewsubtitle";
    }

    public void reg(String name, MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(literal(name)
            .then(argument("message", StringArgumentType.greedyString())
                .executes(ctx -> {
                    Text msg = TextUtil.colorCodesToTextComponent(
                        ctx.getArgument("message", String.class)
                            .replace("&", "§"));

                    mc.inGameHud.setTitles(new LiteralText("§c"), null, 20, 60, 20);
                    mc.inGameHud.setTitles(null, msg, 0,0,0);
                    return 1;
                })
            )
            .executes(ctx -> {
                mc.inGameHud.setTitles(new LiteralText("§c"), null, 20, 60, 20);
                mc.inGameHud.setTitles(null, mc.player.getMainHandStack().getName(), 0,0,0);
                return 1;
            })
        );
    }
}
