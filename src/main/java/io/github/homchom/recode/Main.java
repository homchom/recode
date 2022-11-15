package io.github.homchom.recode;

import net.fabricmc.api.ModInitializer;

// Keep this file as a .java file!
public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		Recode.getRecodeMod().enable();
	}
}