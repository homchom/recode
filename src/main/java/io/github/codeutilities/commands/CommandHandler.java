package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.image.ImageHologramCommand;
import io.github.codeutilities.commands.image.ImageParticleCommand;
import io.github.codeutilities.commands.item.*;
import io.github.codeutilities.commands.item.template.SendTemplateCommand;
import io.github.codeutilities.commands.item.template.WebviewCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.schem.SchemCommand;
import io.github.codeutilities.commands.util.*;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.IManager;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements ClientCommandPlugin, IManager<Command> {
    private static CommandHandler instance;
    private final List<Command> registeredCommands = new ArrayList<>();

    public CommandHandler() {
        instance = this;

        this.initialize();
    }

    public static CommandHandler getInstance() {
        return instance == null ? new CommandHandler() : instance;
    }

    public void initialize() {
        register(
                new CodeUtilitiesCommand(),
                new BreakableCommand(),
                new UnpackCommand(),
                new ItemdataCommand(),
                new UuidCommand(),
                new HeadsCommand(),
                new ColorsCommand(),
                new ColorCommand(),
                new EditItemCommand(),
                new CopyTextCommand(),
                new GradientCommand(),
                new ConfigCommand(),
                new SearchCommand(),
                new DebugCommand()
        );

        if (CodeUtilsConfig.getBool("dfCommands")) {
            register(
                    new GiveCommand(),
                    new NodeCommand(),
                    new TemplatesCommand(),
                    new WebviewCommand(),
                    new NBSCommand(),
                    new ImageHologramCommand(),
                    new ImageParticleCommand(),
                    new SendTemplateCommand(),
                    new PJoinCommand(),
                    new ImageHologramCommand(),
                    new ImageParticleCommand(),
                    new SchemCommand(),
                    new RelativeLocCommand(),
                    new PlotsCommand()
            );
        }
    }

    @Override
    public void register(Command object) {
        this.registeredCommands.add(object);
    }

    public void register(Command... objects) {
        for (Command object : objects) {
            this.register(object);
        }
    }

    @Override
    public List<Command> getRegistered() {
        return this.registeredCommands;
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        for (Command command : this.getRegistered()) {
            command.register(CodeUtilities.MC, commandDispatcher);
        }
    }

}
