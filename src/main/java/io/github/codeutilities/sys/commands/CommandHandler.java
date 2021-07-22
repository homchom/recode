package io.github.codeutilities.sys.commands;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.image.ImageHologramCommand;
import io.github.codeutilities.mod.commands.image.ImageParticleCommand;
import io.github.codeutilities.mod.commands.item.BreakableCommand;
import io.github.codeutilities.mod.commands.item.EditItemCommand;
import io.github.codeutilities.mod.commands.item.GiveCommand;
import io.github.codeutilities.mod.commands.item.ItemdataCommand;
import io.github.codeutilities.mod.commands.item.RelativeLocCommand;
import io.github.codeutilities.mod.commands.item.TemplatesCommand;
import io.github.codeutilities.mod.commands.item.UnpackCommand;
import io.github.codeutilities.mod.commands.item.template.SendTemplateCommand;
import io.github.codeutilities.mod.commands.item.template.WebviewCommand;
import io.github.codeutilities.mod.commands.other.NBSCommand;
import io.github.codeutilities.mod.commands.other.SchemCommand;
import io.github.codeutilities.mod.commands.other.CodeUtilitiesCommand;
import io.github.codeutilities.mod.commands.text.ColorCommand;
import io.github.codeutilities.mod.commands.text.ColorsCommand;
import io.github.codeutilities.mod.commands.other.ConfigCommand;
import io.github.codeutilities.mod.commands.text.CopyTextCommand;
import io.github.codeutilities.mod.commands.other.DebugCommand;
import io.github.codeutilities.mod.commands.text.GradientCommand;
import io.github.codeutilities.mod.commands.item.HeadsCommand;
import io.github.codeutilities.mod.commands.other.NodeCommand;
import io.github.codeutilities.mod.commands.other.PJoinCommand;
import io.github.codeutilities.mod.commands.other.PlotsCommand;
import io.github.codeutilities.mod.commands.other.SearchCommand;
import io.github.codeutilities.mod.commands.text.UuidCommand;
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
