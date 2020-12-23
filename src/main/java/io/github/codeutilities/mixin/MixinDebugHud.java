package io.github.codeutilities.mixin;

import com.sun.org.apache.bcel.internal.classfile.Code;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.ModConfig;
import io.github.codeutilities.util.DFInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
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
    private MinecraftClient minecraftClient = MinecraftClient.getInstance();

    @Inject(method = "getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> callbackInfoReturnable) {
        try {
            CodeUtilities.log(Level.INFO, "getting left text");
            List<String> leftText = callbackInfoReturnable.getReturnValue();
            leftText.remove(9);

            if(minecraftClient.player.getPos() != null) {
                CodeUtilities.log(Level.INFO, "adding world location");
                leftText.add(9, String.format("%s %.3f / %.3f / %.3f",
                        Formatting.GOLD + "World Location:" + Formatting.YELLOW,
                        minecraftClient.player.getPos().getX(),
                        minecraftClient.player.getPos().getY(),
                        minecraftClient.player.getPos().getZ()));

                if(CodeUtilities.isOnDF() && DFInfo.currentState == DFInfo.State.DEV) {
                    CodeUtilities.log(Level.INFO, "adding plot location");
                    Vec3d plotCoord = minecraftClient.player.getPos().subtract(DFInfo.plotCorner);
                    leftText.add(10, String.format("%s %.3f / %.3f / %.3f", "" +
                                    Formatting.GOLD + "Plot Location:" + Formatting.YELLOW,
                            plotCoord.getX(),
                            plotCoord.getY(),
                            plotCoord.getZ()));
                }
            }

            CodeUtilities.log(Level.INFO, "adding codeutils debug info");
            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "Version: " + CodeUtilities.MOD_VERSION);
            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "onDF: " + CodeUtilities.isOnDF());
            leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "State: " + DFInfo.currentState);
            CodeUtilities.log(Level.INFO, "returning");
            callbackInfoReturnable.setReturnValue(leftText);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
