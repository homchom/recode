package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ItemUtil;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CustomHeadCommand extends Command {

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgumentBuilders.literal("customhead")
                .then(ArgumentBuilders.literal("search")
                        .executes(ctx -> {
                            if (MinecraftClient.getInstance().player.isCreative()) {
                                CodeUtilities.openGuiAsync(new CustomHeadSearchGui());
                                CodeUtilities.chat("Tip: Do /heads instead. Its shorter!", ChatType.INFO_YELLOW);

                            } else {
                                CodeUtilities.chat("You need to be in creative to get heads.", ChatType.FAIL);
                            }
                            return 1;
                        })
                )

                // This is a bit broken.
                .then(ArgumentBuilders.argument("value", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            if (mc.player.isCreative()) {
                                ItemUtil.givePlayerHead(ctx.getArgument("value", String.class));
                            } else {
                                CodeUtilities.chat("You need to be in creative for this command to work!", ChatType.FAIL);
                            }
                            return 1;
                        })
                )
        );
    }
}
