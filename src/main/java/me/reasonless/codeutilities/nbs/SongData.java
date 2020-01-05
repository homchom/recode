package me.reasonless.codeutilities.nbs;

public class SongData {
	String name;
	String author;
	float speed;
	String fileName;
	String notes;
	String layers;
	
	public SongData(String name, String author, float speed, String notes, String fileName, String layers) {
		this.name = name;
		this.author = author;
		this.speed = speed;
		this.notes = notes;
		this.fileName = fileName;
		this.layers = layers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLayers() {
		return layers;
	}
}
