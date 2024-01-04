package io.github.homchom.recode;

import net.fabricmc.api.ClientModInitializer;

// keep this file as a .java file!
public final class Main implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Recode.INSTANCE.initialize();
	}
}