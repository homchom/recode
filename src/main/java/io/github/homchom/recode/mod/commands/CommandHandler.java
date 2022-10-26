package io.github.homchom.recode.mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.commands.impl.image.*;
import io.github.homchom.recode.mod.commands.impl.item.*;
import io.github.homchom.recode.mod.commands.impl.item.template.*;
import io.github.homchom.recode.mod.commands.impl.other.*;
import io.github.homchom.recode.mod.commands.impl.text.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

import java.util.*;

public class CommandHandler {

    private static List<Command> cmds = new ArrayList<>();

    public static List<Command> getCommands() {
        return cmds;
    }

    public static void load(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        cmds.clear();

        register(dispatcher, context,
            new RecodeCommand(),
            new BreakableCommand(),
            new UnpackCommand(),
            new ItemdataCommand(),
            new UuidCommand(),
            new NameCommand(),
            // new HeadsCommand(),
            new ColorsCommand(),
            new ColorCommand(),
            new EditItemCommand(),
            new CopyTextCommand(),
            new GradientCommand(),
            new SearchCommand(),
            new QueueCommand(),
            new TitleCommand(),
            new SubTitleCommand(),
            new ActionbarCommand(),
            new CalcCommand()
        );

        if (Config.getBoolean("dfCommands")) {
            register(dispatcher, context,
                new GiveCommand(),
                new NodeCommand(),
                new TemplatesCommand(),
                new WebviewCommand(),
                new NBSCommand(),
                new ImageHologramCommand(),
                new ImageParticleCommand(),
                new SendTemplateCommand(),
                new PJoinCommand(),
                new SchemCommand(),
                new RelativeLocCommand(),
                new NBSSearchCommand(),
                new PartnerBracketCommand(),
                new CodeVaultCommand(),
                new ImportFileCommand()
            );
        }
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context, Command cmd) {
        cmd.register(LegacyRecode.MC, dispatcher, context);
        cmds.add(cmd);
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context, Command... cmds) {
        for (Command cmd : cmds) {
            register(dispatcher, context, cmd);
        }
    }
}
