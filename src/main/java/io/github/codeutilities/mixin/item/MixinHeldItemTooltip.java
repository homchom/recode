package io.github.codeutilities.mixin.item;


import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.ItemUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(InGameHud.class)
public class MixinHeldItemTooltip {
    private static final Map<String, MutableText> scopes = new HashMap<>();

    static {
        scopes.put("unsaved", new LiteralText("GAME").styled((style) -> style.withColor(Formatting.GRAY)));
        scopes.put("saved", new LiteralText("SAVE").styled((style) -> style.withColor(Formatting.YELLOW)));
        scopes.put("local", new LiteralText("LOCAL").styled((style) -> style.withColor(Formatting.GREEN)));
    }

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private ItemStack variableStack;
    private JsonObject varItemNbt;

    @Inject(method = "renderHeldItemTooltip", at = @At("HEAD"), cancellable = true)
    public void renderHeldItemTooltip(MatrixStack matrices, CallbackInfo callbackInfo) {
        try {
            if (!ModConfig.getConfig().variableScopeView) {
                return;
            }

            ItemStack itemStack = mc.player.inventory.getMainHandStack();

            if (variableStack != itemStack) {
                if (ItemUtil.isVar(itemStack, "var")) {
                    variableStack = itemStack;

                    CompoundTag tag = itemStack.getTag();
                    if (tag == null) {
                        return;
                    }

                    CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
                    if (publicBukkitNBT == null) {
                        return;
                    }

                    varItemNbt = CodeUtilities.JSON_PARSER.parse(publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject().getAsJsonObject("data");
                } else {
                    variableStack = null;
                }
            }

            if (variableStack != null) {
                callbackInfo.cancel();

                String name = varItemNbt.get("name").getAsString();
                MutableText scope = scopes.get(varItemNbt.get("scope").getAsString());

                int x1 = (mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(new LiteralText(name))) / 2;
                int y1 = mc.getWindow().getScaledHeight() - 45;

                int x2 = (mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(scope.asString())) / 2;
                int y2 = mc.getWindow().getScaledHeight() - 35;

                mc.textRenderer.drawWithShadow(matrices, new LiteralText(name), (float) x1, (float) y1, 16777215);
                mc.textRenderer.drawWithShadow(matrices, scope, (float) x2, (float) y2, 16777215);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
