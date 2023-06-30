package io.github.homchom.recode;

import net.fabricmc.api.ModInitializer;

// keep this file as a .java file!
public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		Recode.INSTANCE.initialize();
	}
}