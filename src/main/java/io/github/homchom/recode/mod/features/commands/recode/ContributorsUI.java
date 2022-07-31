package io.github.homchom.recode.mod.features.commands.recode;

import com.google.gson.*;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.networking.WebUtil;
import io.github.homchom.recode.sys.renderer.IMenu;
import io.github.homchom.recode.sys.renderer.widgets.CImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ContributorsUI extends LightweightGuiDescription implements IMenu {
    private static ContributorsUI INSTANCE;
    private final List<Contributor> contributors = new ArrayList<>();

    public static ContributorsUI getInstance() {
        return INSTANCE;
    }

    @Override
    public void open(String... args) {

        INSTANCE = this;

        WPlainPanel root = new WPlainPanel();
        root.setHost(this);
        root.setSize(300, 220);

        WPlainPanel panel = new WPlainPanel();
        root.add(new WLabel(Component.literal("Contributors")), 0, 0);

        WScrollPanel scrollPanel = new WScrollPanel(panel);
        scrollPanel.setHost(this);
        root.add(scrollPanel, 0, 10, 300, 210);

        int y = 0;
        int x = 0;

        try {
            JsonArray array = WebUtil.getJson("https://api.github.com/repos/homchom/recode/contributors").getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject object = element.getAsJsonObject();
                this.contributors.add(new Contributor(object.get("login").getAsString(), object.get("id").getAsInt(), object.get("contributions").getAsInt(), object.get("avatar_url").getAsString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Contributor contributor : contributors) {
            if (contributor.getAvatar() == null) {
                try {
                    URL url = new URL(contributor.getAvatarUrl());
                    ResourceLocation identifier = LegacyRecode.MC.getTextureManager().register("contributor_" + contributor.getName().toLowerCase(), new DynamicTexture(NativeImage.read(url.openStream())));
                    contributor.setAvatar(identifier);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            CImage image = new CImage(contributor.getAvatar());
            image.setSize(32, 32);
            panel.add(image, x, y);
            panel.add(new WLabel(Component.literal(contributor.getName())), x + 35, y + 12);


            if (x == 150) {
                x = 0;
                y += 35;
            } else {
                x = 150;
            }
        }

        panel.setHost(this);
        setRootPanel(root);

    }
}