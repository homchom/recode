package io.github.codeutilities.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.commands.item.TemplatesCommand;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

public class TemplateUploaderGui extends LightweightGuiDescription {

    MinecraftClient mc = MinecraftClient.getInstance();

    public TemplateUploaderGui(ItemStack template) {
        WGridPanel root = new WGridPanel(1);
        root.setSize(256, 100);

        CItem icon = new CItem(template);
        root.add(icon, 0, 0, 20, 20);

        CTextField name = new CTextField(new LiteralText(""));
        name.setMaxLength(25);
        name.setSuggestion(new LiteralText("Template Name"));
        root.add(name, 30, 0, 226, 0);

        CTextField description = new CTextField(new LiteralText(""));
        description.setMaxLength(100);
        description.setSuggestion(new LiteralText("Template Description"));
        root.add(description, 30, 25, 226, 0);

        WToggleButton listed = new WToggleButton(new LiteralText("Public"));
        listed.setToggle(true);
        listed.setOnToggle(bool -> {
            if (bool) {
                listed.setLabel(new LiteralText("Public"));
            } else {
                listed.setLabel(new LiteralText("Private"));
            }
        });
        root.add(listed, 30, 50, 226, 20);

        WButton upload = new WButton(new LiteralText("Upload"));
        upload.setEnabled(false);
        upload.setOnClick(() -> {
            CompletableFuture.runAsync(() -> {
                JsonObject data = new JsonObject();
                data.addProperty("name", name.getText());
                data.addProperty("description", description.getText());
                data.addProperty("authId", TemplatesCommand.authId);
                data.addProperty("template", template.toTag(new CompoundTag()).toString());
                data.addProperty("listed", listed.getToggle());

                ChatUtil.sendMessage("Uploading Template...", ChatType.INFO_BLUE);

                HttpResponse res = WebUtil
                    .makePost(TemplatesCommand.templateServer + "upload", data);
                try {
                    JsonObject json = new JsonParser().parse(
                        IOUtils.toString(res.getEntity().getContent(), Charset.defaultCharset()))
                        .getAsJsonObject();

                    if (json.get("success").getAsBoolean()) {
                        ChatUtil.sendMessage("Uploaded Template!",
                            ChatType.SUCCESS);
                    } else {
                        ChatUtil.sendMessage(json.get("error").getAsString(), ChatType.FAIL);
                        if (json.get("error").getAsString().equals("Not authenticated.")) {
                            ChatUtil.sendMessage("Re-Authenticating...", ChatType.INFO_BLUE);
                            TemplatesCommand.authenticated = false;
                            TemplatesCommand.authenticate(
                                () -> ChatUtil.sendMessage("Try uploading the template again.",
                                    ChatType.INFO_YELLOW));
                        }
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                    ChatUtil.sendMessage("Server returned invalid response.", ChatType.FAIL);
                }
            });
            mc.currentScreen.onClose();
        });
        name.setChangedListener(str -> upload
            .setEnabled(name.getText().length() >= 3 && description.getText().length() >= 3));

        description.setChangedListener(str -> upload
            .setEnabled(name.getText().length() >= 3 && description.getText().length() >= 3));

        root.add(upload, 190, 80, 70, 20);

        setRootPanel(root);
        root.validate(this);
    }

}
