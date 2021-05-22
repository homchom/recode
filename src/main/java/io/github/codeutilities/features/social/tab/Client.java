package io.github.codeutilities.features.social.tab;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.file.ILoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.lang3.RandomStringUtils;
import io.github.codeutilities.features.social.tab.CodeUtilitiesServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;

public class Client implements ILoader {

    public static CodeUtilitiesServer client;
    public static boolean connected = false;

    @Override
    public void load() {
        connect();
    }

    public static void connect() {
        try {
            MinecraftClient mc = CodeUtilities.MC;
            Session session = mc.getSession();

            String serverid = RandomStringUtils.randomAlphabetic(20);
            mc.getSessionService().joinServer(session.getProfile(), session.getAccessToken(), serverid);
            String url = "wss://codeutilities.vatten.dev/?username=" + session.getUsername() + "&serverid=" + serverid + "&version=" + CodeUtilities.MOD_VERSION;

            client = new CodeUtilitiesServer(new URI(url));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
