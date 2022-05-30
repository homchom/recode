package io.github.homchom.recode.mod.commands.impl.item.template;

import com.mojang.brigadier.CommandDispatcher;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.player.chat.*;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractTemplateCommand extends Command {


    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal(getCmdName())
                .executes(ctx -> {
                    ItemStack item = mc.player.getMainHandItem();
                    if (TemplateUtil.isTemplate(item)) {
                        withTemplate(item);
                        return 1;
                    } else {
                        ChatUtil.sendMessage("This item is not a template!", ChatType.FAIL);
                        return -1;
                    }
                })
        );
    }

    protected abstract String getCmdName();

    protected abstract void withTemplate(ItemStack stack);
}
