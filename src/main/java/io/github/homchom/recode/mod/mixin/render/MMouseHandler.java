package io.github.homchom.recode.mod.mixin.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.util.List;

@Mixin(MouseHandler.class)
public class MMouseHandler {
    @Shadow
    private double xpos;

    @Shadow
    private double ypos;

    private long cd = System.currentTimeMillis();

    @Inject(method = "onScroll(JDD)V", at = @At("HEAD"))
    private void onScroll(long window, double horiz, double vertical, CallbackInfo ci) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof ContainerScreen && LegacyConfig.getBoolean("quicknum")) {
            AbstractContainerMenu handler = ((ContainerScreen) screen).getMenu();
            List<Slot> slotList = handler.slots;

            double scale = Minecraft.getInstance().getWindow().getGuiScale();

            double mouseX = xpos;
            double mouseY = ypos;


            for (Slot slot : slotList) {
                double sX = Math.floor(((double) (screen.width - 176) / 2) + slot.x);
                double sY = Math.floor(((double) (screen.height - 166) / 2) + slot.y);
                sX *= scale;
                sY *= scale;


                if (sX < mouseX && mouseX < sX + (16 * scale)) {
                    if (sY < mouseY && mouseY < sY + (16 * scale)) {
                        if (System.currentTimeMillis() >= cd) {
                            if (Minecraft.getInstance().player != null && Minecraft.getInstance().gameMode != null && ItemUtil.isVar(slot.getItem(), "num")) {
                                if (Minecraft.getInstance().player.isCreative()) {
                                    cd = System.currentTimeMillis() + 250;
                                    ItemStack itemStack = slot.getItem().copy();

                                    CompoundTag tag = itemStack.getTag();

                                    if (tag == null) return;

                                    CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
                                    String varItem = publicBukkitValues.getString("hypercube:varitem");

                                    JsonObject parsedJson = JsonParser.parseString(varItem).getAsJsonObject();
                                    JsonObject data = parsedJson.get("data").getAsJsonObject();

                                    String name = data.get("name").getAsString();

                                    try {
                                        BigDecimal bigDecimal = new BigDecimal(name);

                                        if (Screen.hasControlDown()) {
                                            if (vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumSecondaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumSecondaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 0);
                                            }
                                        } else if (Screen.hasShiftDown()) {
                                            if (vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumTertiaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumTertiaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 0);
                                            }
                                        } else {
                                            if (vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumPrimaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(LegacyConfig.getDouble("quicknumPrimaryAmount")));
                                                if (LegacyConfig.getBoolean("quicknumSound"))
                                                    Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 1, 0);
                                            }
                                        }

                                        name = bigDecimal.toString();
                                        if (name.endsWith(".0")) name = name.substring(0, name.length() - 2);

                                        data.addProperty("name", name);
                                        parsedJson.add("data", data);
                                        publicBukkitValues.putString("hypercube:varitem", parsedJson.toString());
                                        tag.put("PublicBukkitValues", publicBukkitValues);

                                        itemStack.setTag(tag);
                                        itemStack.setHoverName(Component.literal(name)
                                                .withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withItalic(false)));

                                        ItemUtil.setContainerItem(slot.index, itemStack);
                                    } catch (NumberFormatException e) {
                                        if (LegacyConfig.getBoolean("quicknumSound"))
                                            Minecraft.getInstance().player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.PLAYERS, 1, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
