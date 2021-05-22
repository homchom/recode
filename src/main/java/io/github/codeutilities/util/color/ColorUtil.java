package io.github.codeutilities.util.color;

import java.awt.*;
import java.util.ArrayList;

public class ColorUtil {
    public static ArrayList<Color> recentColors = new ArrayList<>(); //for colors gui

    public static Color invertColor(Color color){
        return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
    }

    public static HSBColor toHSB(Color color){
        return new HSBColor(Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null));
    }
}
