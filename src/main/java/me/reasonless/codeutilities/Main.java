package me.reasonless.codeutilities;

import me.reasonless.codeutilities.schematic.SchematicLoader;
import net.fabricmc.api.ModInitializer;

import java.io.File;

public class Main implements ModInitializer {
	
	public static final String MOD_VERSION = "1.3"; //CodeUtilities mod version
	
	public static final String PARSER_VERSION = "2"; //NBS parser version
	public static final String NBS_FORMAT_VERSION = "4"; //NBS format version
	
	@Override 
	public void onInitialize() {
		File codeUtilities = new File("CodeUtilities");
		File nbsFolder = new File("CodeUtilities/NBS Files");
		File imagesFolder = new File("CodeUtilities/Images");

		if(!codeUtilities.exists()) {
			codeUtilities.mkdir();
		}

		if(!nbsFolder.exists()) {
			nbsFolder.mkdir();
		}

		if(!imagesFolder.exists()) {
			imagesFolder.mkdir();
		}

		new SchematicLoader();



	}
}
