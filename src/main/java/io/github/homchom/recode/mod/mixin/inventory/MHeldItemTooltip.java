package io.github.homchom.recode.mod.mixin.inventory;


import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.VarSyntaxHighlighter;
import io.github.homchom.recode.sys.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Gui.class)
public class MHeldItemTooltip {
    private static final Map<String, MutableComponent> scopes = new HashMap<>();

    static {
        scopes.put("unsaved",
            new TextComponent("GAME").withStyle((style) -> style.withColor(ChatFormatting.GRAY)));
        scopes.put("saved",
            new TextComponent("SAVE").withStyle((style) -> style.withColor(ChatFormatting.YELLOW)));
        scopes.put("local",
            new TextComponent("LOCAL").withStyle((style) -> style.withColor(ChatFormatting.GREEN)));
    }

    private final Minecraft mc = Minecraft.getInstance();
    private ItemStack variableStack;
    private JsonObject varItemNbt;

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(PoseStack matrices, CallbackInfo callbackInfo) {
        try {
            if (Config.getBoolean("variableScopeView")) {
                ItemStack itemStack = mc.player.getMainHandItem();

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

                        varItemNbt = JsonParser.parseString(
                                publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject()
                            .getAsJsonObject("data");
                    } else {
                        variableStack = null;
                    }
                }

                if (variableStack != null) {
                    callbackInfo.cancel();

                    String name = varItemNbt.get("name").getAsString();
                    MutableComponent scope = scopes.get(varItemNbt.get("scope").getAsString());

                    int x1 = (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                        new TextComponent(name))) / 2;
                    int y1 = mc.getWindow().getGuiScaledHeight() - 45;

                    int x2 = (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                        scope.getContents())) / 2;
                    int y2 = mc.getWindow().getGuiScaledHeight() - 35;

                    mc.font.drawShadow(matrices, new TextComponent(name), (float) x1,
                        (float) y1, 16777215);
                    mc.font.drawShadow(matrices, scope, (float) x2, (float) y2,
                        16777215);
                }
            }

            if (Config.getBoolean("highlightVarSyntax")) {
                try {
                    ItemStack item = LegacyRecode.MC.player.getMainHandItem();

                    if (item.getItem() != Items.AIR) {
                        CompoundTag vals = item.getOrCreateTagElement("PublicBukkitValues");
                        if (vals.contains("hypercube:varitem")) {

                            String var = vals.getString("hypercube:varitem");
                            JsonObject json = JsonParser.parseString(var)
                                .getAsJsonObject();
                            String type = json.get("id").getAsString();

                            if (Objects.equals(type, "num") || Objects.equals(type, "var")) {
                                String unformatted = json.getAsJsonObject("data").get("name")
                                    .getAsString();
                                Component formatted = VarSyntaxHighlighter.highlight(unformatted);

                                if (formatted != null) {
                                    callbackInfo.cancel();

                                    int x1 =
                                        (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                                            item.getHoverName())) / 2;
                                    int y1 = mc.getWindow().getGuiScaledHeight() - 45;

                                    int x2 =
                                        (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                                            formatted)) / 2;
                                    int y2 = mc.getWindow().getGuiScaledHeight() - 55;

                                    if (!TextUtil.textComponentToColorCodes(formatted)
                                        .startsWith("§c")
                                        && type.equals("num")) {

                                        formatted = TextUtil.colorCodesToTextComponent(
                                            TextUtil.textComponentToColorCodes(formatted)
                                                .replace("§bHighlighted:§r ", "")
                                        );

                                        x1 =
                                            (mc.getWindow().getGuiScaledWidth()
                                                - mc.font.width(
                                                formatted)) / 2;

                                        mc.font.drawShadow(matrices, formatted,
                                            (float) x1,
                                            (float) y1, 16777215);

                                    } else {
                                        mc.font.drawShadow(matrices, item.getHoverName(),
                                            (float) x1,
                                            (float) y1, 16777215);

                                        if (!type.equals("var")) y2 += 20;

                                        mc.font.drawShadow(matrices, formatted, x2, y2,
                                            0xffffff);
                                    }
                                }
                            }
                        }
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
