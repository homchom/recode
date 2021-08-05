package io.github.codeutilities.mod.commands.impl.other;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CalcCommand extends Command {

    private static int exec(CommandContext<FabricClientCommandSource> ctx) {
        String exp = ctx.getArgument("exp", String.class);

        try {
            String texp = exp.replaceAll(" ", "");
            String old = null;

            while (!Objects.equals(old, texp)) {
                old = texp;

                Matcher mulm = Pattern.compile(
                    "(?<n1>-?(\\.\\d+|\\d+\\.\\d+|\\d+))(?<e>\\*|\\/)(?<n2>-?(\\.\\d+|\\d+\\.\\d+|\\d+))"
                ).matcher(texp);
                Matcher addm = Pattern.compile(
                    "(?<n1>-?(\\.\\d+|\\d+\\.\\d+|\\d+))(?<e>\\+|\\-)(?<n2>-?(\\.\\d+|\\d+\\.\\d+|\\d+))"
                ).matcher(texp);
                Matcher bm = Pattern.compile(
                    "\\((?<n>-?(\\.\\d+|\\d+\\.\\d+|\\d+))\\)"
                ).matcher(texp);

                if (mulm.find()) {
                    double num1 = Double.parseDouble(mulm.group("n1"));
                    double num2 = Double.parseDouble(mulm.group("n2"));
                    if (Objects.equals(mulm.group("e"), "*")) {
                        texp = mulm.replaceFirst(
                            String.valueOf(num1 * num2));
                    } else {
                        texp = mulm.replaceFirst(String.valueOf(num1 / num2));
                    }

                } else if (addm.find()) {
                    System.out.println("found addm");
                    double num1 = Double.parseDouble(addm.group("n1"));
                    double num2 = Double.parseDouble(addm.group("n2"));
                    if (Objects.equals(addm.group("e"), "+")) {
                        texp = addm.replaceFirst(
                            String.valueOf(num1 + num2));
                    } else {
                        texp = addm.replaceFirst(String.valueOf(num1 - num2));
                    }
                } else if (bm.find()) {
                    double num = Double.parseDouble(bm.group("n"));
                    texp = bm.replaceFirst(String.valueOf(num));
                }
            }

            ChatUtil.sendMessage( exp + " §e=§f " + texp,ChatType.SUCCESS);
        } catch (Exception err) {
            err.printStackTrace();
            ChatUtil.sendMessage("Error while calculating §6"+exp, ChatType.FAIL);
        }

        return 1;
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("calc")
            .then(ArgBuilder.argument("exp", StringArgumentType.greedyString())
                .executes(CalcCommand::exec)
            )
        );
    }
}
