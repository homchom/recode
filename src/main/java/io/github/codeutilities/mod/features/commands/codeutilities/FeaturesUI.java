package io.github.codeutilities.mod.features.commands.codeutilities;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.sys.renderer.IMenu;
import io.github.codeutilities.sys.renderer.widgets.CImage;
import io.github.codeutilities.sys.util.TextUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class FeaturesUI extends LightweightGuiDescription implements IMenu {
    private static final Identifier CODEUTILS_LOGO = new Identifier("codeutilities:icon.png");

    @Override
    public void open(String... args) {
        WPlainPanel root = new WPlainPanel();
        root.setSize(425, 220);

        WPlainPanel panel = new WPlainPanel();
        WScrollPanel scrollPanel = new WScrollPanel(panel);
        scrollPanel.setHost(this);

        root.add(scrollPanel, 0, 0, 425, 220);
        panel.add(new WLabel(new LiteralText("CodeUtilities Features || v" + CodeUtilities.MOD_VERSION)), 4, 4);

        CImage cImage = new CImage(CODEUTILS_LOGO);
        cImage.setSize(60, 60);
        panel.add(cImage, 355, 0);

        try {
            URL oracle = new URL("https://raw.githubusercontent.com/CodeUtilities/data/main/menus/featuresGUI-" + CodeUtilities.MOD_VERSION + ".txt");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(oracle.openStream()));

            String inputLine;
            int y = 14;

            while ((inputLine = in.readLine()) != null) {
                y += 10;
                if (inputLine.contains("-")) {
                    String[] inls = inputLine.replace("§l/§r", "/").split("-");
                    String[] colors = {"§x§C§C§A§A§0§0", "§x§3§3§0§3§0§0", "§x§C§C§5§2§0§0"};
                    Text txt = TextUtil.colorCodesToTextComponent(colors[0]+inls[0]+colors[1]+" - "+colors[2]+inls[1]);
                    panel.add(new WLabel(txt), 4, y);
                } else {
                    CodeUtilities.log(Level.INFO, inputLine+" does not contain a \"-\" character.");
                }
            }

            in.close();

        } catch (Exception e) {
            CodeUtilities.log(Level.ERROR, String.valueOf(e));
        }

        panel.setHost(this);
        setRootPanel(root);
    }
}
