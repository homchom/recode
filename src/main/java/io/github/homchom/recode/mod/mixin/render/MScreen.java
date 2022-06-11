package io.github.homchom.recode.mod.mixin.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.renderer.TexturedOtherPlayerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.util.*;

@Mixin(Screen.class)
public class MScreen {
    private final HashMap<String, ResourceLocation> cache = new HashMap<>();

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        LegacyRecode.signText = new String[0];
    }

    @Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderTooltip(PoseStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
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

                        Minecraft mc = LegacyRecode.MC;

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
                                InventoryScreen.renderEntityInInventory(mc.screen.width/5, mc.screen.height/2+20, 40, -20, -20, entity);
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
