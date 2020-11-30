package io.github.codeutilities.schem;

import io.github.codeutilities.schem.sk89q.worldedit.math.BlockVector3;
import io.github.codeutilities.schem.utils.DFText;

import java.util.ArrayList;
import java.util.List;

public class DFUtils {
	
	public static String GenerateFunctionHeader(String functionName) {
		return "{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[]},\"data\":\"" + functionName + "\"}";
	}
	public static String GenerateTextList(String listName, DFText... texts) {
		ArrayList<String> jsons = new ArrayList<>();
		for(int actionIndex = 0; actionIndex < Math.ceil(texts.length / 26f); actionIndex++) {
			String json = "{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[";
			List<String> itemsJSON = new ArrayList<String>();
			
			itemsJSON.add("{\"item\":" + "{\"id\":\"var\",\"data\":{\"name\":\"" + listName + "\",\"scope\":\"local\"}}" + ",\"slot\":" + 0 + "}");
			
			for (int i = (actionIndex * 26); i < ((actionIndex + 1) * 26); i++) {
				if(texts.length == i) break;
				itemsJSON.add("{\"item\":" + texts[i].asJson() + ",\"slot\":" + ((i % 26) + 1) + "}");
			}
			
			json += String.join(",", itemsJSON);
			if(actionIndex == 0) json += "]},\"action\":\"" + "CreateList" +"\"}";
			else json += "]},\"action\":\"" + "AppendValue" +"\"}";
			jsons.add(json);
		}
		
		return String.join(",", jsons);
	}
	
	public static String GenerateSchematicData(String schematicName, BlockVector3 size, BlockVector3 offset) {
		String json = "{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[";
		List<String> itemsJSON = new ArrayList<String>();
		
		itemsJSON.add("{\"item\":" + "{\"id\":\"var\",\"data\":{\"name\":\"SchemData\",\"scope\":\"local\"}}" + ",\"slot\":" + 0 + "}");
		itemsJSON.add("{\"item\":" + new DFText(schematicName).asJson() + ",\"slot\":1}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + size.getX() + "\"}}" + ",\"slot\":2}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + size.getY() + "\"}}" + ",\"slot\":3}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + size.getZ() + "\"}}" + ",\"slot\":4}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + offset.getX() + "\"}}" + ",\"slot\":5}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + offset.getY() + "\"}}" + ",\"slot\":6}");
		itemsJSON.add("{\"item\":" + "{\"id\":\"num\",\"data\":{\"name\":\"" + offset.getZ() + "\"}}" + ",\"slot\":7}");
		
		json += String.join(",", itemsJSON);
		json += "]},\"action\":\"" + "CreateList" +"\"}";
		
		return json;
	}
	
	public static String GenerateSchematicFunction(Schematic schematic, String schematicName) {
		String functionHeader = DFUtils.GenerateFunctionHeader(schematicName);
		String schemDataJson = DFUtils.GenerateSchematicData(schematicName, schematic.getDimensions(), schematic.getOffset());
		String paletteJson = DFUtils.GenerateTextList("Palette", schematic.getPaletteTexts());
		String blocksJson = DFUtils.GenerateTextList("BlockData", schematic.getBlocksTexts());
		
		return "{\"blocks\":[" + functionHeader + "," + schemDataJson + "," + paletteJson + "," + blocksJson + "]}";
	}
}
