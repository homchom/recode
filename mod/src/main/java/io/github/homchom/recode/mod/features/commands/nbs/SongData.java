package io.github.homchom.recode.mod.features.commands.nbs;

public class SongData {

    final int length;
    final String layers;
    final int loopTick;
    final int loopCount;
    final int customInstrumentCount;
    String name;
    String author;
    float speed;
    String fileName;
    String notes;

    public SongData(String name, String author, float speed, int length, String notes, String fileName, String layers, int loopTick, int loopCount, int customInstrumentCount) {
        this.name = name;
        this.author = author;
        this.speed = speed;
        this.length = length;
        this.notes = notes;
        this.fileName = fileName;
        this.layers = layers;
        this.loopTick = loopTick;
        this.loopCount = loopCount;
        this.customInstrumentCount = customInstrumentCount;
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

    public int getLength() {
        return length;
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

    public int getLoopTick() {
        return loopTick;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public int getCustomInstrumentCount() {
        return customInstrumentCount;
    }
}
