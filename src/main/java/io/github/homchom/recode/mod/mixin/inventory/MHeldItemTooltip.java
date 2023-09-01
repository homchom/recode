package io.github.homchom.recode.mod.mixin.inventory;


import com.google.gson.JsonParser;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.VarSyntaxHighlighter;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Gui.class)
public class MHeldItemTooltip {
    @Shadow
    private ItemStack lastToolHighlight;

    // TODO: remove redundant code and improve performance (e.g. don't highlight every iteration)
    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
        var renderVarScope = Config.getBoolean("variableScopeView");
        var highlightVarSyntax = Config.getBoolean("highlightVarSyntax");
        if (!renderVarScope && !highlightVarSyntax) return;

        try {
            if (lastToolHighlight.isEmpty()) return;

            var tag = lastToolHighlight.getTag();
            if (tag == null) return;

            var bukkitValues = tag.getCompound("PublicBukkitValues");
            if (!bukkitValues.contains("hypercube:varitem")) return;

            var varString = bukkitValues.getString("hypercube:varitem");
            var varJson = JsonParser.parseString(varString).getAsJsonObject();
            var varData = varJson.getAsJsonObject("data");
            var type = varJson.get("id").getAsString();

            var scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            var scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            var font = Minecraft.getInstance().font;

            // render scope
            if (type.equals("var") && renderVarScope) {
                callbackInfo.cancel();

                var name = varData.get("name").getAsString();
                int x1 = (scaledWidth - font.width(Component.literal(name))) / 2;
                int y1 = scaledHeight - 45;
                guiGraphics.drawString(font, Component.literal(name), x1, y1, 0xffffff, true);

                var lore = tag.getCompound("display").getList("Lore", Tag.TAG_STRING);
                if (lore.isEmpty()) return;
                var scopeJson = tag.getCompound("display")
                        .getList("Lore", Tag.TAG_STRING)
                        .getString(0);
                var scope = Objects.requireNonNull(Component.Serializer.fromJson(scopeJson));
                int x2 = (scaledWidth - font.width(scope.getVisualOrderText())) / 2;
                int y2 = scaledHeight - 35;
                guiGraphics.drawString(font, scope, x2, y2, 0xffffff, true);
            }

            // render highlighting
            if (highlightVarSyntax) {
                var unformatted = varData.get("name").getAsString();
                var formatted = VarSyntaxHighlighter.highlight(unformatted);

                if (formatted != null) {
                    callbackInfo.cancel();

                    int x1 = (scaledWidth - font.width(lastToolHighlight.getHoverName())) / 2;
                    int y1 = scaledHeight - 45;
                    int x2 = (scaledWidth - font.width(formatted)) / 2;
                    int y2 = scaledHeight - 55;

                    if (!TextUtil.toLegacyCodes(formatted).startsWith("§c") && type.equals("num")) {
                        formatted = TextUtil.colorCodesToTextComponent(
                                TextUtil.toLegacyCodes(formatted)
                                        .replace("§bHighlighted:§r ", "")
                        );

                        x1 = (scaledWidth - font.width(formatted)) / 2;
                        guiGraphics.drawString(font, formatted, x1, y1, 0xffffff);
                    } else {
                        guiGraphics.drawString(font, lastToolHighlight.getHoverName(), x1, y1, 0xffffff);
                        if (!type.equals("var")) y2 += 20;
                        guiGraphics.drawString(font, formatted, x2, y2, 0xffffff);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
