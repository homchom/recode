package io.github.codeutilities.commands.item.template;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.TemplateUtils;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.templates.TemplateUtils;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public abstract class AbstractTemplateCommand extends Command {


    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal(getName())
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandStack();
                    if (TemplateUtils.isTemplate(item)) {
                        withTemplate(item);
                        return 1;
                    } else {
                        ChatUtil.sendMessage("This item is not a template!", ChatType.FAIL);
                        return -1;
                    }
                })
        );
    }

    protected abstract String getName();

    protected abstract void withTemplate(ItemStack stack);
}
