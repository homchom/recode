package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.item.*;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.util.CopyTextCommand;
import io.github.codeutilities.commands.util.HeadsCommand;
import io.github.codeutilities.commands.util.UuidCommand;
import io.github.codeutilities.commands.util.WebviewCommand;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler implements ClientCommandPlugin {

    private static final List<Command> commands = new ArrayList<>();

    static {
        register(
                new BreakableCommand(),
                new CustomHeadCommand(),
                new GiveCommand(),
                new ItemdataCommand(),
                new LoreCommand(),
                new UnpackCommand(),
                new NBSCommand(),
                new UuidCommand(),
                new WebviewCommand(),
                new CopyTextCommand(),
                new HeadsCommand()
        );
    }

    public static void register(Command... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    public static List<Command> getCommands() {
        return commands;
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        for (Command command : getCommands()) {
            command.register(MinecraftClient.getInstance(), commandDispatcher);
        }
    }
}
