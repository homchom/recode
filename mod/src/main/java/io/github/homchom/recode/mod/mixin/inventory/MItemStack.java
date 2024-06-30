package io.github.homchom.recode.mod.mixin.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import io.github.homchom.recode.mod.features.keybinds.Keybinds;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(ItemStack.class)
public abstract class MItemStack {
    @Shadow
    @Nullable
    public abstract CompoundTag getTagElement(String key);

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void getTooltipLines(Player player, TooltipFlag context,
        CallbackInfoReturnable<List<Component>> cir) {

        if (player == null) return;

        try {
            String cname = FabricLoader.getInstance().isDevelopmentEnvironment() ? "key" : "field_1655";

            int keycode = ((Key) FieldUtils.getField(KeyMapping.class,cname,true).get(Keybinds.showTags)).getValue();

            if (keycode == -1) return;

            if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),keycode)) {
                List<Component> t = cir.getReturnValue();

                CompoundTag tags = getTagElement("PublicBukkitValues");

                if (tags != null) {
                    Set<String> keys = tags.getAllKeys();
                    if (keys.size() != 0) {
                        t.add(Component.literal(""));

                        for (String key : keys) {
                            String value = tags.get(key).getAsString();
                            if (value.length()>20) value = value.substring(0,30)+"...";
                            key = key.replaceFirst("hypercube:","");

                            t.add(Component.literal("§a" + key + " §7= §f" + value));
                        }
                    }
                }

                cir.setReturnValue(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
