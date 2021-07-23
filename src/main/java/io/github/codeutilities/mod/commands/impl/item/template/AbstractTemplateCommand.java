package io.github.codeutilities.mod.commands.impl.item.template;

import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.mod.commands.Command;
import io.github.codeutilities.mod.commands.arguments.ArgBuilder;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import io.github.codeutilities.sys.templates.TemplateUtils;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public abstract class AbstractTemplateCommand extends Command {


    @Override
    public void register(MinecraftClient mc, CommandDispatcher<FabricClientCommandSource> cd) {
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
