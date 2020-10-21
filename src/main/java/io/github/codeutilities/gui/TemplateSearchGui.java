package io.github.codeutilities.gui;

import com.google.gson.*;
import io.github.codeutilities.util.*;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.List;
import java.util.*;

public class TemplateSearchGui extends LightweightGuiDescription {
    
    private static final String[] plots = new String[]{"Basic", "Large", "Massive"};
    private static final String[] ranks = new String[]{"Noble", "Emperor", "Mythic", "Overlord"};
    
    public TemplateSearchGui(JsonArray templates) {
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
            ItemStack templateItem = new ItemStack(Registry.ITEM.get(new Identifier(template.get("material").getAsString())));

            try {
                TemplateUtils.applyRawTemplateNBT(templateItem, name, uploader, template.get("data").getAsString());
            } catch (Exception err) {
                err.printStackTrace();
                templateItem.setCustomName(new LiteralText("§cFailed to load item."));
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


            List<Text> texts = new ArrayList<>();
            texts.add(new LiteralText(name));
            texts.add(new LiteralText("Created By: ").setStyle(createdByColorText).append(new LiteralText(uploader).setStyle(createdByColor)));
            texts.add(new LiteralText("§r" + (listed == 1 ? "§aPublic" : "§cPrivate")));
            texts.add(new LiteralText(""));
            texts.add(new LiteralText("§r⚐ Category: ").setStyle(categoryIcon).append(new LiteralText(template.get("category").getAsString().replace('&', '§')).setStyle(categoryColor)));
            texts.add(new LiteralText("☐ ").setStyle(reqIcon)
                    .append(getOrUnknown(plots, template.get("plot").getAsInt())).setStyle(reqText)
                    .append(new LiteralText(" ! ").setStyle(reqIcon.withBold(true)))
                    .append(new LiteralText(getOrUnknown(ranks, template.get("rank").getAsInt())).setStyle(reqText))
            );
            texts.add(new LiteralText(""));
            texts.add(new LiteralText("§rℹ ID: ").setStyle(idIcon).append(new LiteralText(String.valueOf(i)).setStyle(idColor)));

            item.setTooltip(texts.toArray(new Text[0]));
            
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
