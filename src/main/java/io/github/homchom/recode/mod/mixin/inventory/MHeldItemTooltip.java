package io.github.homchom.recode.mod.mixin.inventory;


import com.google.gson.JsonParser;
import io.github.homchom.recode.Logging;
import io.github.homchom.recode.game.ItemExtensions;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.ui.text.TextInterop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class MHeldItemTooltip {
    @Shadow
    private ItemStack lastToolHighlight;

    // TODO: remove redundant code and improve performance
    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo callbackInfo) {
        var renderVarScope = LegacyConfig.getBoolean("variableScopeView");
        var highlightVarSyntax = LegacyConfig.getBoolean("highlightVarSyntax");
        if (!renderVarScope && !highlightVarSyntax) return;

        if (lastToolHighlight.isEmpty()) return;

        var tag = lastToolHighlight.getTag();
        if (tag == null) return;

        var bukkitValues = tag.getCompound("PublicBukkitValues");
        if (!bukkitValues.contains("hypercube:varitem")) return;

        var varString = bukkitValues.getString("hypercube:varitem");
        var varJson = JsonParser.parseString(varString).getAsJsonObject();

        try {
            var type = varJson.get("id").getAsString();
            var name = varJson.getAsJsonObject("data").get("name");
            if (name == null) return;

            var scaledWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            var scaledHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            var font = Minecraft.getInstance().font;

            // render scope
            if (type.equals("var") && renderVarScope) {
                callbackInfo.cancel();

                var nameText = Component.literal(name.getAsString());
                var x1 = (scaledWidth - font.width(nameText)) / 2;
                var y1 = scaledHeight - 45;
                guiGraphics.drawString(font, nameText, x1, y1, 0xffffff, true);

                var lore = ItemExtensions.lore(lastToolHighlight);
                if (lore.size() == 1) {
                    var scope = TextInterop.toVanilla(lore.get(0));
                    var x2 = (scaledWidth - font.width(scope)) / 2;
                    var y2 = scaledHeight - 35;
                    guiGraphics.drawString(font, scope, x2, y2, 0xffffff, true);
                }
            }

            // render highlighting
            // TODO: re-evaluate
            /*if (highlightVarSyntax) {
                var formatted = VarSyntaxHighlighter.highlight(name.getAsString());

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
            }*/
        } catch (Exception e) {
            Logging.logError("Unrecognized DF value item data: " + varJson);
            throw e;
        }
    }
}
