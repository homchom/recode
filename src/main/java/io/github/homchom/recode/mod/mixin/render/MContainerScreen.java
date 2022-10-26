package io.github.homchom.recode.mod.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.hypercube.codeaction.*;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.TooltipFlag.Default;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;

@Mixin(ContainerScreen.class)
public abstract class MContainerScreen extends AbstractContainerScreen<ChestMenu> {
    public MContainerScreen(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String[] signt = LegacyRecode.signText;
        if (signt.length != 4) {
            return;
        }

        List<Action> actions = ActionDump.getActions(signt[1])
            .stream().filter(e -> Objects.equals(e.getCodeBlock().getName(), signt[0]))
            .filter(e -> Objects.equals(e.getName(), signt[1]))
            .collect(Collectors.toList());

        if (actions.size() == 1) {
            if (Config.getBoolean("showCodeblockDescription")) {
                showDesc(actions.get(0), poseStack);
            }
            if (Config.getBoolean("showParameterErrors")) {
                argCheck(actions.get(0), poseStack);
            }
        }
    }

    private void argCheck(Action a, PoseStack matrices) {
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
        boolean checkingOR = false;
        boolean valid = false;
        boolean passedAll = true;
        boolean startsOR = false;
        int current = 0;
        int slot = 0;
        int checkSlot = 0;

        // Check if the first argument is an OR
        while (current < rawArgs.size()) {
            Argument rarg = rawArgs.get(current);
            if (!rarg.isValueArgument() && rarg.getText().equals("OR")) {
                checkingOR = true;
                startsOR = true;
            }
            if (!rarg.isValueArgument()) {
                break;
            }
            current ++;
        }

        current = 0;
        // Check for any errors
        while (current < rawArgs.size()) {
            Argument rarg = rawArgs.get(current);
            if (rarg.isValueArgument()) optionList.add(current);
            if (!checkingOR) {
                if (optionList.size() != 0) {
                    currentOptions.add(optionList);
                }
                List<String> expected = new ArrayList<>();
                for (List<Integer> options : currentOptions) {
                    checkSlot = slot;
                    valid = true;
                    for (int checkOption : options) {
                        Argument checkArgument = rawArgs.get(checkOption);
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
                        expected.add(lastChecked.getType().getName());
                    } else {
                        expected = new ArrayList<>();
                        break;
                    }
                }
                if (expected.size() == 1) {
                    errors.add("§cExpected §6" + expected.get(0) + " §cin slot " + (checkSlot + 1) + " but got §6" + Argument.ValueType.fromItemStack(items.get(checkSlot)).getName());
                    slot = checkSlot;
                    passedAll = false;
                    break;
                }else { slot = checkSlot; }
                currentOptions = new ArrayList<>();
                optionList = new ArrayList<>();
            }
            if (!rarg.isValueArgument()) {
                if (rarg.getText().equals("OR")) {
                    currentOptions.add(optionList);
                    optionList = new ArrayList<>();
                }else {
                    if (checkingOR) {
                        if (optionList.size() != 0) {
                            currentOptions.add(optionList);
                        }
                        List<String> expected = new ArrayList<>();
                        for (List<Integer> options : currentOptions) {
                            checkSlot = slot;
                            valid = true;
                            for (int checkOption : options) {
                                Argument checkArgument = rawArgs.get(checkOption);
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
                                expected.add(lastChecked.getType().getName());
                            } else {
                                expected = new ArrayList<>();
                                break;
                            }
                        }
                        if (expected.size() != 0) {
                            errors.add("§cExpected one of §6" + expected + " §cin slot " + (checkSlot + 1) + " but got §6" + Argument.ValueType.fromItemStack(items.get(checkSlot)).getName());
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
            for (List<Integer> options : currentOptions) {
                checkSlot = slot;
                valid = true;
                for (int checkOption : options) {
                    Argument checkArgument = rawArgs.get(checkOption);
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
                    expected.add(lastChecked.getType().getName());
                } else {
                    expected = new ArrayList<>();
                    break;
                }
            }
            if (expected.size() != 0) {
                errors.add("§cExpected one of §6" + expected + " §cin slot " + (checkSlot + 1) + " but got §6" + Argument.ValueType.fromItemStack(items.get(checkSlot)).getName());
                slot = checkSlot;
                passedAll = false;
            }else { slot = checkSlot; }
        }

        // Check for extra data not requested by arguments.
        int slotCheckIndex = 0;
        if (passedAll && startsOR) { slot --; }
        for (ItemStack slotCheck : items) {
            if (slotCheckIndex >= 25-ditem.getTags()) { break; }
            if (lastChecked != null && lastChecked.isPlural() && typeCheck(lastChecked.getType(), slotCheck)) {
                slotCheckIndex ++;
                continue;
            }
            if (slotCheckIndex >= slot + 1 && !slotCheck.isEmpty()) {
                errors.add("§cExpected §6None §cin slot " + (slotCheckIndex + 1) + " but got §6" + Argument.ValueType.fromItemStack(slotCheck).getName());
            }
            slotCheckIndex ++;
        }

        int y = 0;
        for (String line : errors) {
            Component text = TextUtil.colorCodesToTextComponent(line);
            LegacyRecode.MC.font.draw(matrices, text, LegacyRecode.MC.screen.width - font.width(text) - 10, 10 + y, 0xffffff);
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

    private boolean typeCheck(Argument.ValueType type, ItemStack item) {
        return type.isCompatibleWith(Argument.ValueType.fromItemStack(item));
    }

    private void showDesc(Action a, PoseStack matrices) {
        DisplayItem icon = a.getIcon();

        List<Component> desc = icon.toItemStack().getTooltipLines(LegacyRecode.MC.player, Default.NORMAL);

        int y = 0;
        for (Component line : desc) {
            LegacyRecode.MC.font.draw(matrices, line, 10, 10 + y, 0xffffff);
            y += 10;
        }
    }
}
