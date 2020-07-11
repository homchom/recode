package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.commands.item.GiveCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class Commands implements ClientCommandPlugin {

  private CommandDispatcher<CottonClientCommandSource> cd;

  @Override
  public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
    this.cd = cd;
    
    //ITEM RELATED
    GiveCommand.register(cd); // give <item> <amount> | give clipboard
    
    //CODE TEMPLATE RELATED
    NBSCommand.register(cd); // nbs load <filename> | nbs player
  }
}
