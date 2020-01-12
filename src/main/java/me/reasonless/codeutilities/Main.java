package me.reasonless.codeutilities;

import me.reasonless.codeutilities.templates.Template;
import me.reasonless.codeutilities.templates.variables.TextVariable;
import net.fabricmc.api.ModInitializer;

import java.io.File;

public class Main implements ModInitializer {
	
	public static final String MOD_VERSION = "1.2"; //CodeUtilities mod version
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

		Template template = new Template("Testing", "Reasonless");
		new TextVariable("test");


	}
}
