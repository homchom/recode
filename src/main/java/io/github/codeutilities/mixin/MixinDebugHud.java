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
        List<String> leftText = callbackInfoReturnable.getReturnValue();
        leftText.remove(9);

        if(minecraftClient.player.getPos() != null) {
            leftText.add(9, String.format("%s %.3f / %.3f / %.3f",
                    Formatting.GOLD + "World Location:" + Formatting.YELLOW,
                    minecraftClient.player.getPos().getX(),
                    minecraftClient.player.getPos().getY(),
                    minecraftClient.player.getPos().getZ()));

            if(CodeUtilities.isOnDF() && DFInfo.currentState == DFInfo.State.DEV) {
                Vec3d plotCoord = minecraftClient.player.getPos().subtract(DFInfo.plotCorner);
                leftText.add(10, String.format("%s %.3f / %.3f / %.3f", "" +
                        Formatting.GOLD + "Plot Location:" + Formatting.YELLOW,
                        plotCoord.getX(),
                        plotCoord.getY(),
                        plotCoord.getZ()));
            }
        }


        leftText.add(Formatting.GOLD + "[CodeUtilities] " + Formatting.YELLOW + "Version: " + CodeUtilities.MOD_VERSION);
        callbackInfoReturnable.setReturnValue(leftText);
    }

}
