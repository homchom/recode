package io.github.codeutilities.commands.sys;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.impl.image.impl.ImageHologramCommand;
import io.github.codeutilities.commands.impl.image.impl.ImageParticleCommand;
import io.github.codeutilities.commands.impl.item.BreakableCommand;
import io.github.codeutilities.commands.impl.item.EditItemCommand;
import io.github.codeutilities.commands.impl.item.GiveCommand;
import io.github.codeutilities.commands.impl.item.ItemdataCommand;
import io.github.codeutilities.commands.impl.item.RelativeLocCommand;
import io.github.codeutilities.commands.impl.item.TemplatesCommand;
import io.github.codeutilities.commands.impl.item.UnpackCommand;
import io.github.codeutilities.commands.impl.item.template.SendTemplateCommand;
import io.github.codeutilities.commands.impl.item.template.WebviewCommand;
import io.github.codeutilities.commands.impl.nbs.impl.NBSCommand;
import io.github.codeutilities.commands.impl.schem.impl.SchemCommand;
import io.github.codeutilities.commands.impl.util.CodeUtilitiesCommand;
import io.github.codeutilities.commands.impl.util.ColorCommand;
import io.github.codeutilities.commands.impl.util.ColorsCommand;
import io.github.codeutilities.commands.impl.util.ConfigCommand;
import io.github.codeutilities.commands.impl.util.CopyTextCommand;
import io.github.codeutilities.commands.impl.util.CountCommand;
import io.github.codeutilities.commands.impl.util.DebugCommand;
import io.github.codeutilities.commands.impl.util.GradientCommand;
import io.github.codeutilities.commands.impl.util.HeadsCommand;
import io.github.codeutilities.commands.impl.util.NodeCommand;
import io.github.codeutilities.commands.impl.util.PJoinCommand;
import io.github.codeutilities.commands.impl.util.PlotsCommand;
import io.github.codeutilities.commands.impl.util.SearchCommand;
import io.github.codeutilities.commands.impl.util.UuidCommand;
import io.github.codeutilities.config.Config;
import io.github.codeutilities.util.file.ILoader;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public class CommandHandler implements ILoader {

    @Override
    public void load() {
        register(
                new CodeUtilitiesCommand(),
                new BreakableCommand(),
                new CountCommand(),
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
