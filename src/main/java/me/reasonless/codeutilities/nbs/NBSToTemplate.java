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
	
	public NBSToTemplate(SongData song) {
		
		//These codes are spamming my console!! :(
		//System.out.println(song.getName());
		//System.out.println(song.getNotes());
		//System.out.println(song.getAuthor());
		//System.out.println(song.getFileName());
		
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
	}

	public String convert() {
		String[] songData = song.split("=");
		StringBuilder currentNotes = new StringBuilder();
		StringBuilder code = new StringBuilder();
		StringBuilder currentBlock = new StringBuilder();
		
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
		code.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}]},\"action\":\"CreateList\"},");
		for(String s:songData) {
			if(slot == 26) {
				code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"},", currentBlock.toString()));
				currentBlock.delete(0, currentBlock.length());
				slot = 1;
			}

			if(!(currentNotes.length() < 1900)) {
				currentBlock.append(String.format(", {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", currentNotes.toString(), slot));
				currentNotes.delete(0, currentNotes.length());
				slot++;
			}

			currentNotes.append("=" + s);
		}
		
		if(slot != 26) {
			currentBlock.append(String.format(", {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", currentNotes, slot));
		}else {
			currentBlock.append(String.format(", {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":%d}", currentNotes, 1));
		}

		code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"},", currentBlock.toString()));
		code.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"instrumentNames\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Harp\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass\"}},\"slot\":2},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bass Drum\"}},\"slot\":3},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Snare Drum\"}},\"slot\":4},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Click\"}},\"slot\":5},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Guitar\"}},\"slot\":6},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Flute\"}},\"slot\":7},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bell\"}},\"slot\":8},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Chime\"}},\"slot\":9},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Xylophone\"}},\"slot\":10},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Iron Xylophone\"}},\"slot\":11},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Cow Bell\"}},\"slot\":12},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Didgeridoo\"}},\"slot\":13},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Bit\"}},\"slot\":14},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Banjo\"}},\"slot\":15},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"Pling\"}},\"slot\":16},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #1>\"}},\"slot\":17},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #2>\"}},\"slot\":18},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #3>\"}},\"slot\":19},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #4>\"}},\"slot\":20},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #5>\"}},\"slot\":21},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #6>\"}},\"slot\":22},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #7>\"}},\"slot\":23},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #8>\"}},\"slot\":24},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #9>\"}},\"slot\":25},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"<Custom Instrument #10>\"}},\"slot\":26}]},\"action\":\"CreateList\"},");
		code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"songData\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":2},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%s\"}},\"slot\":3}, {\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":4}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":5}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":6},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":7},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":8}]},\"action\":\"CreateList\"}", name, author, songTempo, length, layers, version, loopTick, loopCount));
		
		return "{\"blocks\": [" + code + "]}";
	}
}
