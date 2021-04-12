package io.github.codeutilities.commands.item;

import com.google.gson.*;
import com.mojang.brigadier.CommandDispatcher;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.*;
import io.github.codeutilities.util.chat.ChatType;
import io.github.codeutilities.util.chat.ChatUtil;
import io.github.codeutilities.util.networking.WebUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.http.HttpResponse;

import java.io.*;

public class TemplatesCommand extends Command {

    public static final String templateServer = "https://codeutilities-templates.glitch.me/";
    public static MinecraftClient mc = CodeUtilities.mc;
    public static String authId = null;

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("templates")
            .executes(ctx -> {
                if (mc.player.isCreative()) {
                    CodeUtilities.openGuiAsync(new TemplateStorageUI());
                    return 1;
                }else {
                    ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
                    return 1;
                }
            })
        );
    }

    private JsonObject getObject(String url) {
        return getObject(url, true);
    }

    private JsonObject getObject(String url, boolean requireAuth) {
        System.out.println("hey");
        try {
            if (authId == null && requireAuth) {
                ChatUtil.sendMessage("You are not authenticated!", ChatType.FAIL);
                return null;
            }

            JsonObject response = new JsonParser().parse(WebUtil.getString(url)).getAsJsonObject();
            System.out.println(response);
            if (response.get("success").getAsBoolean()) {
                return response;
            } else {
                ChatUtil.sendMessage(response.get("error").getAsString(), ChatType.FAIL);
                if (response.get("error").getAsString().equals("Not authenticated.")) {
                    ChatUtil.sendMessage("You are not authenticated!", ChatType.FAIL);
                    return null;
                }
            }

        } catch (IOException err) {
            err.printStackTrace();
            ChatUtil.sendMessage("Server Returned Invalid Response.", ChatType.FAIL);
        }

        return null;
    }

    public static void authenticate() {
        JsonObject data = new JsonObject();
        String serverId = "CodeUtilities" + CodeUtilities.rng.nextInt(99999);
        data.addProperty("accessToken", mc.getSession().getAccessToken());
        data.addProperty("selectedProfile", mc.getSession().getUuid().replace("-", ""));
        data.addProperty("serverId", serverId);

        HttpResponse res = WebUtil.makePost("https://sessionserver.mojang.com/session/minecraft/join", data);
        if (res.getStatusLine().getStatusCode() == 204) {
            JsonObject response;
            try {
                response = new JsonParser().parse(WebUtil.getString(
                        templateServer + "authenticate?username=" + mc.getSession().getUsername()
                                + "&serverId=" + serverId)).getAsJsonObject();
            } catch (IOException e) {
                return;
            }

            if (response.get("success").getAsBoolean()) {
                authId = response.get("id").getAsString();
            } else {
                System.out.println(response.get("error").getAsString());
            }
        }
    }
}
