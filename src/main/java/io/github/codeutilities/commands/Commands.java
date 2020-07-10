package io.github.codeutilities.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.item.GiveCommand;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.command.arguments.ItemStackArgumentType;

public class Commands implements ClientCommandPlugin {

  private CommandDispatcher<CottonClientCommandSource> cd;

  @Override
  public void registerCommands(CommandDispatcher<CottonClientCommandSource> cd) {
    this.cd = cd;
    give(); // give <item> <amount> | give clipboard
  }

  private void give() {
    cd.register(ArgumentBuilders.literal("give")
        .then(ArgumentBuilders.argument("item", ItemStackArgumentType.itemStack())
            .then(ArgumentBuilders.argument("count", IntegerArgumentType.integer(1, 127))
                .executes(ctx -> {
                  try {
                    GiveCommand.run(ctx.getArgument("item", ItemStackArgument.class).createStack(1,false),
                        ctx.getArgument("count", Integer.class));
                    return 1;
                  } catch (Exception err) {
                    CodeUtilities.chat("§cError while executing command.");
                    err.printStackTrace();
                    return -1;
                  }
                })
            )
            .executes(ctx -> {
              try {
                GiveCommand.run(ctx.getArgument("item", ItemStackArgument.class).createStack(1,false),
                    1);
                return 1;
              } catch (Exception err) {
                CodeUtilities.chat("§cError while executing command.");
                err.printStackTrace();
                return -1;
              }
            })
        )
        .then(ArgumentBuilders.literal("clipboard")
            .executes(ctx -> {
              try {
                GiveCommand.clipboard();
                return 1;
              } catch (Exception err) {
                CodeUtilities.chat("§cError while executing command.");
                err.printStackTrace();
                return -1;
              }
            })
        )
    );
  }

}
