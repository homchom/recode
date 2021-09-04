package io.github.codeutilities.mod.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuiltinModelItemRenderer.class)
public class MBuiltinModelItemRenderer {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(ItemStack stack, Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (!Config.getBoolean("betaItemTextures")) return;

        try {
            CompoundTag tag = stack.getSubTag("CodeutilitiesTextureData");
            if (tag != null && tag.contains("texture")) {
                matrices.push();
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(tag.getString("texture"))));

                if (img.getWidth()*img.getHeight()>50*50) {
                    String error64 = "iVBORw0KGgoAAAANSUhEUgAAABUAAAAHCAYAAADnCQYGAAAATUlEQVQoFWNgYGD4TwPM8P9/WBgcgyyA8WGWofNhavDIQwyBKUDWADKMHD4DTBOya5DZyPIgNoyPTsMcAFVDR5eiuwSZj4uN5Frqxz4ARAnkKV+mXQwAAAAASUVORK5CYII=";
                    img = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(error64)));
                }

                if (mode == Mode.GROUND) {
                    matrices.scale(0.5f,0.5f,0.5f);
                    matrices.translate(0.3,0.5,1);
                } else if (mode == Mode.THIRD_PERSON_RIGHT_HAND
                || mode == Mode.THIRD_PERSON_LEFT_HAND) {
                    matrices.translate(0.245,0.425,0.55);
                    matrices.scale(0.53f,0.53f,0.53f);
                } else if (mode == Mode.FIXED) {
                    matrices.translate(0,0,0.5);
                } else if (mode == Mode.HEAD) {
                    matrices.translate(1,0.8,1);
                    matrices.scale(-1,1,1);
                } else if (mode == Mode.FIRST_PERSON_RIGHT_HAND) {
                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-75));
                    matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(25));
                    matrices.translate(0.2,-0.2,-0.8);
                }

                if (tag.contains("scale")) {
                    float scale = tag.getFloat("scale");
                    matrices.scale(scale,scale,scale);
                }
                if (tag.contains("x") || tag.contains("y") || tag.contains("z")) {
                    float x = tag.getFloat("x");
                    float y = tag.getFloat("y");
                    float z = tag.getFloat("z");
                    matrices.translate(x,y,z);
                }

                matrices.scale(1,-1,1);
                matrices.translate(0,-1,0);

                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                Matrix4f matrix = matrices.peek().getModel();

                RenderSystem.enableBlend();
                RenderSystem.disableTexture();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableDepthTest();
                RenderSystem.disableCull();

                float scale = 1f / 16f;

                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);

                for (float side = -scale/2; side<=scale; side+=scale) {//run twice once with negative and once with positive scale
                    for (int x = 0; x < img.getWidth(); x++) {
                        for (int y = 0; y < img.getHeight(); y++) {
                            if (cu_isTransparent(img,x,y)) continue;
                            Color col = new Color(img.getRGB(x,y));
                            int r = col.getRed();
                            int g = col.getGreen();
                            int b = col.getBlue();
                            int a = col.getAlpha();

                            bufferBuilder.vertex(matrix, x * scale, y * scale, side).color(r, g, b, a).next();
                            bufferBuilder.vertex(matrix, (x+1) * scale, y * scale, side).color(r, g, b, a).next();
                            bufferBuilder.vertex(matrix, (x+1) * scale, (y+1) * scale, side).color(r, g, b, a).next();
                            bufferBuilder.vertex(matrix, x * scale, (y+1) * scale, side).color(r, g, b, a).next();

                            if (side < 0 && mode!=Mode.GUI) {//no need to do this twice or in guis
                                if (cu_isTransparent(img,x-1,y)) {
                                    bufferBuilder.vertex(matrix, x * scale, y * scale, side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, x * scale, y * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, x * scale, (y+1) * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, x * scale, (y+1) * scale, side).color(r, g, b, a).next();
                                }
                                if (cu_isTransparent(img,x+1,y)) {
                                    bufferBuilder.vertex(matrix, (x+1) * scale, y * scale, side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, y * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, (y+1) * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, (y+1) * scale, side).color(r, g, b, a).next();
                                }
                                if (cu_isTransparent(img,x,y+1)) {
                                    bufferBuilder.vertex(matrix, x * scale, (y+1) * scale, side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, x * scale, (y+1) * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, (y+1) * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, (y+1) * scale, side).color(r, g, b, a).next();
                                }
                                if (cu_isTransparent(img,x,y-1)) {
                                    bufferBuilder.vertex(matrix, x * scale, y * scale, side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, x * scale, y * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, y * scale, -side).color(r, g, b, a).next();
                                    bufferBuilder.vertex(matrix, (x+1) * scale, y * scale, side).color(r, g, b, a).next();
                                }
                            }
                        }
                    }
                }
                img.flush();
                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);

                RenderSystem.enableCull();
                RenderSystem.enableTexture();
                RenderSystem.disableBlend();
                RenderSystem.disableDepthTest();
                ci.cancel();
                matrices.pop();
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static boolean cu_isTransparent(BufferedImage img, int x, int y) {
        if (x < 0 || x >= img.getWidth()) return true;
        if (y < 0 || y >= img.getHeight()) return true;
        return new Color(img.getRGB(x,y),true).getAlpha()!=255;
    }

}
