package io.github.codeutilities.commands.item.template;

import com.google.gson.JsonObject;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.templates.TemplateUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;

public class WebviewCommand extends AbstractTemplateCommand {

    @Override
    protected String getName() {
        return "webview";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        JsonObject template = TemplateUtils.fromItemStack(stack);
        LiteralText text = new LiteralText("Click this message to view this code template in web!");
        text.styled((style) -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, String.format("https://derpystuff.gitlab.io/code/?template=%s", template.get("code").getAsString()))));
        ChatUtil.sendMessage(text, ChatType.INFO_BLUE);

    }
}
