package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.OptionsScreen.class)
public class MOptionsScreen extends Screen {

    public MOptionsScreen(Component literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 6 + 144 - 6, 150, 20, Component.literal("Recode"), (buttonWidget) -> LegacyRecode.MC.setScreen(ConfigScreen.getScreen(LegacyRecode.MC.screen))));
    }
}
