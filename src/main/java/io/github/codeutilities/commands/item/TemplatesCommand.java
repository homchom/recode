package io.github.codeutilities.commands.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.gui.TemplateSearchGui;
import io.github.codeutilities.gui.TemplateStorageUI;
import io.github.codeutilities.gui.TemplateUploaderGui;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.TemplateUtils;
import io.github.codeutilities.util.WebUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.http.HttpResponse;

public class TemplatesCommand extends Command {

    public static final String templateServer = "https://codeutilities-templates.glitch.me/";
    public static MinecraftClient mc = CodeUtilities.mc;
    public static boolean authenticated = false;
    public static String authId = "";

    public static void authenticate(Runnable callback) {
        CompletableFuture.runAsync(() -> {
            ChatUtil.sendMessage("Authenticating with SessionServer...", ChatType.INFO_BLUE);
            JsonObject data = new JsonObject();
            String serverId = "CodeUtilities" + CodeUtilities.rng.nextInt(99999);
            data.addProperty("accessToken", mc.getSession().getAccessToken());
            data.addProperty("selectedProfile", mc.getSession().getUuid().replace("-", ""));
            data.addProperty("serverId", serverId);
            HttpResponse res = WebUtil
                .makePost("https://sessionserver.mojang.com/session/minecraft/join", data);
            if (res.getStatusLine().getStatusCode() == 204) {
                ChatUtil.sendMessage("Authenticated with SessionServer.", ChatType.INFO_BLUE);
                ChatUtil.sendMessage("Authenticating with TemplateServer...", ChatType.INFO_BLUE);
                try {
                    JsonObject response = (JsonObject) new JsonParser().parse(WebUtil.getString(
                        templateServer + "authenticate?username=" + mc.getSession().getUsername()
                            + "&serverId=" + serverId));
                    if (response.get("success").getAsBoolean()) {
                        authId = response.get("id").getAsString();
                        authenticated = true;
                        ChatUtil
                            .sendMessage("Authenticated with TemplateServer.", ChatType.INFO_BLUE);
                    } else {
                        ChatUtil.sendMessage(
                            "Authentication Failed: " + response.get("error").getAsString(),
                            ChatType.FAIL);
                    }
                    callback.run();
                } catch (Exception err) {
                    err.printStackTrace();
                    ChatUtil.sendMessage("Authentication Failed: Server Returned Invalid Response.",
                        ChatType.FAIL);
                    callback.run();
                }
            } else {
                ChatUtil
                    .sendMessage(
                        "Authentication Failed: Invalid session id (Try restarting your game)",
                        ChatType.FAIL);
                callback.run();
            }
        });
    }

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("templates")
            .then(ArgBuilder.literal("recent")
                .executes(ctx -> {
                    CodeUtilities.openGuiAsync(new TemplateStorageUI());
                    return 1;
                })
            )
            .then(ArgBuilder.literal("upload")
                .executes(ctx -> {
                    if (authenticated) {
                        ItemStack template = mc.player.getMainHandStack();
                        if (template.getItem() != Items.AIR) {
                            if (TemplateUtils.isTemplate(template)) {
                                CodeUtilities.openGuiAsync(new TemplateUploaderGui(template));
                            } else {
                                ChatUtil.sendMessage("That isn't a template!", ChatType.FAIL);
                            }
                        } else {
                            ChatUtil.sendMessage("You need to hold an item which is not air!",
                                ChatType.FAIL);
                        }
                    } else {
                        authenticate(() -> {
                            if (authenticated) {
                                mc.player.sendChatMessage("/templates upload");
                            }
                        });
                    }
                    return 1;
                })
            )
            .then(ArgBuilder.literal("search")
                .then(ArgBuilder.argument("query", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        CompletableFuture.runAsync(() -> {
                            if (authenticated) {
                                try {
                                    JsonObject response = new JsonParser().parse(WebUtil.getString(
                                        templateServer + "search?query=" + URLEncoder
                                            .encode(ctx.getArgument("query", String.class),
                                                "UTF-8") + "&authId=" + authId)).getAsJsonObject();
                                    if (response.get("success").getAsBoolean()) {
                                        JsonArray templates = response.get("templates")
                                            .getAsJsonArray();
                                        CodeUtilities
                                            .openGuiAsync(new TemplateSearchGui(templates));
                                    } else {
                                        ChatUtil.sendMessage(response.get("error").getAsString(),
                                            ChatType.FAIL);
                                        if (response.get("error").getAsString()
                                            .equals("Not authenticated.")) {
                                            ChatUtil.sendMessage("Re-Authenticating...",
                                                ChatType.INFO_BLUE);
                                            authenticated = false;
                                            authenticate(() -> mc.player
                                                .sendChatMessage("/" + ctx.getInput()));
                                        }
                                    }

                                } catch (Exception err) {
                                    err.printStackTrace();
                                    ChatUtil.sendMessage("Server Returned Invalid Response.",
                                        ChatType.FAIL);
                                }
                            } else {
                                authenticate(() -> mc.player.sendChatMessage("/" + ctx.getInput()));
                            }
                        });
                        return 1;
                    })
                )
            )
        );
    }
}
