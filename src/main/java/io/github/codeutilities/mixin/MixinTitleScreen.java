package io.github.codeutilities.mixin;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {
    Identifier identifier = new Identifier(CodeUtilities.MOD_ID + ":df.png");

    protected MixinTitleScreen(LiteralText title) {
        super(title);

    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    public void drawMenuButton(int y, int spacingY, CallbackInfo info) {
        if(ModConfig.getConfig().playDiamondFire) {
            this.addButton(new TexturedButtonWidget(this.width / 2 - 100 + 205, y + spacingY, 20, 20, 0, 0, 20, identifier, 20, 40, button -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                ServerInfo serverInfo = new ServerInfo("DF", "mcdiamondfire.com:25565", false);
                mc.openScreen(new ConnectScreen(mc.currentScreen, mc, serverInfo));
            }));
        }
    }

    protected <T extends AbstractButtonWidget> T addButton(T button) {
        if (button.y <= this.height / 4 + 48 + 24 * 3) {
            button.y -= 12;
        }
        if (button.y > this.height / 4 + 48 + 24 * 3) {
            button.y += 12;
        }
        return super.addButton(button);
    }
}
