package io.github.homchom.recode.mod.commands.impl.item.template;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.ModConstants;
import io.github.homchom.recode.sys.hypercube.templates.TemplateUtil;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WebviewCommand extends AbstractTemplateCommand {

    @Override
    public String getDescription() {
        return """
                [blue]/webview[reset]

                Sends you a link for previewing the code template in the website.""";
    }

    @Override
    public String getName() {
        return "/webview";
    }

    @Override
    protected String getCmdName() {
        return "webview";
    }

    @Override
    protected void withTemplate(ItemStack stack) {
        ChatUtil.sendMessage("Generating template link...", ChatType.INFO_BLUE);

        LegacyRecode.executor.submit(() -> {
            JsonObject template = TemplateUtil.read(stack);
            String data = template.get("code").getAsString();
            try (var httpClient = HttpClientBuilder.create().build()) {
                HttpPost post = new HttpPost("https://twv.vercel.app/v2/create");

                JsonObject json = new JsonObject();
                json.addProperty("temp", true);
                json.addProperty("template", data);

                StringEntity postingString = new StringEntity(json.toString());
                post.setEntity(postingString);
                post.setHeader("content-type", "application/json");
                post.setHeader("user-agent", ModConstants.MOD_ID);
                HttpResponse res = httpClient.execute(post);

                String response = EntityUtils.toString(res.getEntity());

                JsonObject obj = JsonParser.parseString(response).getAsJsonObject();

                String link = obj.get("link").getAsString();


                MutableComponent text = Component.literal(
                        "Click this message to view this code template in web!");
                text.withStyle((style) -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                "https://derpystuff.gitlab.io/code/l?link=" + link)));
                ChatUtil.sendMessage(text, ChatType.INFO_BLUE);
            } catch (Exception err) {
                err.printStackTrace();
                ChatUtil.sendMessage("Failed to shorten link.", ChatType.FAIL);
                MutableComponent text = Component.literal(
                        "Click this message to view this code template in web!");
                text.withStyle((style) -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                "https://derpystuff.gitlab.io/code/?template=" + data)));
                ChatUtil.sendMessage(text, ChatType.INFO_BLUE);
            }
        });
    }
}
