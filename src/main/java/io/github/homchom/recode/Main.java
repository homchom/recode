package io.github.homchom.recode;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

// keep this file as a .java file!
public class Main implements ClientModInitializer, PreLaunchEntrypoint {
	@Override
	public void onInitializeClient() {
		Recode.INSTANCE.initialize();
	}

	@Override
	public void onPreLaunch() {
		MixinExtrasBootstrap.init();
	}
}