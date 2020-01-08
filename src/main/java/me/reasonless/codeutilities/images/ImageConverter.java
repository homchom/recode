package me.reasonless.codeutilities.images;

import me.reasonless.codeutilities.util.MinecraftColors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;

public class ImageConverter {
    public static String[] convert(File file) {
        List<String> layers = new ArrayList<>();
        if(file.exists()) {
            try {
                BufferedImage image = ImageIO.read(file);

                int width = image.getWidth();
                int height = image.getHeight();

                if(width > 64 || height > 64) {
                    height = 64;
                    width = 64;
                    BufferedImage newImage = new BufferedImage(64, 64, image.getType());
                    Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();
                    graphics2D.drawImage(image.getScaledInstance(64, 64, SCALE_SMOOTH), 0, 0, null);
                    graphics2D.dispose();
                    image = newImage;
                }

                for (int i = 0; i < height; i++) {
                    String layer = "";
                    for (int j = 0; j < width; j++) {
                        int rgb = image.getRGB(j, i);
                        Color color = new Color(rgb, true);

                        List<MinecraftColors> colors = new ArrayList<>();
                        colors.addAll(Arrays.asList(MinecraftColors.values()));



                        Comparator<MinecraftColors> comparator = (o1, o2) -> {
                            Color color1 = new Color(o1.getR(), o1.getG(), o1.getB());
                            Color color2 = new Color(o2.getR(), o2.getG(), o2.getB());

                            return (int) (colorDistance(color, color1) - colorDistance(color, color2));
                        };

                        colors.sort(comparator);

                        if (color.getAlpha() < 255) {
                            layer = layer + "  ";
                        }else {
                            layer = layer + colors.get(0).getMc() + "█";
                        }

                    }

                    while(layer.startsWith("  ")) {
                        layer = layer.substring(2);
                    }
                    while(layer.endsWith("  ")) {
                        layer = layer.substring(0, layer.length() - 2);
                    }
                    if(layer.length() == 0) {
                        layers.add(" ");
                    }else {
                        layers.add(layer);
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return layers.toArray(new String[0]);
    }

    public static double colorDistance(Color c1, Color c2) {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rmean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rmean)*r*r)>>8) + 4*g*g + (((767-rmean)*b*b)>>8));
    }
}
