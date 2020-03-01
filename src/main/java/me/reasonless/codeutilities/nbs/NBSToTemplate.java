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
		
		BigDecimal tempoBD = new BigDecimal(String.valueOf(this.speed));  
		String songTempo = tempoBD.stripTrailingZeros().toPlainString();
		
		if(name.length() == 0) name = filename;
		if(author.length() == 0) author = "N/A";

		code.append(String.format("{\"id\":\"block\",\"block\":\"func\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]},\"data\":\"%s\"}, ", ((name.length() > 12) ? name.substring(0, 12) : name)));
		
		int slot = 1;
		code.append("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}]},\"action\":\"CreateList\"}, ");
		for(String s:songData) {
			if(slot == 26) {
				code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}, ", currentBlock.toString()));
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

		code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"notes\",\"scope\":\"local\"}},\"slot\":0}%s]},\"action\":\"AppendValue\"}, ", currentBlock.toString()));
		code.append(String.format("{\"id\":\"block\",\"block\":\"set_var\",\"args\":{\"items\":[{\"item\":{\"id\":\"var\",\"data\":{\"name\":\"songData\",\"scope\":\"local\"}},\"slot\":0},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":1},{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":2},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%s\"}},\"slot\":3}, {\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":4}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":5}, {\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"%s\"}},\"slot\":6},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":7},{\"item\":{\"id\":\"num\",\"data\":{\"name\":\"%d\"}},\"slot\":8}]},\"action\":\"CreateList\"}", name, author, songTempo, length, layers, version, loopTick, loopCount));
		
		return "{\"blocks\": [" + code + "]}";
	}
}
