package me.reasonless.codeutilities.events;

import com.mojang.blaze3d.platform.GlStateManager;

import me.reasonless.codeutilities.CodeUtilities;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

public class RenderWorldLastEvent {

  public static void onRender(float partialTicks) {
	  
	  if (CodeUtilities.hasblazing) return;
	  
    /*MinecraftClient mc = MinecraftClient.getInstance();
    assert mc.player != null;
    if (mc.player.isCreative()) {
      if (mc.player.getMainHandStack().getItem() == Items.PAPER) {
        if (mc.player.getMainHandStack().getName().asFormattedString()
            .matches(".aLocation")) {
          try {
            String nbt = Objects.requireNonNull(
                mc.player.getMainHandStack().getOrCreateTag()
                    .getCompound("PublicBukkitValues").get("hypercube:varitem")).toString();
            nbt = nbt.split("\"data\":")[1];
            nbt = nbt.split("\"loc\":")[1];
            int x = (int) Double.parseDouble(nbt.split("\"x\":")[1].split(",")[0]);
            int y = (int) Double.parseDouble(nbt.split("\"y\":")[1].split(",")[0]);
            int z = (int) Double.parseDouble(nbt.split("\"z\":")[1].split(",")[0].split("}")[0]);
            x += plotPos.getX();
            y += plotPos.getY();
            z += plotPos.getZ();

            if (mc.player.getOffHandStack().getName().asFormattedString()
                .matches(".aLocation")) {
              String nbt2 = Objects.requireNonNull(
                  mc.player.getOffHandStack().getOrCreateTag()
                      .getCompound("PublicBukkitValues").get("hypercube:varitem")).toString();
              nbt2 = nbt2.split("\"data\":")[1];
              nbt2 = nbt2.split("\"loc\":")[1];
              int x2 = (int) Double.parseDouble(nbt2.split("\"x\":")[1].split(",")[0]);
              int y2 = (int) Double.parseDouble(nbt2.split("\"y\":")[1].split(",")[0]);
              int z2 = (int) Double
                  .parseDouble(nbt2.split("\"z\":")[1].split(",")[0].split("}")[0]);
              x2 += plotPos.getX();
              y2 += plotPos.getY();
              z2 += plotPos.getZ();

              int x3 = Math.min(x, x2);
              int y3 = Math.min(y, y2);
              int z3 = Math.min(z, z2);
              int x4 = Math.max(x, x2);
              int y4 = Math.max(y, y2);
              int z4 = Math.max(z, z2);

              drawCube(partialTicks, x3 - 0.0005, y3 - 0.0005, z3 - 0.0005,
                  x4 + 1.0005, y4 + 1.0005, z4 + 1.0005);

            } else {
              drawCube(partialTicks, x - 0.0005, y - 0.0005, z - 0.0005,
                  x + 1.0005, y + 1.0005, z + 1.0005);
            }
          } catch (Exception ignored) {
          }
        }
      }
    }*/
  }

  //found that code in the internet and slightly modified it, so idk how 99% of it works
  private static void drawCube(float partialTicks, double x0, double y0, double z0, double x1,
      double y1, double z1) {
    assert MinecraftClient.getInstance().player != null;
    double yoffset = MinecraftClient.getInstance().player.getStandingEyeHeight();
    y0 -= yoffset;
    y1 -= yoffset;

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    MinecraftClient mc = MinecraftClient.getInstance();
    
    //GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    
    buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
    assert mc.player != null;
    x0 = x0 - (mc.player.lastRenderX
        + (mc.player.getX() - mc.player.lastRenderX) * (double) partialTicks);
    y0 = y0 - (mc.player.lastRenderY
        + (mc.player.getY() - mc.player.lastRenderY) * (double) partialTicks);
    z0 = z0 - (mc.player.lastRenderZ
        + (mc.player.getZ() - mc.player.lastRenderZ) * (double) partialTicks);
    x1 = x1 - (mc.player.lastRenderX
        + (mc.player.getX() - mc.player.lastRenderX) * (double) partialTicks);
    y1 = y1 - (mc.player.lastRenderY
        + (mc.player.getY() - mc.player.lastRenderY) * (double) partialTicks);
    z1 = z1 - (mc.player.lastRenderZ
        + (mc.player.getZ() - mc.player.lastRenderZ) * (double) partialTicks);

    buffer.vertex(x1, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x0, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y0, z1).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y0, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z0).color(255, 255, 255, 100).next();
    buffer.vertex(x1, y1, z1).color(255, 255, 255, 100).next();
    tessellator.draw();
    GlStateManager.disableBlend();
    //GlStateManager.enableTexture2D();
  }

}
