package io.github.codeutilities.mod.mixin.render;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.mod.features.CPU_UsageText;
import io.github.codeutilities.mod.features.VarSyntaxHighlighter;
import io.github.codeutilities.sys.player.DFInfo;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.mod.features.commands.CodeSearcher;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MInGameHUD {
    @Inject(method = "renderStatusEffectOverlay", at = @At("RETURN"))
    private void renderStatusEffectOverlay(MatrixStack stack, CallbackInfo ci) {
        CPU_UsageText.onRender(stack);

        MinecraftClient mc = CodeUtilities.MC;
        TextRenderer tr = mc.textRenderer;

        if (CodeSearcher.searchType != null && CodeSearcher.searchValue != null && DFInfo.isOnDF() && DFInfo.currentState.getMode() == State.Mode.DEV) {

            tr.drawWithShadow(stack, new LiteralText("Searching usages of " + CodeSearcher.searchType.toString()).styled(style -> style.withUnderline(true)), 2, 2, 0xffffff);
            tr.drawWithShadow(stack, new LiteralText(CodeSearcher.searchValue), 2, 12, 0xffffff);
        }

        if (Config.getBoolean("highlightVarSyntax")) {
            try {
                ItemStack item = CodeUtilities.MC.player.getMainHandStack();

                if (item.getItem() != Items.AIR) {
                    CompoundTag vals = item.getOrCreateSubTag("PublicBukkitValues");
                    if (vals.contains("hypercube:varitem")) {
                        String var = vals.getString("hypercube:varitem");
                        JsonObject json = CodeUtilities.JSON_PARSER.parse(var).getAsJsonObject();
                        String type = json.get("id").getAsString();

                        if (Objects.equals(type, "num") || Objects.equals(type, "var")) {
                            String unformatted = json.getAsJsonObject("data").get("name").getAsString();
                            Text formatted = VarSyntaxHighlighter.highlight(unformatted);

                            if (formatted != null) {
                                int x = (mc.getWindow().getScaledWidth() - mc.textRenderer.getWidth(formatted)) / 2;
                                int y = mc.getWindow().getScaledHeight() - 55;
                                tr.drawWithShadow(stack, formatted, x,y, 0xffffff);
                            }
                        }
                    }

                }

            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
