package io.github.codeutilities.mixin;

import io.github.prospector.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModsScreen.class)
public class LibraryFix extends Screen {


    // This fixes a crash when looking at certain libraries. By simply, removing the library button.
    protected LibraryFix(Text title) {
        super(title);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        for (AbstractButtonWidget buttonWidget: this.buttons) {
            if (buttonWidget.getMessage().getString().startsWith("Libraries")) {
                // Removing it won't work for some odd reason. I really don't wanna look into this rn.
                buttonWidget.active = false;
            }
        }
    }
}
