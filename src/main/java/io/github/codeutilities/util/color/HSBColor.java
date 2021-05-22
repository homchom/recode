package io.github.codeutilities.util.color;

public class HSBColor {
    private float hue;
    private float saturation;
    private float brightness;

    public HSBColor(int h, int s, int b) {
        this.hue = h;
        this.saturation = s;
        this.brightness = b;
    }

    public HSBColor(float[] hsb) {
        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];
    }

    public String toString() {
        return "HSB(" + hue + ", " + saturation + ", " + brightness + ")";
    }

    public boolean isGrayscale() {
        return saturation == 0 || brightness == 0;
    }

    public float getBrightness() {
        return brightness;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setBrightness(float b) {
        this.brightness = b;
    }

    public void setHue(float h) {
        this.hue = h;
    }

    public void setSaturation(float s) {
        this.saturation = s;
    }
}
