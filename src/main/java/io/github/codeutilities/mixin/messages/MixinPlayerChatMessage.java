package io.github.codeutilities.mixin.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinPlayerChatMessage {
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "Lnet/minecraft/client/network/ClientPlayerEntity;sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onMessage(String string, CallbackInfo ci) {
        if(minecraftClient.player != null) {
            if((string.endsWith(" -l") || string.endsWith(" -s") || string.endsWith(" -g")) && !string.startsWith("/") && ModConfig.getConfig().quickVarScope) {
                ItemStack itemStack = minecraftClient.player.inventory.getMainHandStack();
                if(itemStack.hasTag()) {
                    CompoundTag tag = itemStack.getTag();
                    if(tag.contains("PublicBukkitValues")) {
                        CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
                        if(publicBukkitValues.contains("hypercube:varitem")) {
                            String varItem = publicBukkitValues.getString("hypercube:varitem");
                            try {
                                JsonObject jsonObject = new JsonParser().parse(varItem).getAsJsonObject();
                                if(jsonObject.has("id")) {
                                    if(jsonObject.get("id").getAsString().equals("var")) {
                                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                                        if(string.endsWith(" -l")) data.addProperty("scope", "local");
                                        if(string.endsWith(" -s")) data.addProperty("scope", "saved");
                                        if(string.endsWith(" -g")) data.addProperty("scope", "unsaved");
                                        data.addProperty("name", string.substring(0, string.length() - 3));
                                        jsonObject.add("data", data);
                                        publicBukkitValues.putString("hypercube:varitem", jsonObject.toString());
                                        tag.put("PublicBukkitValues", publicBukkitValues);
                                        itemStack.setTag(tag);
                                        ci.cancel();
                                        minecraftClient.interactionManager.clickCreativeStack(itemStack, minecraftClient.player.inventory.selectedSlot + 36);
                                    }
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
}
