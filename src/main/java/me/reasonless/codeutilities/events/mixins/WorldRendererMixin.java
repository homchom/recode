package me.reasonless.codeutilities.events.mixins;

import me.reasonless.codeutilities.CodeUtilities;
import me.reasonless.codeutilities.events.RenderWorldLastEvent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

  @Inject(at = @At("RETURN"), method = "render")
  public void render(MatrixStack matrices, float tickDelta, long limitTime,
      boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
      LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
	  if (!CodeUtilities.hasblazing) RenderWorldLastEvent.onRender(tickDelta);
  }


}
