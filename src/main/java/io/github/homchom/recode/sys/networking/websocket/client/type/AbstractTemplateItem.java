package io.github.homchom.recode.sys.networking.websocket.client.type;

import com.google.gson.*;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

import java.io.IOException;

public abstract class AbstractTemplateItem extends SocketItem {

    @Override
    public ItemStack getItem(String data) throws Exception {
        JsonObject templateObject = JsonParser.parseString(data).getAsJsonObject();

        String author;
        Component name;
        String templateData = parseJsonData(templateObject.get("data").getAsString());

        ItemStack stack = new ItemStack(Items.ENDER_CHEST);
        if (templateObject.has("author")) {
            author = templateObject.get("author").getAsString();
        } else {
            author = Minecraft.getInstance().player.getGameProfile().getName();
        }
        if (templateObject.has("name")) {
            name = new TextComponent(templateObject.get("name").getAsString());
        } else {
            name = new TextComponent("Imported Code Template").withStyle((style) -> style.withColor(TextColor.fromRgb(11163135)));
        }
        int version;
        if (templateObject.has("version")) {
            version = templateObject.get("version").getAsInt();
        } else {
            version = TemplateUtil.VERSION;
        }

        stack.setHoverName(name);
        TemplateUtil.applyRawTemplateNBT(stack, name, author, templateData, version);
        return stack;
    }

    public abstract String parseJsonData(String templateData) throws IOException;

}
