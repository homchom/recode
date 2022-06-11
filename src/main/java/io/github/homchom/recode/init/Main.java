package io.github.homchom.recode.init;

import io.github.homchom.recode.Recode;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {
	@Override
	public void onInitialize() {
		ModuleInit.init(new Recode());
	}
}
