package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.item.BreakableCommand;
import io.github.codeutilities.commands.item.EditItemCommand;
import io.github.codeutilities.commands.item.GiveCommand;
import io.github.codeutilities.commands.item.ItemdataCommand;
import io.github.codeutilities.commands.item.LoreCommand;
import io.github.codeutilities.commands.item.TemplatesCommand;
import io.github.codeutilities.commands.item.UnpackCommand;
import io.github.codeutilities.commands.item.template.SendTemplateCommand;
import io.github.codeutilities.commands.item.template.WebviewCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.util.ColorCommand;
import io.github.codeutilities.commands.util.ColorsCommand;
import io.github.codeutilities.commands.util.CopyTextCommand;
import io.github.codeutilities.commands.util.HeadsCommand;
import io.github.codeutilities.commands.util.NodeCommand;
import io.github.codeutilities.commands.util.UuidCommand;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.MinecraftClient;

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
            new NodeCommand(),
            new EditItemCommand()
        );
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        for (Command command : getCommands()) {
            command.register(MinecraftClient.getInstance(), commandDispatcher);
        }
    }

}
