package io.github.homchom.recode.mod.commands;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.commands.impl.image.*;
import io.github.homchom.recode.mod.commands.impl.item.*;
import io.github.homchom.recode.mod.commands.impl.item.template.*;
import io.github.homchom.recode.mod.commands.impl.other.*;
import io.github.homchom.recode.mod.commands.impl.text.*;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.file.ILoader;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

import java.util.*;

public class CommandHandler implements ILoader {

    private static List<Command> cmds = new ArrayList<>();

    public static List<Command> getCommands() {
        return cmds;
    }

    @Override
    public void load() {
        register(
            new RecodeCommand(),
            new BreakableCommand(),
            new UnpackCommand(),
            new ItemdataCommand(),
            new UuidCommand(),
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
            new CalcCommand(),
            new CustomTextureCommand()
        );

        if (Config.getBoolean("dfCommands")) {
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
                new SchemCommand(),
                new RelativeLocCommand(),
                new NBSSearchCommand(),
                new PartnerBracketCommand(),
                new CodeVaultCommand(),
                new ImportFileCommand()
            );
        }
    }

    public void register(Command cmd) {
        cmd.register(Recode.MC, ClientCommandManager.DISPATCHER);
        cmds.add(cmd);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}
