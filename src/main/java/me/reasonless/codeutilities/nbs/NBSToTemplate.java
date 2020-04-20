package me.reasonless.codeutilities.nbs;

import java.math.BigDecimal;

import me.reasonless.codeutilities.Main;

public class NBSToTemplate {
	String song;
	String name;
	String author;
	String filename;
	String layers;
	String version;
	float speed;
	int length;
	int loopTick;
	int loopCount;
	int customInstrumentCount;
	boolean multipleChests = false;
	
	public NBSToTemplate(SongData song) {
		
		this.version = "v" + Main.PARSER_VERSION + "-nbs" + Main.NBS_FORMAT_VERSION;

		this.song = song.getNotes();
		this.author = song.getAuthor();
		this.name = song.getName();
		this.filename = song.getFileName();
		this.layers = song.getLayers();
		this.length = song.getLength();
		this.speed = song.getSpeed();
		this.loopTick = song.getLoopTick();
		this.loopCount = song.getLoopCount();
		this.customInstrumentCount = song.getCustomInstrumentCount();
	}

	public String convert() {
		String[] songData = song.split("=");
		StringBuilder currentNotes = new StringBuilder();
		StringBuilder code = new StringBuilder();
		StringBuilder currentBlock = new StringBuilder();
		StringBuilder instList = new StringBuilder();
		
		String songTempo = new BigDecimal(this.speed).stripTrailingZeros().toPlainString(); 
		
		if(name.length() == 0) {
			if (filename.indexOf(".") > 0) {
				name = filename.substring(0, filename.lastIndexOf("."));
			} else {
				name = filename;
			}
		}
		if(author.length() == 0) author = "N/A";

		code.append(String.format("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"%s\"},", name));
		
		int slot = 1;
		int index = 1;
		int chestCount = 1;
		boolean chestInited = false;
		int noteCount = 0;
		boolean finalNote = false;
		
		for(String s:songData) {
			boolean closeChest = false;
			if (slot==1) {
				if (!chestInited) {
					chestInited = true;
					currentBlock.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}");
				}
			}
			
			if (slot >= 27) {
				closeChest = true;
			}
			
			if (!closeChest) {
				if (noteCount==0) {
					currentNotes.append(s);
				}else {
					currentNotes.append("=" + s);
				}
				noteCount++;
				
				if(currentNotes.length() > 1900) {
					currentBlock.append(String.format(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", currentNotes.toString(), slot));
					currentNotes.setLength(0);
					noteCount = 0;
					finalNote = true;
					slot++;
				}
				
				if (index >= songData.length) {
					if (!finalNote) {
						currentBlock.append(String.format(",{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", currentNotes.toString(), slot));
						currentNotes.setLength(0);
					}
					closeChest = true;
				}
				finalNote = false;
			}
			if (closeChest) {
				String varActionType;
				if (chestCount==1) {
					//System.out.println("This is the first chest. Setting var type to CreateList...");
					varActionType = "CreateList";
				}else {
					//System.out.println("This is not the first chest. Setting var type to AppendValue...");
					varActionType = "AppendValue";
				}
				
				//System.out.println("Closing the Chest #" + chestCount + " with vartype " + varActionType + ".");
				currentBlock.append(String.format("]},\"action\":\"%s\"},", varActionType));
				code.append(currentBlock.toString());
				currentBlock.setLength(0);
				currentNotes.setLength(0);
				
				chestInited = false;
				noteCount = 0;
				finalNote = false;
				chestCount++;
				closeChest = false;
				slot = 1;
			}
			
			index++;
		}
		
		//CreateList: instrumentNames
		if (customInstrumentCount == 0) {
			code.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"instrumentNames\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Harp\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass\"}},\"slot\":2},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass Drum\"}},\"slot\":3},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Snare Drum\"}},\"slot\":4},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Click\"}},\"slot\":5},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Guitar\"}},\"slot\":6},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Flute\"}},\"slot\":7},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bell\"}},\"slot\":8},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Chime\"}},\"slot\":9},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Xylophone\"}},\"slot\":10},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Iron Xylophone\"}},\"slot\":11},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Cow Bell\"}},\"slot\":12},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Didgeridoo\"}},\"slot\":13},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bit\"}},\"slot\":14},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Banjo\"}},\"slot\":15},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Pling\"}},\"slot\":16}]},\"action\":\"CreateList\"},");
		}else {
			instList.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"instrumentNames\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Harp\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass\"}},\"slot\":2},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass Drum\"}},\"slot\":3},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Snare Drum\"}},\"slot\":4},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Click\"}},\"slot\":5},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Guitar\"}},\"slot\":6},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Flute\"}},\"slot\":7},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bell\"}},\"slot\":8},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Chime\"}},\"slot\":9},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Xylophone\"}},\"slot\":10},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Iron Xylophone\"}},\"slot\":11},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Cow Bell\"}},\"slot\":12},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Didgeridoo\"}},\"slot\":13},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bit\"}},\"slot\":14},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Banjo\"}},\"slot\":15},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Pling\"}},\"slot\":16},");
			
			int currentSlot;
			
			currentSlot = 17;
			for (int currentInstID = 1 ; currentInstID <= customInstrumentCount ; currentInstID++) {
				if (currentInstID == customInstrumentCount) {
					instList.append(String.format("{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #%d>\"}},\"slot\":%d}", currentInstID, currentSlot));
				}else {
					instList.append(String.format("{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #%d>\"}},\"slot\":%d},", currentInstID, currentSlot));
				}
				currentSlot++;
			}			
			instList.append("]},\"action\":\"CreateList\"},");
			code.append(instList.toString());
		}		
		
		//CreateList: songData
		code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"songData\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":2},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%s\"}},\"slot\":3}, {\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":4}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":5}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":6},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":7},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":8}]},\"action\":\"CreateList\"}", name, author, songTempo, length, layers, version, loopTick, loopCount));
		
		return "{\"blocks\": [" + code + "]}";
	}
}
