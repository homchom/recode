package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.BlendableTexturedButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(TitleScreen.class)
public class MTitleScreen extends Screen {
	private final ResourceLocation icon = new ResourceLocation(Recode.MOD_ID + ":textures/df.png");

	protected MTitleScreen(TextComponent title) {
		super(title);
	}

	@Inject(method = "createNormalMenuOptions", at = @At("TAIL"))
	public void drawMenuButton(int y, int spacingY, CallbackInfo info) {
		if (Config.getBoolean("dfButton")) {
			// Default Server Join
			this.addWidget(new BlendableTexturedButtonWidget(this.width / 2 + 104, y + spacingY, 20, 20, 0, 0, 20, icon, 20, 40,
					(button) -> {
						Minecraft mc = Minecraft.getInstance();
						String address = "mcdiamondfire.com:25565";
						ServerData serverInfo = new ServerData("DF", address, false);
						ConnectScreen.startConnecting(mc.screen, mc, ServerAddress.parseString(address), serverInfo);
					}));
		}
	}
}
