package me.reasonless.codeutilities;

import java.io.File;

import me.reasonless.codeutilities.schematic.SchematicLoader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;

public class Main implements ModInitializer {
	@Override 
	public void onInitialize() {
		File nbsFolder = new File("CodeUtilities/NBS Files");
		File imagesFolder = new File("CodeUtilities/Images");

		if(!nbsFolder.exists()) {
			nbsFolder.mkdir();
		}

		if(!imagesFolder.exists()) {
			imagesFolder.mkdir();
		}

	}
}
