package io.github.homchom.recode.mod.features.commands.image;

import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;

public class ImageToHologram {

    // Converts a file into an array of literal texts containing the respected color. This would work?
    public static String[] convertWithHex(File file) {
        List<String> layers = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedImage image = ImageIO.read(file);
                int width = image.getWidth();
                int height = image.getHeight();

                if (width > 17 || height > 17) {
                    height = 17;
                    width = 17;
                    BufferedImage newImage = new BufferedImage(17, 17, image.getType());
                    Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();
                    graphics2D.drawImage(image.getScaledInstance(17, 17, SCALE_SMOOTH), 0, 0, null);
                    graphics2D.dispose();
                    image = newImage;
                }

                for (int i = 0; i < height; i++) {
                    StringBuilder layer = new StringBuilder();
                    String hex = "";
                    for (int j = 0; j < width; j++) {
                        int rgb = image.getRGB(j, i);
                        Color color = new Color(rgb, true);

                        if (color.getAlpha() < 255) {
                            layer.append(MinecraftColors.GRAY.getMc()).append("▁");
                        } else {
                            if (!hex.equals(Integer.toHexString(rgb).substring(2))) {
                                hex = Integer.toHexString(rgb).substring(2);
                                String finalHex = hex.replaceAll("(.?)", "§$1").substring(0, 12);
                                layer.append("§x").append(finalHex).append("█");
                            } else {
                                layer.append("█");
                            }
                        }

                    }
                    layers.add(layer.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return layers.toArray(new String[0]);
    }

    public static String[] convertWithColorCodes(File file) {
        List<String> layers = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedImage image = ImageIO.read(file);

                int width = image.getWidth();
                int height = image.getHeight();

                if (width > 64 || height > 64) {
                    height = 64;
                    width = 64;
                    BufferedImage newImage = new BufferedImage(64, 64, image.getType());
                    Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();
                    graphics2D.drawImage(image.getScaledInstance(64, 64, SCALE_SMOOTH), 0, 0, null);
                    graphics2D.dispose();
                    image = newImage;
                }

                for (int i = 0; i < height; i++) {
                    StringBuilder layer = new StringBuilder();
                    for (int j = 0; j < width; j++) {
                        int rgb = image.getRGB(j, i);
                        Color color = new Color(rgb, true);

                        List<MinecraftColors> colors = new ArrayList<>(Arrays.asList(MinecraftColors.values()));

                        MinecraftColors nearestColor = MinecraftColors.BLACK;

                        double distance = 100000;
                        for (MinecraftColors c : colors) {
                            if (colorDistance(c.getColor(), color) < distance) {
                                distance = colorDistance(c.getColor(), color);
                                nearestColor = c;
                            }
                        }

                        if (color.getAlpha() < 255) {
                            layer.append(MinecraftColors.GRAY.getMc()).append("▁");
                        } else {
                            layer.append(nearestColor.getMc()).append("█");
                        }

                    }


                    if (layer.length() == 0) {
                        layers.add(" ");
                    } else {
                        layers.add(layer.toString());
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return layers.toArray(new String[0]);
    }

    private static double colorDistance(Color c1, Color c2) {
        int sum = 0;

        sum += Math.pow(c1.getRed() - c2.getRed(), 2);
        sum += Math.pow(c1.getGreen() - c2.getGreen(), 2);
        sum += Math.pow(c1.getBlue() - c2.getBlue(), 2);

        return Math.sqrt(sum);
    }
}
