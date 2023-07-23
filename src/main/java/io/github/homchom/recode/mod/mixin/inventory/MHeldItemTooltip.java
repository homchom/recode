package io.github.homchom.recode.mod.mixin.inventory;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.VarSyntaxHighlighter;
import io.github.homchom.recode.sys.util.ItemUtil;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(Gui.class)
public class MHeldItemTooltip {
    private static final Map<String, MutableComponent> scopes = new HashMap<>();

    static {
        scopes.put("unsaved",
                Component.literal("GAME").withStyle((style) -> style.withColor(ChatFormatting.GRAY)));
        scopes.put("saved",
                Component.literal("SAVE").withStyle((style) -> style.withColor(ChatFormatting.YELLOW)));
        scopes.put("local",
                Component.literal("LOCAL").withStyle((style) -> style.withColor(ChatFormatting.GREEN)));
    }

    private final Minecraft mc = Minecraft.getInstance();
    private ItemStack variableStack;
    private JsonObject varItemNbt;

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
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
                            Component.literal(name))) / 2;
                    int y1 = mc.getWindow().getGuiScaledHeight() - 45;

                    int x2 = (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                        scope.getVisualOrderText())) / 2;
                    int y2 = mc.getWindow().getGuiScaledHeight() - 35;

                    guiGraphics.drawString(mc.font, Component.literal(name), x1, y1, 0xffffff, true);
                    guiGraphics.drawString(mc.font, scope, x2, y2, 0xffffff, true);
                }
            }

            if (Config.getBoolean("highlightVarSyntax")) {
                try {
                    ItemStack item = Minecraft.getInstance().player.getMainHandItem();

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

                                        guiGraphics.drawString(mc.font, formatted, x1, y1, 0xffffff);
                                    } else {
                                        guiGraphics.drawString(mc.font, item.getHoverName(), x1, y1, 0xffffff);
                                        if (!type.equals("var")) y2 += 20;
                                        guiGraphics.drawString(mc.font, formatted, x2, y2, 0xffffff);
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
