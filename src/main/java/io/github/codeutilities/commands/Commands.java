package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.commands.item.GiveCommand;
import io.github.codeutilities.commands.item.LoreCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class Commands implements ClientCommandPlugin {

   @Override
   public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
      GiveCommand.register(cd);
      LoreCommand.register(cd);
      NBSCommand.register(cd);
   }
}
