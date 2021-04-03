package io.github.codeutilities.mixin.screen;

import io.github.codeutilities.config.JereConfig;
import io.github.codeutilities.config.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class MixinOptionsScreen extends Screen {

    public MixinOptionsScreen(LiteralText literalText) {
        super(literalText);
    }

    @Inject(method = "init()V", at = @At("RETURN"))
    protected void init(CallbackInfo callbackInfo) {
        String uuid = MinecraftClient.getInstance().getSession().getUuid();
        if (uuid.equals("6c669475-3026-4603-b3e7-52c97681ad3a")
                || uuid.equals("3134fb4d-a345-4c5e-9513-97c2c951223e")) {
            this.addButton(new ButtonWidget(this.width / 4 - 75, this.height / 6 + 144 - 6, 150, 20, new LiteralText("CodeUtilities"), (buttonWidget) -> {
                MinecraftClient.getInstance()
                        .openScreen(AutoConfig.getConfigScreen(ModConfig.class, this).get());
            }));
            this.addButton(new ButtonWidget((this.width / 4) * 3 - 75, this.height / 6 + 144 - 6, 150, 20, new LiteralText("Jeremaster"), (buttonWidget) -> {
                MinecraftClient.getInstance()
                        .openScreen(AutoConfig.getConfigScreen(JereConfig.class, this).get());
            }));
        } else {
            this.addButton(new ButtonWidget(this.width / 2 - 75, this.height / 6 + 144 - 6, 150, 20, new LiteralText("CodeUtilities"), (buttonWidget) -> {
                MinecraftClient.getInstance()
                        .openScreen(AutoConfig.getConfigScreen(ModConfig.class, this).get());
            }));
        }
    }
}
