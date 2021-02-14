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
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.IManager;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements ClientCommandPlugin, IManager<Command> {
    private final List<Command> registeredCommands = new ArrayList<>();
    private static CommandHandler instance;

    public CommandHandler() {
        instance = this;

        this.initialize();
    }

    public void initialize() {
        register(new CodeUtilitiesCommand());
        register(new BreakableCommand());
        register(new UnpackCommand());
        register(new ItemdataCommand());
        register(new UuidCommand());
        register(new HeadsCommand());
        register(new ColorsCommand());
        register(new ColorCommand());
        register(new EditItemCommand());
        register(new CopyTextCommand());
        register(new ImageHologramCommand());
        register(new ImageParticleCommand());
        register(new SchemCommand());

        if (ModConfig.getConfig().dfCommands) {
            register(new GiveCommand());
            register(new NodeCommand());
            register(new TemplatesCommand());
            register(new WebviewCommand());
            register(new NBSCommand());
            register(new ImageHologramCommand());
            register(new ImageParticleCommand());
            register(new SendTemplateCommand());
        }
    }

    @Override
    public void register(Command object) {
        this.registeredCommands.add(object);
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

    public static CommandHandler getInstance() {
        return instance == null ? new CommandHandler() : instance;
    }

}
