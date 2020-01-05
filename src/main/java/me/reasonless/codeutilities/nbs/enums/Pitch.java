package me.reasonless.codeutilities.nbs.enums;

public enum Pitch {
	NULL(0, "null"),
	
	FS0(33, "F#0"),
	G0(34, "G0"),
	GS0(35, "G#0"),
	
	A1(36, "A1"),
	AS1(37, "A#1"),
	B1(38, "B1"),
	C1(39, "C1"),
	CS1(40, "C#1"),
	D1(41, "D1"),
	DS1(42, "D#1"),
	E1(43, "E1"),
	F1(44, "F1"),
	FS1(45, "F#1"),
	G1(46, "G1"),
	GS1(47, "G#1"),
	
	A2(48, "A2"),
	AS2(49, "A#2"),
	B2(50, "B2"),
	C2(51, "C2"),
	CS2(52, "C#2"),
	D2(53, "D2"),
	DS2(54, "D#2"),
	E2(55, "E2"),
	F2(56, "F2"),
	FS2(57, "F#2");

	int nbspitch;
	String dfpitch;
	
	Pitch(int nbspitch, String dfpitch){
		this.nbspitch = nbspitch;
		this.dfpitch = dfpitch;
	}
	
	public static Pitch getNBSPitch(int pitch) {
		for(Pitch v:Pitch.values()) {
			if(v.nbspitch == pitch) {
				return v;
			}
		}
		return NULL;
	}
	
	public String getDFPitch() {
		return dfpitch;
	}
	
	
}
