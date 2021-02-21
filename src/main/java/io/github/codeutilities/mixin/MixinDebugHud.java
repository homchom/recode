package io.github.codeutilities.mixin;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
    private final MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        try {
            List<String> leftText = callbackInfoReturnable.getReturnValue();
            leftText.remove(9);

            if (minecraftClient.player.getPos() != null) {
                CodeUtilities.log(Level.INFO, "adding world location");
                leftText.add(9, String.format("%s %.3f / %.3f / %.3f",
                        Formatting.GOLD + "World Location:" + Formatting.YELLOW,
                        minecraftClient.player.getPos().getX(),
                        minecraftClient.player.getPos().getY(),
                        minecraftClient.player.getPos().getZ()));

                if (DFInfo.isOnDF() && DFInfo.currentState == DFInfo.State.DEV) {
                    CodeUtilities.log(Level.INFO, "adding plot location");
                    Vec3d plotCoord = minecraftClient.player.getPos().subtract(DFInfo.plotCorner);
                    leftText.add(10, String.format("%s %.3f / %.3f / %.3f", "" +
                                    Formatting.GOLD + "Plot Location:" + Formatting.YELLOW,
                            plotCoord.getX(),
                            plotCoord.getY(),
                            plotCoord.getZ()));
                }
            }

            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "Version: " + CodeUtilities.MOD_VERSION);
            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "onDF: " + DFInfo.isOnDF());
            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "State: " + DFInfo.currentState);
            callbackInfoReturnable.setReturnValue(leftText);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
