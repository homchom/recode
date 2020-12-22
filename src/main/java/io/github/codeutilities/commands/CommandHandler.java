package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.image.ImageHologramCommand;
import io.github.codeutilities.commands.image.ImageParticleCommand;
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
                new CodeUtilitiesCommand(),
                new BreakableCommand(),
                new UnpackCommand(),
                new ItemdataCommand(),
                new UuidCommand(),
                new HeadsCommand(),
                new ColorsCommand(),
                new ColorCommand(),
                new EditItemCommand(),
                new CopyTextCommand()
        );
        
        if (ModConfig.getConfig().dfCommands) {
            register(
                    new GiveCommand(),
                    new RejoinCommand(),
                    new PJoinCommand(),
                    new NodeCommand(),
                    new TemplatesCommand(),
                    new WebviewCommand(),
                    new NBSCommand(),
                    new ImageHologramCommand(),
                    new ImageParticleCommand(),
                    new SendTemplateCommand()
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
