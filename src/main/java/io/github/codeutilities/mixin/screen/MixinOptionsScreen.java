package io.github.codeutilities.mixin.screen;

import io.github.codeutilities.config.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {

    public MixinOptionsScreen(LiteralText literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        this.addButton(new ButtonWidget(this.width / 2 - 75, this.height / 6 + 144 - 6, 150, 20, new LiteralText("CodeUtilities"), (buttonWidget) -> {
            MinecraftClient.getInstance()
                    .openScreen(AutoConfig.getConfigScreen(ModConfig.class, this).get());
        }));
    }
}
