package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.GridWidget;
import net.minecraft.client.gui.components.LayoutSettings;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OptionsScreen.class)
public class MOptionsScreen extends Screen {

    public MOptionsScreen(Component literalText) {
        super(literalText);
    }

    @Redirect(method = "init()V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/GridWidget$RowHelper;addChild(Lnet/minecraft/client/gui/components/AbstractWidget;ILnet/minecraft/client/gui/components/LayoutSettings;)Lnet/minecraft/client/gui/components/AbstractWidget;",
            ordinal = 0
    ))
    protected <T extends AbstractWidget> T init(
            GridWidget.RowHelper instance,
            T abstractWidget,
            int i,
            LayoutSettings layoutSettings
    ) {
        var button = Button.builder(
                Component.literal("Recode"),
                buttonWidget -> LegacyRecode.MC.setScreen(ConfigScreen.getScreen(LegacyRecode.MC.screen))
        );
        instance.addChild(button.build());
        return instance.addChild(abstractWidget, i, layoutSettings);
    }
}
