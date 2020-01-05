package me.reasonless.codeutilities.nbs.enums;

public enum Instrument {
	HARP("Harp"), 
	DOUBLE_BASS("Bass"), 
	BASS_DRUM("Bass Drum"), 
	SNARE_DRUM("Snare Drum"), 
	CLICK("Click"), 
	GUITAR("Guitar"), 
	FLUTE("Flute"),
	BELL("Bell"), 
	CHIME("Chime"),
	XYLOPHONE("Xylophone"), 
	IRON_XYLOPHONE("Iron Xylophone"), 
	COW_BELL("Cow Bell"), 
	DIDGERIDOO("Didgeridoo"), 
	BIT("Bit"), 
	BANJO("Banjo"), 
	PLING("Pling");
	
	String df;
	Instrument(String df) {
		this.df = df;
	}
	
	public String getDF() {
		return df;
	}
}
