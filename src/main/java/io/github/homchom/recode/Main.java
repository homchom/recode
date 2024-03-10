package io.github.homchom.recode;

import net.fabricmc.api.ClientModInitializer;

// keep this file as a .java file!
public final class Main implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.setProperty("java.awt.headless", "false"); // Enable AWT features

		Recode.INSTANCE.initialize();
	}
}