package io.github.codeutilities.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.commands.item.TemplatesCommand;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.LiteralText;

public class TemplateSearchGui extends LightweightGuiDescription {

    public TemplateSearchGui(JsonArray templates) {
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 240);

        WGridPanel panel = new WGridPanel(1);
        WScrollPanel scrollPanel = new WScrollPanel(panel);
        scrollPanel.setScrollingHorizontally(TriState.FALSE);
        scrollPanel.setScrollingVertically(TriState.TRUE);
        root.add(scrollPanel, 0, 0, 256, 240);

        String uuid = MinecraftClient.getInstance().getSession().getUuid().replace("-", "");

        int i = 0;
        for (JsonElement jsonElement : templates) {
            JsonObject template = jsonElement.getAsJsonObject();
            String name = template.get("name").getAsString();
            String description = template.get("description").getAsString();
            boolean listed = template.get("listed").getAsBoolean();
            String uploader = template.get("uploadername").getAsString();
            String owner = template.get("uploaderid").getAsString();
            ItemStack templateItem = new ItemStack(Items.BARRIER);
            try {
                templateItem = ItemStack
                    .fromTag(StringNbtReader.parse(template.get("data").getAsString()));
            } catch (Exception err) {
                err.printStackTrace();
                templateItem.setCustomName(new LiteralText("§cFailed to load item."));
            }

            if (!listed) {
                name += " §c(Private)";
            }

            CItem item = new CItem(templateItem);
            panel.add(item, 0, i * 35, 20, 30);

            WText info = new WText(new LiteralText(
                "Name: " + name + "§r\n§rDesc: " + description + "§r\n§rUploader: " + uploader));
            panel.add(info, 30, i * 35, 200, 30);

            WButton get = new WButton(new LiteralText("Get"));
            ItemStack finalTemplateItem = templateItem;
            get.setOnClick(() -> ItemUtil.giveCreativeItem(finalTemplateItem));
            panel.add(get, 223, i * 35 + 5, 24, 20);

            if (owner.equals(uuid)) {
                WButton delete = new WButton(new LiteralText("Delete"));
                ItemStack finalTemplateItem1 = templateItem;
                delete.setOnClick(() -> {
                    CompletableFuture.runAsync(() -> {
                        try {
                            JsonObject res = new JsonParser().parse(WebUtil.getString(
                                TemplatesCommand.templateServer + "delete?id=" + template.get("id")
                                    .getAsInt() + "&authId=" + TemplatesCommand.authId))
                                .getAsJsonObject();
                            if (res.get("success").getAsBoolean()) {
                                ItemUtil.giveCreativeItem(finalTemplateItem1);
                                ChatUtil.sendMessage("Deleted Template!", ChatType.SUCCESS);
                            } else {
                                ChatUtil.sendMessage(res.get("error").getAsString(), ChatType.FAIL);
                            }
                        } catch (Exception err) {
                            err.printStackTrace();
                            ChatUtil
                                .sendMessage("Server Returned Invalid Response.", ChatType.FAIL);
                        }
                    });
                    MinecraftClient.getInstance().currentScreen.onClose();
                });
                panel.add(delete, 188, i * 35 + 5, 35, 20);
            }

            i++;
        }

        setRootPanel(root);
        root.validate(this);
    }

}
