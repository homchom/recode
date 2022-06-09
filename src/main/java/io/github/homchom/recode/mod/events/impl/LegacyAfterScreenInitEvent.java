package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.Constants;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.BlendableTexturedButtonWidget;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LegacyAfterScreenInitEvent {
	public LegacyAfterScreenInitEvent() {
		ScreenEvents.AFTER_INIT.register(this::afterScreenInit);
	}

	private void afterScreenInit(Minecraft mc, Screen screen, int scaledWidth, int scaledHeight) {
		if (screen instanceof TitleScreen) afterTitleScreenInit(mc, screen);
	}

	private void afterTitleScreenInit(Minecraft mc, Screen screen) {
		final List<AbstractWidget> buttons = Screens.getButtons(screen);
		final int spacing = 24;
		if (Config.getBoolean("dfButton")) addDfButton(mc, screen, buttons, spacing);
	}

	private void addDfButton(Minecraft mc, Screen screen, List<AbstractWidget> buttons, int spacing) {
		final ResourceLocation icon = new ResourceLocation(Constants.MOD_ID + ":textures/ui/df.png");

		int index = -1;
		int y = screen.height / 4 + spacing;
		for (int i = 0; i < buttons.size(); i++) {
			AbstractWidget button = buttons.get(i);
			if (buttonHasText(button, "menu.multiplayer") && button.visible) {
				index = i + 1;
				y = button.y;
			}
		}

		if (index != -1) {
			buttons.add(index, new BlendableTexturedButtonWidget(screen.width / 2 + 104, y, 20, 20, 0, 0, 20, icon, 20, 40,
					(button) -> {
						String address = "mcdiamondfire.com:25565";
						ServerData serverInfo = new ServerData("DF", address, false);
						ConnectScreen.startConnecting(screen, mc, ServerAddress.parseString(address), serverInfo);
					}));
		}
	}

	private boolean buttonHasText(AbstractWidget button, String translationKey) {
		Component text = button.getMessage();
		return text instanceof TranslatableComponent tr && tr.getKey().equals(translationKey);
	}
}
