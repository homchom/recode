package io.github.codeutilities.mod.commands;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.commands.impl.image.ImageHologramCommand;
import io.github.codeutilities.mod.commands.impl.image.ImageParticleCommand;
import io.github.codeutilities.mod.commands.impl.item.BreakableCommand;
import io.github.codeutilities.mod.commands.impl.item.CodeVaultCommand;
import io.github.codeutilities.mod.commands.impl.item.EditItemCommand;
import io.github.codeutilities.mod.commands.impl.item.GiveCommand;
import io.github.codeutilities.mod.commands.impl.item.HeadsCommand;
import io.github.codeutilities.mod.commands.impl.item.ImportFileCommand;
import io.github.codeutilities.mod.commands.impl.item.ItemdataCommand;
import io.github.codeutilities.mod.commands.impl.item.RelativeLocCommand;
import io.github.codeutilities.mod.commands.impl.item.TemplatesCommand;
import io.github.codeutilities.mod.commands.impl.item.UnpackCommand;
import io.github.codeutilities.mod.commands.impl.item.template.SendTemplateCommand;
import io.github.codeutilities.mod.commands.impl.item.template.WebviewCommand;
import io.github.codeutilities.mod.commands.impl.other.CalcCommand;
import io.github.codeutilities.mod.commands.impl.other.CodeUtilitiesCommand;
import io.github.codeutilities.mod.commands.impl.other.DebugCommand;
import io.github.codeutilities.mod.commands.impl.other.NBSCommand;
import io.github.codeutilities.mod.commands.impl.other.NBSSearchCommand;
import io.github.codeutilities.mod.commands.impl.other.NodeCommand;
import io.github.codeutilities.mod.commands.impl.other.PJoinCommand;
import io.github.codeutilities.mod.commands.impl.other.PartnerBracketCommand;
import io.github.codeutilities.mod.commands.impl.other.PlotsCommand;
import io.github.codeutilities.mod.commands.impl.other.QueueCommand;
import io.github.codeutilities.mod.commands.impl.other.SchemCommand;
import io.github.codeutilities.mod.commands.impl.other.SearchCommand;
import io.github.codeutilities.mod.commands.impl.text.ActionbarCommand;
import io.github.codeutilities.mod.commands.impl.text.ColorCommand;
import io.github.codeutilities.mod.commands.impl.text.ColorsCommand;
import io.github.codeutilities.mod.commands.impl.text.CopyTextCommand;
import io.github.codeutilities.mod.commands.impl.text.GradientCommand;
import io.github.codeutilities.mod.commands.impl.text.SubTitleCommand;
import io.github.codeutilities.mod.commands.impl.text.TitleCommand;
import io.github.codeutilities.mod.commands.impl.text.UuidCommand;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.file.ILoader;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

public class CommandHandler implements ILoader {

    private static List<Command> cmds = new ArrayList<>();

    public static List<Command> getCommands() {
        return cmds;
    }

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
            new SearchCommand(),
            new DebugCommand(),
            new QueueCommand(),
            new TitleCommand(),
            new SubTitleCommand(),
            new ActionbarCommand(),
            new CalcCommand()
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
                new PlotsCommand(),
                new NBSSearchCommand(),
                new PartnerBracketCommand(),
                new CodeVaultCommand(),
                new ImportFileCommand()
            );
        }
    }

    public void register(Command cmd) {
        cmd.register(CodeUtilities.MC, ClientCommandManager.DISPATCHER);
        cmds.add(cmd);
    }

    public void register(Command... cmds) {
        for (Command cmd : cmds) {
            this.register(cmd);
        }
    }
}
