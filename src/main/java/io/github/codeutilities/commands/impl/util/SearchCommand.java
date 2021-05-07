package io.github.codeutilities.commands.impl.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.commands.sys.Command;
import io.github.codeutilities.commands.sys.arguments.ArgBuilder;
import io.github.codeutilities.commands.sys.arguments.types.StringListArgumentType;
import io.github.codeutilities.util.networking.DFInfo;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.templates.SearchUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class SearchCommand extends Command {
    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("search")
            .then(ArgBuilder.literal("clear")
                .executes(ctx -> { // /search clear
                    ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.cleared"), ChatType.SUCCESS);
                    SearchUtil.clearSearch();
                    return 1;
                }))
            .then(ArgBuilder.argument("codeblock", StringListArgumentType.string(SearchUtil.SearchType.STRINGS))
                .then(ArgBuilder.argument("action", StringArgumentType.greedyString())
                    .executes(ctx -> { // /search <codeblock> <action..>
                        try {
                            SearchUtil.SearchType searchType = SearchUtil.SearchType.valueOf(ctx.getArgument("codeblock", String.class).toUpperCase());
                            String action = ctx.getArgument("action", String.class);
                            if (DFInfo.isOnDF() && DFInfo.currentState == DFInfo.State.DEV && mc.player.isCreative()) {
                                SearchUtil.beginSearch(searchType, action);
                            }else {
                                ChatUtil.sendMessage(new TranslatableText("codeutilities.command.require_dev_mode").parse(mc.player.getCommandSource(), mc.player, 1), ChatType.FAIL);
                            }

                        }catch (IllegalArgumentException e) {
                            StringBuilder stringBuilder = new StringBuilder();

                            for(SearchUtil.SearchType searchType:SearchUtil.SearchType.values()) {
                                if(stringBuilder.length() > 0) stringBuilder.append(", ");
                                stringBuilder.append(searchType.toString());
                            }

                            ChatUtil.sendMessage(new TranslatableText("codeutilities.template_search.invalid_usage", stringBuilder.toString()).parse(mc.player.getCommandSource(), mc.player, 1), ChatType.FAIL);
                        }
                        return 1;

                    }))));
    }
}
