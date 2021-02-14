package io.github.codeutilities.commands.item;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.TemplateSearchGui;
import io.github.codeutilities.gui.TemplateStorageUI;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class TemplatesCommand extends Command {

    public static final String templateServer = "https://codeutilities-templates.glitch.me/";
    public static String authId = null;

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("templates")
                .then(ArgBuilder.literal("recent")
                        .executes(ctx -> {
                            CodeUtilities.openGuiAsync(new TemplateStorageUI());
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("view-self")
                        .executes(ctx -> {
                            JsonObject response = getObject(templateServer + "templateServer" + "view-self?authId=" + authId);

                            CodeUtilities.openGuiAsync(new TemplateSearchGui(response.get("templates").getAsJsonArray()));
                            return 1;
                        })
                )
                .then(ArgBuilder.literal("search")
                        .then(ArgBuilder.argument("query", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    JsonObject response;
                                    try {
                                        response = getObject(templateServer + "search?query=" +
                                                URLEncoder.encode(ctx.getArgument("query", String.class), "UTF-8")
                                                + "&authId=" + authId, false);
                                    } catch (UnsupportedEncodingException e) {
                                        ChatUtil.sendMessage("Could not parse query!", ChatType.FAIL);
                                        return 0;
                                    }

                                    CodeUtilities.openGuiAsync(new TemplateSearchGui(response.get("templates").getAsJsonArray()));
                                    return 1;
                                })
                        )
                )
                .then(ArgBuilder.literal("all")
                        .executes(ctx -> {
                            JsonObject response = getObject(templateServer + "all", false);
                            CodeUtilities.openGuiAsync(new TemplateSearchGui(response.get("templates").getAsJsonArray()));

                            return 1;
                        })
                )
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

            JsonObject response = CodeUtilities.JSON_PARSER.parse(WebUtil.getString(url)).getAsJsonObject();
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
        MinecraftClient mc = CodeUtilities.MC;
        Random random = CodeUtilities.RANDOM;
        JsonParser jsonParser = CodeUtilities.JSON_PARSER;

        JsonObject data = new JsonObject();
        String serverId = "CodeUtilities" + random.nextInt(99999);
        data.addProperty("accessToken", mc.getSession().getAccessToken());
        data.addProperty("selectedProfile", mc.getSession().getUuid().replace("-", ""));
        data.addProperty("serverId", serverId);

        HttpResponse res = WebUtil.makePost("https://sessionserver.mojang.com/session/minecraft/join", data);
        if (res.getStatusLine().getStatusCode() == 204) {
            JsonObject response;
            try {
                response = jsonParser.parse(WebUtil.getString(
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
