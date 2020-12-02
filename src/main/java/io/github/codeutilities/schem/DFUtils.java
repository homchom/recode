package io.github.codeutilities.schem;

import java.util.ArrayList;
import java.util.List;

import io.github.codeutilities.schem.Schematic;
import io.github.codeutilities.schem.utils.DFText;
import io.github.codeutilities.schem.utils.DFNumber;

public class DFUtils {
	
	public static String GenerateFunctionHeader(String functionName) {
		return "{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[]},\"data\":\"" + functionName + "\"}";
	}

	public static String GeneratePaletteList(DFText... texts) {
		ArrayList<String> jsons = new ArrayList<>();

		for (int actionIndex = 0; actionIndex < Math.ceil(texts.length / 26f); actionIndex++) {
			String json = "{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[";
			List<String> itemsJSON = new ArrayList<>();

			itemsJSON.add("{\"item\":" + "{\"id\":\"var\",\"data\":{\"name\":\"Palette\",\"scope\":\"local\"}}" + ",\"slot\":" + 0 + "}");

			for (int i = (actionIndex * 26); i < ((actionIndex + 1) * 26); i++) {
				if (texts.length == i) break;
				itemsJSON.add("{\"item\":" + texts[i].asJson() + ",\"slot\":" + ((i % 26) + 1) + "}");
			}

			json += String.join(",", itemsJSON);
			if (actionIndex == 0) json += "]},\"action\":\"" + "CreateList" + "\"}";
			else json += "]},\"action\":\"" + "AppendValue" + "\"}";
			jsons.add(json);
		}

		return String.join(",", jsons);
	}

	public static String GenerateBlockDataList(DFText... texts) {
		ArrayList<String> jsons = new ArrayList<>();
		for (int blockDataIndex = 1; blockDataIndex <= Math.ceil(texts.length / 10000f); blockDataIndex++) {
			int actionIndexLimit = (Math.ceil(texts.length / 26f) > 384) ? 384 : (int)Math.ceil(texts.length / 26f);

			for (int actionIndex = 1; actionIndex <= actionIndexLimit; actionIndex++) {
				String json = "{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[";
				List<String> itemsJSON = new ArrayList<>();

				itemsJSON.add("{\"item\":" + "{\"id\":\"var\",\"data\":{\"name\":\"BlockData" + blockDataIndex + "\",\"scope\":\"local\"}}" + ",\"slot\":" + 0 + "}");

				for (int i = ((actionIndex - 1) * 26); i < (actionIndex * 26); i++) {
					if (texts.length == i) break;
					itemsJSON.add("{\"item\":" + texts[i].asJson() + ",\"slot\":" + ((i % 26) + 1) + "}");
				}

				json += String.join(",", itemsJSON);
				if (actionIndex == 1) json += "]},\"action\":\"" + "CreateList" + "\"}";
				else json += "]},\"action\":\"" + "AppendValue" + "\"}";
				jsons.add(json);
			}
		}
		
		return String.join(",", jsons);
	}
	
	public static String GenerateSchematicData(Schematic schematic) {
		String json = "{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[";
		
		json += "{\"item\":" + "{\"id\":\"var\",\"data\":{\"name\":\"SchemData\",\"scope\":\"local\"}}" + ",\"slot\":0},";
		json += "{\"item\":" + new DFText(schematic.name).asJson() + ",\"slot\":1},";
		json += "{\"item\":" + new DFText(schematic.author).asJson() + ",\"slot\":2},";
		json += "{\"item\":" + new DFText(schematic.description).asJson() + ",\"slot\":3},";
		json += "{\"item\":" + new DFNumber(schematic.creationTime).asJson() + ",\"slot\":4},";
		json += "{\"item\":" + new DFNumber(schematic.lastModified).asJson() + ",\"slot\":5},";
		json += "{\"item\":" + new DFNumber(schematic.getBlocksCount()).asJson() + ",\"slot\":6},";
		json += "{\"item\":" + new DFNumber(schematic.getDimensions().getX()).asJson() + ",\"slot\":7},";
		json += "{\"item\":" + new DFNumber(schematic.getDimensions().getY()).asJson() + ",\"slot\":8},";
		json += "{\"item\":" + new DFNumber(schematic.getDimensions().getZ()).asJson() + ",\"slot\":9},";
		json += "{\"item\":" + new DFNumber(schematic.getOffset().getX()).asJson() + ",\"slot\":10},";
		json += "{\"item\":" + new DFNumber(schematic.getOffset().getY()).asJson() + ",\"slot\":11},";
		json += "{\"item\":" + new DFNumber(schematic.getOffset().getZ()).asJson() + ",\"slot\":12},";
		json += "{\"item\":" + new DFText(schematic.fileType).asJson() + ",\"slot\":13},";
		json += "{\"item\":" + new DFNumber(schematic.getListAmount()).asJson() + ",\"slot\":14}";

		json += "]},\"action\":\"" + "CreateList" +"\"}";
		
		return json;
	}
	
	public static String GenerateSchematicFunction(Schematic schematic) {
		String functionHeader = DFUtils.GenerateFunctionHeader(schematic.name);
		String schemDataJson = DFUtils.GenerateSchematicData(schematic);
		String paletteJson = GeneratePaletteList(schematic.getPaletteTexts());
		String blocksJson = GenerateBlockDataList(schematic.getBlocksTexts());

		return "{\"blocks\":[" + functionHeader + "," + schemDataJson + "," + paletteJson + "," + blocksJson + "]}";
	}
}