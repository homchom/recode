package io.github.codeutilities.sys.image;

public class ParticleImage {
    final String[] imageData;
    final int imageWidth;
    final int imageHeight;

    public ParticleImage(String[] imageData, int imageWidth, int imageHeight) {
        this.imageData = imageData;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String[] getData() {
        return imageData;
    }

    public int getWidth() {
        return imageWidth;
    }

    public int getHeight() {
        return imageHeight;
    }
}
