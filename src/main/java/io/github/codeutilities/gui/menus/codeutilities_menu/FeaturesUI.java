package io.github.codeutilities.gui.menus.codeutilities_menu;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.gui.IMenu;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FeaturesUI extends LightweightGuiDescription implements IMenu {
    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(425, 220);


        root.add(new WLabel(new LiteralText("CodeUtilities Features || v" + CodeUtilities.MOD_VERSION)), 4, 4);

        try {
            URL oracle = new URL("https://raw.githubusercontent.com/CodeUtilities/data/main/menus/featuresGUI-" + CodeUtilities.MOD_VERSION + ".txt");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            Integer y = 14;

            while ((inputLine = in.readLine()) != null) {
                y += 10;
                root.add(new WLabel(new LiteralText(inputLine)), 4, y);
            }

            in.close();

        } catch (Exception e) {
            CodeUtilities.log(Level.ERROR, String.valueOf(e));
        }

        setRootPanel(root);
    }
}