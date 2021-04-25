package io.github.codeutilities.social;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ILoader;
import io.github.codeutilities.util.networking.WebUtil;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.lang3.RandomStringUtils;

public class PlayerlistStarServer implements ILoader {

    public static String key;

    @Override
    public void load() {
        MinecraftClient mc = CodeUtilities.MC;
        Session session = mc.getSession();

        String serverid = RandomStringUtils.randomAlphabetic(20);

        try {
            mc.getSessionService().joinServer(session.getProfile(),session.getAccessToken(),serverid);

            JsonObject obj = WebUtil.getJson("http://CodeUtilities-Player-DB.techstreetdev.repl.co/login/" + session.getUsername() + "/" + serverid).getAsJsonObject();

            if (obj.get("success").getAsBoolean()) {

                key = obj.get("key").getAsString();

                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

                String id = session.getUuid().replaceAll("-","");

                ses.scheduleAtFixedRate(() -> {

                    try {
                        JsonObject res = WebUtil.getJson("http://CodeUtilities-Player-DB.techstreetdev.repl.co/renew/" + id + "/" + key).getAsJsonObject();

                        if (!res.get("success").getAsBoolean()) throw new Exception(res.get("error").getAsString());

                    } catch (Exception err) {
                        err.printStackTrace();
                    }

                },1,5, TimeUnit.MINUTES);

            } else throw new Exception(obj.get("error").getAsString());
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
