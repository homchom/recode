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

import java.util.*;

public class FeaturesUI extends LightweightGuiDescription implements IMenu {
    List<CButton> all = new ArrayList<>();

    @Override
    public void open(String... args) throws CommandSyntaxException {
        WPlainPanel root = new WPlainPanel();

        List<LegacyFeature> features = FeatureList.get();

        WPlainPanel btnList = new WPlainPanel();

        WText text = new WText(
            new TextComponent("Click a feature on the right to view its description"));
        root.add(text, 110, 10, 190, 190);

        int y = 0;
        for (LegacyFeature f : features) {
            CButton btn = new CButton();
            btn.setLabel(new TextComponent(f.getName()));
            btn.setAlignment(HorizontalAlignment.LEFT);
            btnList.add(btn, 5, y + 5, 95, 15);
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
}
