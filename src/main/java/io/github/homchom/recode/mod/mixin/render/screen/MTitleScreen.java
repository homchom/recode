package io.github.homchom.recode.mod.mixin.render.screen;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.BlendableTexturedButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(TitleScreen.class)
public class MTitleScreen extends Screen {

	// Valid positions: "top_left" && "bottom_right".
	// DM 8Blits if you would like to have the numbers be in another position, or if you want other texture changes.
	private static final String NUM_LOCATION = "top_left";

	protected MTitleScreen(TextComponent title) {
		super(title);
	}

	@Inject(method = "createNormalMenuOptions", at = @At("RETURN"))
	public void drawMenuButton(int y, int spacingY, CallbackInfo info) {
		if (Config.getBoolean("dfButton")) {
			if (!Config.getBoolean("dfNodeButtons")) {
				// Default Server Join
				this.addWidget(new BlendableTexturedButtonWidget(this.width / 2 + 104, y + spacingY, 20, 20, 0, 0, 20, null, 20, 40,
						(button) -> {
							Minecraft mc = Minecraft.getInstance();
							String address = "mcdiamondfire.com:25565";
							ServerData serverInfo = new ServerData("DF", address, false);
							ConnectScreen.startConnecting(mc.screen, mc, ServerAddress.parseString(address), serverInfo);
						}));
			}
		}
	}
}
