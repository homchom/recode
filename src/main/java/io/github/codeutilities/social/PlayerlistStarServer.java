package io.github.codeutilities.social;

import com.mojang.authlib.exceptions.AuthenticationException;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.ILoader;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;

public class PlayerlistStarServer implements ILoader {

    public static String key;
    public static HashMap<UUID, Boolean> users = new HashMap<>();

    @Override
    public void load() {
        MinecraftClient mc = CodeUtilities.MC;

        Socket socket = IO.socket(URI.create("https://CodeUtilitiesServer.blazemcworld1.repl.co"));

        //sent when the server needs the client to authenticate
        //args: (String) salt
        socket.on("requestAuth", (Object... args) -> {
            try {
                String salt = (String) args[0];
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(salt.getBytes());
                String data = RandomStringUtils.randomAlphabetic(20);
                md.update(data.getBytes());

                String serverid = String.valueOf(Hex.encodeHex(md.digest(), true));

                Session session = mc.getSession();

                mc.getSessionService()
                    .joinServer(session.getProfile(), session.getAccessToken(), serverid);

                socket.emit("authLogin", data, session.getUsername());
            } catch (NoSuchAlgorithmException | AuthenticationException e) {
                e.printStackTrace();
            }
        });

        //sent when a new user connects
        //args: (String) uuid (bool) isDev
        socket.on("newUser", (Object... args) -> {
            UUID uuid = UUID.fromString((String) args[0]);
            users.put(uuid, (boolean) args[1]);
        });

        //sent when a user disconnects
        //args (String) uuid
        socket.on("remUser", (Object... args) -> {
            UUID uuid = UUID.fromString((String) args[0]);
            users.remove(uuid);
        });

        //sent once on login, contains all currently connected users
        //args (JsonObject) users
        socket.on("userList", (Object... args) -> {
            JSONObject users = (JSONObject) args[0];
            for (String key : users.keySet()) {
                users.put(key,users.getBoolean(key));
            }
        });

        socket.connect();
    }
}
