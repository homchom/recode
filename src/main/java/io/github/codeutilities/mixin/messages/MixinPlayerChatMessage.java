package io.github.codeutilities.mixin.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.codeutilities.config.CodeUtilsConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinPlayerChatMessage {
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onMessage(String string, CallbackInfo ci) {
        if (minecraftClient.player != null) {
            if ((string.endsWith(" -l") || string.endsWith(" -s") || string.endsWith(" -g")) && !string.startsWith("/") && CodeUtilsConfig.getBool("quickVarScope")) {
                ItemStack itemStack = minecraftClient.player.inventory.getMainHandStack();
                if (itemStack.hasTag()) {
                    CompoundTag tag = itemStack.getTag();
                    if (tag.contains("PublicBukkitValues")) {
                        CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
                        if (publicBukkitValues.contains("hypercube:varitem")) {
                            String varItem = publicBukkitValues.getString("hypercube:varitem");
                            try {
                                JsonObject jsonObject = new JsonParser().parse(varItem).getAsJsonObject();
                                if (jsonObject.has("id")) {
                                    if (jsonObject.get("id").getAsString().equals("var")) {
                                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                                        String displayScope = "";
                                        String displayScopeColor = "";
                                        if (string.endsWith(" -l")) {
                                            displayScope = "LOCAL";
                                            displayScopeColor = "green";
                                            data.addProperty("scope", "local");
                                        }
                                        if (string.endsWith(" -s")) {
                                            displayScope = "SAVE";
                                            displayScopeColor = "yellow";
                                            data.addProperty("scope", "saved");
                                        }
                                        if (string.endsWith(" -g")) {
                                            displayScope = "GAME";
                                            displayScopeColor = "gray";
                                            data.addProperty("scope", "unsaved");
                                        }
                                        data.addProperty("name", string.substring(0, string.length() - 3));
                                        jsonObject.add("data", data);
                                        publicBukkitValues.putString("hypercube:varitem", jsonObject.toString());
                                        tag.put("PublicBukkitValues", publicBukkitValues);
                                        itemStack.setTag(tag);

                                        itemStack.getTag().remove("display");
                                        CompoundTag display = new CompoundTag();
                                        ListTag lore = new ListTag();
                                        display.putString("Name", String.format("{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"white\",\"text\":\"%s\"}],\"text\":\"\"}", string.substring(0, string.length() - 3)));
                                        lore.add(StringTag.of(String.format("{\"extra\":[{\"bold\":false,\"italic\":false,\"underlined\":false,\"strikethrough\":false,\"obfuscated\":false,\"color\":\"%s\",\"text\":\"%s\"}],\"text\":\"\"}", displayScopeColor, displayScope)));
                                        display.put("Lore", lore);
                                        itemStack.getTag().put("display", display);

                                        ci.cancel();
                                        minecraftClient.interactionManager.clickCreativeStack(itemStack, minecraftClient.player.inventory.selectedSlot + 36);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
}
