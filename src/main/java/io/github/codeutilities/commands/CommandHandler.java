package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.image.ImageToTemplateCommand115;
import io.github.codeutilities.commands.item.*;
import io.github.codeutilities.commands.item.template.*;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.util.*;
import io.github.cottonmc.clientcommands.*;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class CommandHandler implements ClientCommandPlugin {

    private static final List<Command> commands = new ArrayList<>();

    public static void register(Command... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    public static List<Command> getCommands() {
        return commands;
    }


    public static void initialize() {
        register(
                new BreakableCommand(),
                new SendTemplateCommand(),
                new GiveCommand(),
                new ItemdataCommand(),
                new LoreCommand(),
                new UnpackCommand(),
                new NBSCommand(),
                new UuidCommand(),
                new WebviewCommand(),
                new CopyTextCommand(),
                new HeadsCommand(),
                //new ImageToTemplateCommand115(),
                new TemplatesCommand(),
                new ColorsCommand(),
                new ColorCommand(),
                new NodeCommand()
        );
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        for (Command command : getCommands()) {
            command.register(MinecraftClient.getInstance(), commandDispatcher);
        }

    }

}
