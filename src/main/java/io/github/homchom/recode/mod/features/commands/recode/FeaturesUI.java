package io.github.homchom.recode.mod.features.commands.recode;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.cotton.gui.client.*;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.CButton;
import io.github.homchom.recode.sys.util.TextUtil;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class FeaturesUI extends LightweightGuiDescription implements IMenu {

    private static final ResourceLocation CODEUTILS_LOGO = new ResourceLocation("codeutilities:icon.png");

    List<CButton> all = new ArrayList<>();

    @Override
    public void open(String... args) throws CommandSyntaxException {
        WPlainPanel root = new WPlainPanel();

        List<Feature> features = FeatureList.get();

        WPlainPanel btnList = new WPlainPanel();

        WText text = new WText(
            new TextComponent("Click a feature on the right to view its description"));
        root.add(text, 110, 10, 190, 190);

        int y = 0;
        for (Feature f : features) {
            CButton btn = new CButton();
            btn.setLabel(new TextComponent(f.getName()));
            btn.setAlignment(HorizontalAlignment.LEFT);
            btnList.add(btn, 0, y, 95, 15);
            btn.setOnClick(() -> {
                all.forEach(b -> b.setEnabled(true));
                btn.setEnabled(false);

                String desc = f.getDescription();

                desc = desc.replaceAll("\\[red\\]", !LibGui.isDarkMode() ? "§x§b§7§1§2§0§0" : "§x§d§d§4§6§2§c");
                desc = desc.replaceAll("\\[blue\\]", !LibGui.isDarkMode() ? "§x§0§0§0§0§a§b" : "§x§6§8§a§e§e§3");
                desc = desc.replaceAll("\\[green\\]", !LibGui.isDarkMode() ? "§x§0§0§8§7§0§f" : "§x§6§8§a§e§e§3");
                desc = desc.replaceAll("\\[yellow\\]", !LibGui.isDarkMode() ? "§x§d§3§8§2§0§0" : "§x§8§1§e§2§4§2");
                desc = desc.replaceAll("\\[reset\\]", "§r");

                text.setText(TextUtil.colorCodesToTextComponent(desc));
            });
            all.add(btn);
            y += 15;
        }

        WScrollPanel sBtnList = new WScrollPanel(btnList);
        sBtnList.setScrollingHorizontally(TriState.FALSE);
        sBtnList.setScrollingVertically(TriState.TRUE);

        root.add(sBtnList, 0, 0, 100, 200);

        setRootPanel(root);
        root.validate(this);
    }

//    @Override
//    public void open(String... args) {
//        WPlainPanel root = new WPlainPanel();
//        root.setSize(425, 220);
//
//        WPlainPanel panel = new WPlainPanel();
//        WScrollPanel scrollPanel = new WScrollPanel(panel);
//        scrollPanel.setHost(this);
//
//        root.add(scrollPanel, 0, 0, 425, 220);
//        panel.add(new WLabel(new LiteralText("CodeUtilities Features || v" + Recode.MOD_VERSION)), 4, 4);
//
//        CImage cImage = new CImage(CODEUTILS_LOGO);
//        cImage.setSize(60, 60);
//        panel.add(cImage, 355, 0);
//
//        try {
//            URL oracle = new URL("https://raw.githubusercontent.com/CodeUtilities/data/main/menus/featuresGUI-" + Recode.MOD_VERSION + ".txt");
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(oracle.openStream()));
//
//            String inputLine;
//            int y = 14;
//
//            while ((inputLine = in.readLine()) != null) {
//                y += 10;
//                if (inputLine.contains("-")) {
//                    String[] inls = inputLine.replace("§l/§r", "/").split("-");
//                    String[] colors = {"§x§C§C§5§D§0§0", "§x§3§3§0§3§0§0", "§x§C§C§5§2§0§0"};
//                    Text txt = TextUtil.colorCodesToTextComponent(colors[0]+inls[0]+colors[1]+" - "+colors[2]+inls[1]);
//                    panel.add(new WLabel(txt), 4, y);
//                }
//            }
//
//            in.close();
//
//        } catch (Exception e) {
//            Recode.log(Level.ERROR, String.valueOf(e));
//        }
//
//        panel.setHost(this);
//        setRootPanel(root);
//    }
}
