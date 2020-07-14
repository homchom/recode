package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.item.BreakableCommand;
import io.github.codeutilities.commands.item.CustomHeadCommand;
import io.github.codeutilities.commands.item.GiveCommand;
import io.github.codeutilities.commands.item.LoreCommand;
import io.github.codeutilities.commands.item.UnpackCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.util.UuidCommand;
import io.github.codeutilities.commands.util.WebviewCommand;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class Commands implements ClientCommandPlugin {

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
        GiveCommand.register(cd);
        LoreCommand.register(cd);
        BreakableCommand.register(cd);
        UnpackCommand.register(cd);
        NBSCommand.register(cd);
        WebviewCommand.register(cd);
        UuidCommand.register(cd);
        CustomHeadCommand.register(cd);

        cd.register(ArgumentBuilders.literal("heads").executes(ctx -> {
            CodeUtilities.openGuiAsync(new CustomHeadSearchGui());
            assert MinecraftClient.getInstance().player != null;
            if (!MinecraftClient.getInstance().player.isCreative()) CodeUtilities.chat("You need to be in creative to get heads.", ChatType.FAIL);
            return 1;
        }));
    }
}
