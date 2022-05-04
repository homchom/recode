package io.github.homchom.recode.mod.features.social.tab;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.sys.file.ILoader;
import net.minecraft.client.User;
import net.minecraft.client.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.net.URI;

public class Client implements ILoader {

    public static RecodeServer client;

    @Override
    public void load() {
        connect();
    }

    public static void connect() {
        try {
            Minecraft mc = Recode.MC;
            User session = mc.getUser();

            String serverid = RandomStringUtils.randomAlphabetic(20);
            mc.getMinecraftSessionService().joinServer(session.getGameProfile(), session.getAccessToken(), serverid);
            String url = "wss://codeutilities.vatten.dev/?username=" + session.getName() + "&serverid=" + serverid + "&version=" + Recode.MOD_VERSION + (Recode.BETA ? "-BETA" : "");

            client = new RecodeServer(new URI(url));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
