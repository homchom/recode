package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.image.ImageCommand;
import io.github.codeutilities.commands.item.*;
import io.github.codeutilities.commands.item.template.*;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.commands.util.*;
import io.github.codeutilities.config.ModConfig;
import io.github.cottonmc.clientcommands.*;
import net.minecraft.client.MinecraftClient;

import java.util.*;

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
                new UnpackCommand(),
                // THis command is just so good
                new ItemdataCommand(),
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
                new EditItemCommand(),
                new ImageCommand()
        );
        
        if (ModConfig.getConfig().dfCommands) {
            register(
                    new GiveCommand(),
                    new LoreCommand(),
                    new RejoinCommand(),
                    new PJoinCommand()
            );
        }
    }
    
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        for (Command command : getCommands()) {
            command.register(MinecraftClient.getInstance(), commandDispatcher);
        }
    }
    
}
