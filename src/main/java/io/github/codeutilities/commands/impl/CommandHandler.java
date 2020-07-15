package io.github.codeutilities.commands.impl;

import io.github.codeutilities.commands.impl.item.*;
import io.github.codeutilities.commands.impl.nbs.NBSCommand;
import io.github.codeutilities.commands.impl.util.UuidCommand;
import io.github.codeutilities.commands.impl.util.WebviewCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandHandler {

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
                new WebviewCommand()
        );
    }

    public static void register(Command... cmds) {
        commands.addAll(Arrays.asList(cmds));
    }

    public static List<Command> getCommands() {
        return commands;
    }
}
