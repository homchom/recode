package io.github.codeutilities.mod.mixin.render.screen;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.menu.ConfigScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.screen.options.OptionsScreen.class)
public class MOptionsScreen extends Screen {

    public MOptionsScreen(LiteralText literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addButton(new ButtonWidget(this.width / 2 - 75, this.height / 6 + 144 - 6, 150, 20, new LiteralText("CodeUtilities"), (buttonWidget) -> CodeUtilities.MC.openScreen(ConfigScreen.getScreen(CodeUtilities.MC.currentScreen))));
    }
}
