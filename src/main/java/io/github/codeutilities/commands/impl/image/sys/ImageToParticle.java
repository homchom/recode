package io.github.codeutilities.commands.impl.image.sys;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Image.SCALE_SMOOTH;

public class ImageToParticle {

    static final int maxWidth = 40;
    static final int maxHeight = 40;

    public static ParticleImage convert(File file) throws IOException {
        List<String> data = new ArrayList<>();

        BufferedImage image = ImageIO.read(file);
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > maxWidth || height > maxHeight) {
            width = maxWidth;
            height = maxHeight;
            BufferedImage newImage = new BufferedImage(maxWidth, maxHeight, image.getType());
            Graphics2D graphics2D = (Graphics2D) newImage.getGraphics();
            graphics2D.drawImage(image.getScaledInstance(maxWidth, maxHeight, SCALE_SMOOTH), 0, 0, null);
            graphics2D.dispose();
            image = newImage;
        }

        StringBuilder currentData = new StringBuilder();
        for (int i = 0; i < height; i++) {
            String hex;
            String previousHex = "";
            int repeatCount = 0;
            for (int j = 0; j < width; j++) {
                repeatCount++;
                int rgb = image.getRGB(j, i);
                hex = Integer.toHexString(rgb).substring(2);
                if (!hex.equals(previousHex)) {
                    previousHex = hex;
                    String appendStr = "#" + hex + repeatCount;
                    if (currentData.length() + appendStr.length() > 300) {
                        currentData.append("#");
                        data.add(currentData.toString());
                        currentData.delete(0, currentData.length());
                    }
                    repeatCount = 0;
                    currentData.append(appendStr);
                }
            }
        }
        currentData.append("#");
        data.add(currentData.toString());

        return new ParticleImage(data.toArray(new String[0]), width, height);
    }
}
