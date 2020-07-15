package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.impl.CommandHandler;
import io.github.codeutilities.gui.CustomHeadSearchGui;
import io.github.codeutilities.util.ChatType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class Commands implements ClientCommandPlugin {

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
        CommandHandler.getCommands().forEach(command -> command.register(MinecraftClient.getInstance(), cd));
    }
}
