package io.github.codeutilities.images;

public class ParticleImage {
    String[] imageData;
    int imageWidth;
    int imageHeight;

    public ParticleImage(String[] imageData, int imageWidth, int imageHeight) {
        this.imageData = imageData;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String[] getData() {return imageData;}

    public int getWidth() {return imageWidth;}

    public int getHeight() {return imageHeight;}
}
