package io.github.homchom.recode.mod.mixin.render;

import com.mojang.authlib.GameProfile;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.render.StaticSkinRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(AbstractContainerScreen.class)
public abstract class MAbstractContainerScreen {
    @Inject(method = "renderTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderTooltip(GuiGraphics guiGraphics, int i, int j, CallbackInfo ci, ItemStack stack) {
        if (LegacyConfig.getBoolean("previewHeadSkins")) {
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof AbstractSkullBlock) {
                    previewHeadSkin(guiGraphics, stack);
                }
            }
        }
    }

    @Unique
    private void previewHeadSkin(GuiGraphics guiGraphics, ItemStack headStack) {
        Minecraft mc = Minecraft.getInstance();

        CompoundTag tag = headStack.getTag();
        if (tag == null) return;
        GameProfile profile = NbtUtils.readGameProfile(tag.getCompound("SkullOwner"));
        if (profile == null) return;

        var skin = mc.getSkinManager().getInsecureSkin(profile);
        var entity = new StaticSkinRender(mc, skin);
        var x = Objects.requireNonNull(mc.screen).width / 5;
        var y = mc.screen.height / 2 + 20;
        // TODO: reduce magic
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics,
                x - 35, y - 50,
                x + 35, y + 50,
                40,
                0.0625f,
                x - 20, y - 20,
                entity
        );
    }
}
