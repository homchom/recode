package io.github.codeutilities.mod.mixin.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.Config;
import io.github.codeutilities.sys.renderer.TexturedOtherPlayerEntity;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class MScreen {

    private final HashMap<String, Identifier> cache = new HashMap<>();

    @Inject(method = "onClose", at = @At("HEAD"))
    private void onClose(CallbackInfo ci) {
        CodeUtilities.signText = new String[0];
    }

    @Inject(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At("HEAD"))
    private void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
        try {
            if (Config.getBoolean("previewHeadSkins")) {
                Item item = stack.getItem();
                if (item instanceof BlockItem) {
                    Block block = ((BlockItem) item).getBlock();
                    if (block instanceof AbstractSkullBlock) {
                        GameProfile gameProfile = null;
                        if (stack.hasTag()) {
                            CompoundTag compoundTag = stack.getTag();
                            if (compoundTag.contains("SkullOwner", 10)) {
                                gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
                            } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                                gameProfile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                                gameProfile = SkullBlockEntity.loadProperties(gameProfile);
                                compoundTag.remove("SkullOwner");
                                compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
                            }
                        }

                        MinecraftClient mc = CodeUtilities.MC;

                        Map<Type, MinecraftProfileTexture> textures = mc.getSkinProvider().getTextures(gameProfile);
                        if (textures.containsKey(Type.SKIN)) {
                            URL url = new URL(textures.get(Type.SKIN).getUrl());

                            if (!cache.containsKey(url.toString())) {
                                cache.put(url.toString(), null);
                                NativeImageBackedTexture texture = new NativeImageBackedTexture(NativeImage.read(url.openStream()));
                                Identifier id = mc.getTextureManager().registerDynamicTexture("skinpreview", texture);
                                cache.put(url.toString(), id);
                            }
                            if (cache.get(url.toString()) != null) {
                                TexturedOtherPlayerEntity entity = new TexturedOtherPlayerEntity(cache.get(url.toString()));
                                InventoryScreen.drawEntity(mc.currentScreen.width/5, mc.currentScreen.height/2+20, 40, -20, -20, entity);
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
