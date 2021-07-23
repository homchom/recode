package io.github.codeutilities.sys.commands;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.image.ImageHologramCommand;
import io.github.codeutilities.mod.commands.image.ImageParticleCommand;
import io.github.codeutilities.mod.commands.item.*;
import io.github.codeutilities.mod.commands.item.template.SendTemplateCommand;
import io.github.codeutilities.mod.commands.item.template.WebviewCommand;
import io.github.codeutilities.mod.commands.other.*;
import io.github.codeutilities.mod.commands.text.*;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.util.file.ILoader;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public class CommandHandler implements ILoader {

    @Override
    public void load() {
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
                    new ImageHologramCommand(),
                    new ImageParticleCommand(),
                    new SchemCommand(),
                    new RelativeLocCommand(),
                    new PlotsCommand()
            );
        }
    }

    public void register(Command cmd) {
        cmd.register(CodeUtilities.MC, ClientCommandManager.DISPATCHER);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}
