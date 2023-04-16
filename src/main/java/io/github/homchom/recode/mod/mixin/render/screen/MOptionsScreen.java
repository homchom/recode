package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LayoutSettings;
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
            target = "Lnet/minecraft/client/gui/layouts/GridLayout$RowHelper;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;ILnet/minecraft/client/gui/layouts/LayoutSettings;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
            ordinal = 0
    ))
    protected <T extends LayoutElement> T init(
            GridLayout.RowHelper instance,
            T layoutElement,
            int i,
            LayoutSettings layoutSettings
    ) {
        var button = Button.builder(
                Component.literal("Recode"),
                buttonWidget -> LegacyRecode.MC.setScreen(ConfigScreen.getScreen(LegacyRecode.MC.screen))
        );
        instance.addChild(button.build());
        return instance.addChild(layoutElement, i, layoutSettings);
    }
}
