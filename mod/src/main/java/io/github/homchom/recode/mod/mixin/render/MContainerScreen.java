package io.github.homchom.recode.mod.mixin.render;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.sys.hypercube.codeaction.Action;
import io.github.homchom.recode.sys.hypercube.codeaction.ActionDump;
import io.github.homchom.recode.sys.hypercube.codeaction.Argument;
import io.github.homchom.recode.sys.hypercube.codeaction.DisplayItem;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag.Default;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ContainerScreen.class)
public abstract class MContainerScreen extends AbstractContainerScreen<ChestMenu> {
    public MContainerScreen(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String[] signT = LegacyRecode.signText;
        if (signT.length != 4) {
            return;
        }

        List<Action> actions = ActionDump.getActions(signT[1])
            .stream().filter(e -> Objects.equals(e.getCodeBlock().getName(), signT[0]))
            .filter(e -> Objects.equals(e.getName(), signT[1]))
            .toList();

        if (actions.size() == 1) {
            if (LegacyConfig.getBoolean("showCodeblockDescription")) {
                showDesc(actions.get(0), guiGraphics);
            }
            if (LegacyConfig.getBoolean("showParameterErrors")) {
                argCheck(actions.get(0), guiGraphics);
            }
        }
    }

    private void argCheck(Action a, GuiGraphics guiGraphics) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < menu.getContainer().getContainerSize(); i++) {
            items.add(menu.getContainer().getItem(i));
        }
        DisplayItem ditem = a.getIcon();

        if (ditem.getArguments() == null) {
            //happens for things like select entities by condition
            return;
        }

        // values menu would crash here.
        if (items.size() != 27) {
            return;
        }

        for (int i = 0; i < ditem.getTags(); i++) {
            items.set(26 - i, new ItemStack(Items.AIR));
        }

        List<String> errors = new ArrayList<>();

        List<Argument> rawArgs = Arrays.asList(ditem.getArguments());
        List<List<Integer>> currentOptions = new ArrayList<>();
        List<Integer> optionList = new ArrayList<>();
        Argument lastChecked = null;
        Boolean checkingOR = false;
        Boolean valid = false;
        Boolean passedAll = true;
        Boolean startsOR = false;
        Integer current = 0;
        Integer slot = 0;
        Integer checkSlot = 0;

        // Check if the first argument is an OR
        while (current < rawArgs.size()) {
            Argument rarg = rawArgs.get(current);
            if (rarg.getType() == null && rarg.getText().matches("OR")) {
                checkingOR = true;
                startsOR = true;
            }
            if (rarg.getType() == null) {
                break;
            }
            current ++;
        }

        current = 0;
        // Check for any errors
        while (current < rawArgs.size()) {
            Argument rarg = rawArgs.get(current);
            if (rarg.getType() != null) { optionList.add(current); }
            if (!checkingOR) {
                if (optionList.size() != 0) {
                    currentOptions.add(optionList);
                }
                List<String> expected = new ArrayList<>();
                for (List options : currentOptions) {
                    checkSlot = slot;
                    valid = true;
                    for (Object checkOption : options) {
                        Argument checkArgument = rawArgs.get((Integer) checkOption);
                        if (lastChecked != null && lastChecked.isPlural() && typeCheck(lastChecked.getType(), items.get(checkSlot))) {
                            checkSlot ++;
                            continue;
                        }
                        lastChecked = checkArgument;
                        if (!typeCheck(checkArgument.getType(), items.get(checkSlot))) {
                            if (checkArgument.isOptional() && !items.get(checkSlot).isEmpty()) {
                                continue;
                            }
                            if (!checkArgument.isOptional()) {
                                valid = false;
                                break;
                            }
                        }
                        checkSlot ++;
                    }
                    if (!valid) {
                        expected.add(lastChecked.getType());
                    } else {
                        expected = new ArrayList<>();
                        break;
                    }
                }
                if (expected.size() == 1) {
                    errors.add("§cExpected §6" + expected.get(0) + " §cin slot " + (checkSlot + 1) + " but got §6" + getType(items.get(checkSlot)));
                    slot = checkSlot;
                    passedAll = false;
                    break;
                }else { slot = checkSlot; }
                currentOptions = new ArrayList<>();
                optionList = new ArrayList<>();
            }
            if (rarg.getType() == null) {
                if (rarg.getText().equals("OR")) {
                    currentOptions.add(optionList);
                    optionList = new ArrayList<>();
                }else {
                    if (checkingOR) {
                        if (optionList.size() != 0) {
                            currentOptions.add(optionList);
                        }
                        List<String> expected = new ArrayList<>();
                        for (List options : currentOptions) {
                            checkSlot = slot;
                            valid = true;
                            for (Object checkOption : options) {
                                Argument checkArgument = rawArgs.get((Integer) checkOption);
                                if (lastChecked != null && lastChecked.isPlural() && typeCheck(lastChecked.getType(), items.get(checkSlot))) {
                                    checkSlot ++;
                                    continue;
                                }
                                lastChecked = checkArgument;
                                if (!typeCheck(checkArgument.getType(), items.get(checkSlot))) {
                                    if (checkArgument.isOptional() && !items.get(checkSlot).isEmpty()) {
                                        continue;
                                    }
                                    if (!checkArgument.isOptional()) {
                                        valid = false;
                                        break;
                                    }
                                }
                                checkSlot ++;
                            }
                            if (!valid) {
                                expected.add(lastChecked.getType());
                            } else {
                                expected = new ArrayList<>();
                                break;
                            }
                        }
                        if (expected.size() != 0) {
                            errors.add("§cExpected one of §6" + expected + " §cin slot " + (checkSlot + 1) + " but got §6" + getType(items.get(checkSlot)));
                            slot = checkSlot;
                            passedAll = false;
                            break;
                        }else { slot = checkSlot; }
                        currentOptions = new ArrayList<>();
                        optionList = new ArrayList<>();
                    }
                    checkingOR = !checkingOR;
                }
            }
            current ++;
        }

        // Check for any OR that is at the end of the argument list
        if (checkingOR) {
            if (optionList.size() != 0) {
                currentOptions.add(optionList);
            }
            List<String> expected = new ArrayList<>();
            for (List options : currentOptions) {
                checkSlot = slot;
                valid = true;
                for (Object checkOption : options) {
                    Argument checkArgument = rawArgs.get((Integer) checkOption);
                    if (lastChecked != null && lastChecked.isPlural() && typeCheck(lastChecked.getType(), items.get(checkSlot))) {
                        checkSlot ++;
                        continue;
                    }
                    lastChecked = checkArgument;
                    if (!typeCheck(checkArgument.getType(), items.get(checkSlot))) {
                        if (checkArgument.isOptional() && !items.get(checkSlot).isEmpty()) {
                            continue;
                        }
                        if (!checkArgument.isOptional()) {
                            valid = false;
                            break;
                        }
                    }
                    checkSlot ++;
                }
                if (!valid) {
                    expected.add(lastChecked.getType());
                } else {
                    expected = new ArrayList<>();
                    break;
                }
            }
            if (expected.size() != 0) {
                errors.add("§cExpected one of §6" + expected + " §cin slot " + (checkSlot + 1) + " but got §6" + getType(items.get(checkSlot)));
                slot = checkSlot;
                passedAll = false;
            }else { slot = checkSlot; }
        }

        // Check for extra data not requested by arguments.
        Integer slotCheckIndex = 0;
        if (passedAll && startsOR) { slot --; }
        for (ItemStack slotCheck : items) {
            if (slotCheckIndex >= 25-ditem.getTags()) { break; }
            if (lastChecked != null && lastChecked.isPlural() && typeCheck(lastChecked.getType(), slotCheck)) {
                slotCheckIndex ++;
                continue;
            }
            if (slotCheckIndex >= slot + 1 && !slotCheck.isEmpty()) {
                errors.add("§cExpected §6NONE §cin slot " + (slotCheckIndex + 1) + " but got §6" + getType(slotCheck));
            }
            slotCheckIndex ++;
        }

        int y = 0;
        for (String line : errors) {
            Component text = TextUtil.colorCodesToTextComponent(line);
            guiGraphics.drawString(Minecraft.getInstance().font, text, Minecraft.getInstance().screen.width - font.width(text) - 10, 10 + y, 0xffffff);
            y += 10;
        }
    }

    protected List<List<Argument>> genPossible(List<Argument> args, int i) {
        List<List<Argument>> p = new ArrayList<>();
        if (args.size() <= i) {
            return Collections.singletonList(args);
        }

        if (args.get(i).isOptional()) {
            List<Argument> copy = new ArrayList<>(args);
            copy.remove(i);
            p.addAll(genPossible(copy, i));
        }
        p.addAll(genPossible(args, i + 1));
        return p;
    }

    private boolean typeCheck(String type, ItemStack item) {
        CompoundTag pbv = item.getTagElement("PublicBukkitValues");
        String varitemtype = "";
        if (pbv != null) {
            String t = pbv.getString("hypercube:varitem");
            if (t != null) {
                try {
                    JsonObject o = JsonParser.parseString(t).getAsJsonObject();
                    varitemtype = o.get("id").getAsString();
                    if (Objects.equals(varitemtype, "var") || Objects.equals(varitemtype, "g_val")) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        switch (varitemtype) {
            case "var":
                return true;
            case "g_val":
                return true;
        }

        switch (type) {
            case "NONE":
                if (item.getItem() == Items.AIR) {
                    return true;
                }
                break;
            case "BLOCK":
            case "PROJECTILE":
            case "VEHICLE":
            case "SPAWN_EGG":
            case "ENTITY_TYPE":
            case "ITEM":
                if (item.getItem() != Items.AIR && varitemtype.matches("")) {
                    return true;
                }
                break;
            case "NUMBER":
                if (Objects.equals(varitemtype, "num")) {
                    return true;
                }
                break;
            case "TEXT":
            case "BLOCK_TAG":
                if (Objects.equals(varitemtype, "txt")) {
                    return true;
                }
                break;
            case "PARTICLE":
                if (Objects.equals(varitemtype, "part")) {
                    return true;
                }
                break;
            case "LOCATION":
                if (Objects.equals(varitemtype, "loc")) {
                    return true;
                }
                break;
            case "VECTOR":
                if (Objects.equals(varitemtype, "vec")) {
                    return true;
                }
                break;
            case "SOUND":
                if (Objects.equals(varitemtype, "snd")) {
                    return true;
                }
                break;
            case "POTION":
                if (Objects.equals(varitemtype, "pot")) {
                    return true;
                }
                break;
            case "LIST":
            case "ANY_TYPE":
                return true;
        }
        return false;
    }

    private String getType (ItemStack item) {
        CompoundTag pbv = item.getTagElement("PublicBukkitValues");
        String varitemtype = "";
        Map<String, String> convert = new HashMap<String,String>();
        convert.put("num", "Number");
        convert.put("txt", "Text");
        convert.put("loc", "Location");
        convert.put("vec", "Vector");
        convert.put("snd", "Sound");
        convert.put("part", "Particle");
        convert.put("pot", "Potion");
        convert.put("var", "Variable");
        convert.put("g_val", "Game Value");
        if (pbv != null) {
            String t = pbv.getString("hypercube:varitem");
            if (t != null) {
                try {
                    JsonObject o = JsonParser.parseString(t).getAsJsonObject();
                    varitemtype = o.get("id").getAsString();
                    return convert.get(varitemtype);
                } catch (Exception ignored) {}
            }
        }else {
            if (item.getItem() != Items.AIR) { return "Item"; }
            return "None";
        }
        return "UNKNOWN";
    }

    private void showDesc(Action a, GuiGraphics guiGraphics) {
        DisplayItem icon = a.getIcon();

        List<Component> desc = icon.toItemStack().getTooltipLines(Minecraft.getInstance().player, Default.NORMAL);

        int y = 0;
        for (Component line : desc) {
            guiGraphics.drawString(Minecraft.getInstance().font, line, 10, 10 + y, 0xffffff);
            y += 10;
        }
    }
}
