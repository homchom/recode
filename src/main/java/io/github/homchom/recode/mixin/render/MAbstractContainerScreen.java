package io.github.homchom.recode.mixin.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.TexturedOtherPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractContainerScreen.class)
public abstract class MAbstractContainerScreen {
    private final HashMap<String, ResourceLocation> cache = new HashMap<>();

    // TODO: improve
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltip(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci, ItemStack stack) {
        try {
            if (Config.getBoolean("previewHeadSkins")) {
                Item item = stack.getItem();
                if (item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if (block instanceof AbstractSkullBlock) {
                        GameProfile gameProfile = null;
                        if (stack.hasTag()) {
                            CompoundTag compoundTag = stack.getTag();
                            if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                                gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                                compoundTag.remove("SkullOwner");
                                SkullBlockEntity.updateGameprofile(gameProfile, p ->
                                        compoundTag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p))
                                );
                            }
                            gameProfile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                        }

                        Minecraft mc = Minecraft.getInstance();

                        Map<Type, MinecraftProfileTexture> textures = mc.getSkinManager().getInsecureSkinInformation(gameProfile);
                        if (textures.containsKey(Type.SKIN)) {
                            URL url = new URL(textures.get(Type.SKIN).getUrl());

                            if (!cache.containsKey(url.toString())) {
                                cache.put(url.toString(), null);
                                DynamicTexture texture = new DynamicTexture(NativeImage.read(url.openStream()));
                                ResourceLocation id = mc.getTextureManager().register("skinpreview", texture);
                                cache.put(url.toString(), id);
                            }
                            if (cache.get(url.toString()) != null) {
                                TexturedOtherPlayerEntity entity = new TexturedOtherPlayerEntity(cache.get(url.toString()));
                                // TODO: replace with quaternion-based method?
                                InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, mc.screen.width/5, mc.screen.height/2+20, 40, -20, -20, entity);
                            }
                        }
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
