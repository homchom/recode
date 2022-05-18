package io.github.homchom.recode.mod.mixin.render;

import com.google.gson.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.*;
import net.minecraft.world.inventory.*;
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
        Screen screen = Recode.MC.screen;
        if (screen instanceof ContainerScreen && Config.getBoolean("quicknum")) {
            AbstractContainerMenu handler = ((ContainerScreen) screen).getMenu();
            List<Slot> slotList = handler.slots;

            double scale = Recode.MC.getWindow().getGuiScale();

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
                            if (Recode.MC.player != null && Recode.MC.gameMode != null && ItemUtil.isVar(slot.getItem(), "num")) {
                                if (Recode.MC.player.isCreative()) {
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
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumSecondaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumSecondaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
                                            }
                                        } else if (Screen.hasShiftDown()) {
                                            if (vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumTertiaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumTertiaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
                                            }
                                        } else {
                                            if (vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(Config.getDouble("quicknumPrimaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 1);
                                            } else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(Config.getDouble("quicknumPrimaryAmount")));
                                                if (Config.getBoolean("quicknumSound"))
                                                    Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_HAT, SoundSource.PLAYERS, 1, 0);
                                            }
                                        }

                                        name = bigDecimal.toString();
                                        if (name.endsWith(".0")) name = name.substring(0, name.length() - 2);

                                        data.addProperty("name", name);
                                        parsedJson.add("data", data);
                                        publicBukkitValues.putString("hypercube:varitem", parsedJson.toString());
                                        tag.put("PublicBukkitValues", publicBukkitValues);

                                        itemStack.setTag(tag);
                                        itemStack.setHoverName(new TextComponent(name)
                                                .withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED)).withItalic(false)));

                                        ItemUtil.setContainerItem(slot.index, itemStack);
                                    } catch (NumberFormatException e) {
                                        if (Config.getBoolean("quicknumSound"))
                                            Recode.MC.player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO, SoundSource.PLAYERS, 1, 0);
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
