package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.ModConstants;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.render.ScreenEvents;
import io.github.homchom.recode.render.ScreenInitContext;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class LegacyAfterScreenInitEvent {
	public LegacyAfterScreenInitEvent() {
		ScreenEvents.getAfterInitScreenEvent().register(this::afterScreenInit);
	}

	private void afterScreenInit(ScreenInitContext context) {
		if (context.getScreen() instanceof TitleScreen) {
			afterTitleScreenInit(context.getClient(), context.getScreen());
		}
	}

	private void afterTitleScreenInit(Minecraft mc, Screen screen) {
		final List<AbstractWidget> buttons = Screens.getButtons(screen);
		final int spacing = 24;
		if (LegacyConfig.getBoolean("dfButton")) addDfButton(mc, screen, buttons, spacing);
	}

	private void addDfButton(Minecraft mc, Screen screen, List<AbstractWidget> buttons, int spacing) {
		final ResourceLocation icon = new ResourceLocation(ModConstants.MOD_ID, "icon/df");

		int index = -1;
		int y = 0;
		for (int i = 0; i < buttons.size(); i++) {
			AbstractWidget button = buttons.get(i);
			if (buttonHasText(button, "menu.multiplayer") && button.visible) {
				index = i + 1;
				y = button.getY();
				break;
			}
		}
		if (index == -1) return;

		var name = Component.translatable("menu.join_df");
		var button = SpriteIconButton.builder(name, b -> joinDF(screen, mc), true)
				.width(20)
				.sprite(icon, 15, 15)
				.build();
		button.setPosition(screen.width / 2 + 104, y);

		buttons.add(index, button);
	}

	private void joinDF(Screen currentScreen, Minecraft mc) {
		String address = "mcdiamondfire.com:25565";
		ServerData serverInfo = new ServerData("DiamondFire", address, ServerData.Type.OTHER);
		serverInfo.setEnforcesSecureChat(false);
		ConnectScreen.startConnecting(currentScreen, mc, ServerAddress.parseString(address), serverInfo, false);
	}

	private boolean buttonHasText(AbstractWidget button, String translationKey) {
		ComponentContents content = button.getMessage().getContents();
		return content instanceof TranslatableContents tr && tr.getKey().equals(translationKey);
	}
}
