package io.github.homchom.recode.mod.mixin.render;

import com.google.gson.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
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
        Minecraft mc = LegacyRecode.MC;
        Screen screen = mc.screen;

        if (screen instanceof MAbstractContainerScreen<?> containerScreen &&
            mc.player != null && mc.gameMode != null && mc.player.isCreative() &&
            Config.getBoolean("quicknum") && System.currentTimeMillis() >= cd
        ) {
            List<Slot> slotList = mc.player.containerMenu.slots;
            double scale = mc.getWindow().getGuiScale();

            for (Slot slot : slotList) {
                if (containerScreen.isHovering(slot, xpos / scale, ypos / scale) && ItemUtil.isVar(slot.getItem(), "num")) {
                    int slotIndex;
                    // Special case for when you're in the creative inventory
                    if (screen instanceof CreativeModeInventoryScreen creativeScreen) {
                        if (creativeScreen.getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId()) return;
                        if (slot.y >= 112) {
                            slotIndex = (slot.x - 9) / 18 + 36 + 2;
                        } else if (slot.y >= 54) {
                            slotIndex = (slot.y - 18) / 18 * 9 + (slot.x - 9) / 18 - 9 + 2;
                        } else {
                            return;
                        }
                    } else {
                        slotIndex = slot.index;
                    }
                    cd = System.currentTimeMillis() + (long)(Config.getDouble("quicknumDelay") * 1000);
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
                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumSecondaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                            } else {
                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumSecondaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
                            }
                        } else if (Screen.hasShiftDown()) {
                            if (vertical > 0) {
                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumTertiaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                            } else {
                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumTertiaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
                            }
                        } else {
                            if (vertical > 0) {
                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumPrimaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                            } else {
                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumPrimaryAmount")));
                                if (Config.getBoolean("quicknumSound"))
                                    mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
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

                        ItemUtil.setContainerItem(slotIndex, itemStack);
                    } catch (NumberFormatException e) {
                        if (Config.getBoolean("quicknumSound"))
                            mc.player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.PLAYERS, 1, 0);
                    }

                    return;
                }
            }
        }
    }
}
