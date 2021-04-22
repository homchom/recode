package io.github.codeutilities.commands.item.template;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.templates.TemplateUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WebviewCommand extends AbstractTemplateCommand {

    @Override
    protected String getName() {
        return "webview";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        ChatUtil.sendMessage("Generating template link...", ChatType.INFO_BLUE);

        CodeUtilities.EXECUTOR.submit(() -> {
            JsonObject template = TemplateUtils.fromItemStack(stack);
            String data = template.get("code").getAsString();
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("https://twv.vercel.app/v2/create");

                JsonObject json = new JsonObject();
                json.addProperty("temp",true);
                json.addProperty("template",data);

                StringEntity postingString = new StringEntity(json.toString());
                post.setEntity(postingString);
                post.setHeader("content-type", "application/json");
                post.setHeader("user-agent", "CodeUtilities");
                HttpResponse res = httpClient.execute(post);

                String response = EntityUtils.toString(res.getEntity());

                JsonObject obj = CodeUtilities.JSON_PARSER.parse(response).getAsJsonObject();

                String link = obj.get("link").getAsString();


                LiteralText text = new LiteralText(
                    "Click this message to view this code template in web!");
                text.styled((style) -> style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://derpystuff.gitlab.io/code/l?link=" + link)));
                ChatUtil.sendMessage(text, ChatType.INFO_BLUE);
            } catch (Exception err) {
                err.printStackTrace();
                ChatUtil.sendMessage("Failed to shorten link.", ChatType.FAIL);
                LiteralText text = new LiteralText(
                    "Click this message to view this code template in web!");
                text.styled((style) -> style
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://https://derpystuff.gitlab.io/code/?template=" + data)));
                ChatUtil.sendMessage(text, ChatType.INFO_BLUE);
            }
        });
    }
}
