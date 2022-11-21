package io.github.homchom.recode.mod.features.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.renderer.widgets.ClickableGiveItem;
import io.github.homchom.recode.sys.renderer.widgets.ItemScrollablePanel;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateSearchMenu extends LightweightGuiDescription {

    // TODO remove?
    private static final String[] PLOTS = {"Basic", "Large", "Massive"};
    private static final String[] RANKS = {"Noble", "Emperor", "Mythic", "Overlord"};

    public TemplateSearchMenu(JsonArray templates) {
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 240);

        ItemScrollablePanel panel = ItemScrollablePanel.with(new ArrayList<>());
        panel.setScrollingHorizontally(TriState.FALSE);
        root.add(panel, 0, 0, 256, 240);

        int i = 0;
        for (JsonElement jsonElement : templates) {
            JsonObject template = jsonElement.getAsJsonObject();
            String name = template.get("name").getAsString();
            int listed = template.get("public").getAsInt();
            String uploader = template.get("uploadername").getAsString();
            ItemStack templateItem = new ItemStack(Registry.ITEM.get(new ResourceLocation(template.get("material").getAsString())));

            try {
                TemplateUtil.applyRawTemplateNBT(templateItem, name, uploader, template.get("data").getAsString());
            } catch (Exception err) {
                err.printStackTrace();
                templateItem.setHoverName(Component.literal("§cFailed to load item."));
            }

            ClickableGiveItem item = new ClickableGiveItem(templateItem);

            panel.getItemGrid().addItem(item);

            Style reqIcon = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#ff00f7").getRGB()));
            Style reqText = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#c300ff").getRGB()));

            Style categoryIcon = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#12b200").getRGB()));
            Style categoryColor = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#11ff00").getRGB()));

            Style idIcon = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#aaaaaa").getRGB()));
            Style idColor = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#666666").getRGB()));

            Style createdByColor = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#00ff66").getRGB()));
            Style createdByColorText = Style.EMPTY.withColor(TextColor.fromRgb(Color.decode("#bbbbbb").getRGB()));


            List<Component> texts = new ArrayList<>();
            texts.add(Component.literal(name));
            texts.add(Component.literal("Created By: ").setStyle(createdByColorText).append(Component.literal(uploader).setStyle(createdByColor)));
            texts.add(Component.literal("§r" + (listed == 1 ? "§aPublic" : "§cPrivate")));
            texts.add(Component.literal(""));
            texts.add(Component.literal("§r⚐ Category: ").setStyle(categoryIcon).append(Component.literal(template.get("category").getAsString().replace('&', '§')).setStyle(categoryColor)));
            texts.add(Component.literal("☐ ").setStyle(reqIcon)
                    .append(getOrUnknown(PLOTS, template.get("plot").getAsInt())).setStyle(reqText)
                    .append(Component.literal(" ! ").setStyle(reqIcon.withBold(true)))
                    .append(Component.literal(getOrUnknown(RANKS, template.get("rank").getAsInt())).setStyle(reqText))
            );
            texts.add(Component.literal(""));
            texts.add(Component.literal("§rℹ ID: ").setStyle(idIcon).append(Component.literal(String.valueOf(i)).setStyle(idColor)));

            item.setTooltip(texts.toArray(new Component[0]));

            i++;
        }

        setRootPanel(root);
        root.validate(this);
    }

    private static String getOrUnknown(String[] strings, int index) {
        try {
            return strings[index - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return "Unknown";
        }
    }


}
