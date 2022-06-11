package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.menu.ConfigScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screens.OptionsScreen.class)
public class MOptionsScreen extends Screen {

    public MOptionsScreen(TextComponent literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addRenderableWidget(new Button(this.width / 2 - 75, this.height / 6 + 144 - 6, 150, 20, new TextComponent("Recode"), (buttonWidget) -> LegacyRecode.MC.setScreen(ConfigScreen.getScreen(LegacyRecode.MC.screen))));
    }
}
