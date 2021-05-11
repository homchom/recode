package io.github.codeutilities.mixin.misc;

import com.google.gson.JsonObject;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.CodeUtilsConfig;
import io.github.codeutilities.util.misc.ItemUtil;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.util.List;

@Mixin(Mouse.class)
public class MixinMouse {
    @Shadow
    private double x;

    @Shadow
    private double y;

    private long cd = System.currentTimeMillis();

    @Inject(method = "onMouseScroll(JDD)V", at = @At("HEAD"))
    private void onMouseScroll(long window, double horiz, double vertical, CallbackInfo ci) {
        Screen screen = CodeUtilities.MC.currentScreen;
        if(screen instanceof GenericContainerScreen && CodeUtilsConfig.getBoolean("quicknum")) {
            ScreenHandler handler = ((GenericContainerScreen) screen).getScreenHandler();
            List<Slot> slotList = handler.slots;

            double scale = CodeUtilities.MC.getWindow().getScaleFactor();

            double mouseX = x;
            double mouseY = y;


            for(Slot slot:slotList) {
                double sX = Math.floor(((double) (screen.width - 176) / 2) + slot.x);
                double sY = Math.floor(((double) (screen.height - 166) / 2) + slot.y);
                sX *= scale;
                sY *= scale;


                if(sX < mouseX && mouseX < sX + (16 * scale)) {
                    if (sY < mouseY && mouseY < sY + (16 * scale)) {
                        if(System.currentTimeMillis() >= cd) {
                            if (CodeUtilities.MC.player != null && CodeUtilities.MC.interactionManager != null && ItemUtil.isVar(slot.getStack(), "num")) {
                                if(CodeUtilities.MC.player.isCreative()) {
                                    cd = System.currentTimeMillis() + 250;
                                    ItemStack itemStack = slot.getStack().copy();

                                    CompoundTag tag = itemStack.getTag();

                                    if(tag == null) return;

                                    CompoundTag publicBukkitValues = tag.getCompound("PublicBukkitValues");
                                    String varItem = publicBukkitValues.getString("hypercube:varitem");

                                    JsonObject parsedJson = CodeUtilities.JSON_PARSER.parse(varItem).getAsJsonObject();
                                    JsonObject data = parsedJson.get("data").getAsJsonObject();

                                    String name = data.get("name").getAsString();

                                    try {
                                        BigDecimal bigDecimal = new BigDecimal(name);

                                        if(Screen.hasControlDown()) {
                                            if(vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumSecondaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 1);
                                            }else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumSecondaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 0);
                                            }
                                        }else if(Screen.hasShiftDown()){
                                            if(vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumTertiaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 1);
                                            }else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumTertiaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 0);
                                            }
                                        }else {
                                            if(vertical > 0) {
                                                bigDecimal = bigDecimal.add(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumPrimaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 1);
                                            }else {
                                                bigDecimal = bigDecimal.subtract(BigDecimal.valueOf(CodeUtilsConfig.getDouble("quicknumPrimaryAmount")));
                                                if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_HAT, SoundCategory.PLAYERS, 1, 0);
                                            }
                                        }

                                        name = bigDecimal.toString();
                                        if (name.endsWith(".0")) name = name.substring(0, name.length() - 2);

                                        data.addProperty("name", name);
                                        parsedJson.add("data", data);
                                        publicBukkitValues.putString("hypercube:varitem", parsedJson.toString());
                                        tag.put("PublicBukkitValues", publicBukkitValues);

                                        itemStack.setTag(tag);
                                        itemStack.setCustomName(new LiteralText(name)
                                                .styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED)).withItalic(false)));

                                        ItemUtil.setContainerItem(slot.id, itemStack);
                                    }catch(NumberFormatException e) {
                                        if(CodeUtilsConfig.getBoolean("quicknumSound")) CodeUtilities.MC.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, SoundCategory.PLAYERS, 1, 0);
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
