package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.item.BreakableCommand;
import io.github.codeutilities.commands.item.CustomHeadCommand;
import io.github.codeutilities.commands.item.GiveCommand;
import io.github.codeutilities.commands.item.LoreCommand;
import io.github.codeutilities.commands.item.UnpackCommand;
import io.github.codeutilities.commands.nbs.NBSCommand;
import io.github.codeutilities.gui.ExampleGui;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class Commands implements ClientCommandPlugin {

   @Override
   public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
      GiveCommand.register(cd);
      LoreCommand.register(cd);
      BreakableCommand.register(cd);
      UnpackCommand.register(cd);
      NBSCommand.register(cd);
      CustomHeadCommand.register(cd);

      cd.register(ArgumentBuilders.literal("guitest").executes(ctx -> {
         CodeUtilities.openGuiAsync(new ExampleGui());
         return 1;
      }));
   }
}
