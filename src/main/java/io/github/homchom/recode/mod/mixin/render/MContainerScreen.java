package io.github.homchom.recode.mod.mixin.render;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.sys.hypercube.codeaction.*;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.nbt.CompoundTag;
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

@SuppressWarnings("ALL")
@Mixin(ContainerScreen.class)
public abstract class MContainerScreen extends AbstractContainerScreen<ChestMenu> {
    public MContainerScreen(ChestMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void render(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String[] signt = Recode.signText;
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

        if (ditem.getArguments()==null) {
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
        List<List<Argument>> possible = new ArrayList<>();
        List<Argument> current = new ArrayList<>();
        List<Argument> always = new ArrayList<>();

        for (Argument rarg : rawArgs) {
            if (rarg.getType() == null) {
                if (Objects.equals(rarg.getText(), "")) {
                    if (rawArgs.indexOf(rarg) != rawArgs.size() - 1) {//check is needed since for FallingBlock there are two empty lines
                        //empty line showing where to trim the or
                        always.addAll(current);
                        current.clear();
                    }
                } else {
                    //or
                    List<Argument> p = new ArrayList<>();
                    p.addAll(always);
                    p.addAll(current);
                    possible.add(p);
                    current.clear();
                }
            } else if (!Objects.equals(rarg.getType(), "NONE")) {
                current.add(rarg);
            }
        }
        List<Argument> p = new ArrayList<>();
        p.addAll(always);
        p.addAll(current);
        possible.add(p);
        current.clear();

        List<List<Argument>> possible2 = new ArrayList<>();
        for (List<Argument> args : possible) {
            possible2.addAll(genPossible(args, 0));
        }

        int furthest = 0;
        HashSet<String> fixes = new HashSet<>();
        boolean valid = false;
        main:
        for (List<Argument> args : possible2) {
            int itemi = 0;
            Argument last = null;
            for (int argi = 0; argi < args.size(); argi++) {
                if (itemi >= 26) {
                    continue main;
                }
                Argument arg = args.get(argi);
                if (typeCheck(arg.getType(), items.get(itemi))) {
                    itemi++;
                    last = arg;
                } else if (last != null && last.isPlural() && typeCheck(last.getType(), items.get(itemi))) {
                    argi--;
                    itemi++;
                } else if (furthest < itemi) {
                    furthest = itemi;
                    fixes = new HashSet<>();
                    fixes.add(arg.getType());
                    continue main;
                } else {
                    continue main;
                }
            }
            while (itemi < items.size()) {
                if (last != null && last.isPlural() && typeCheck(last.getType(), items.get(itemi))) {
                    itemi++;
                } else if (items.get(itemi).getItem() != Items.AIR) {
                    furthest = itemi;
                    fixes.add("AIR");
                    continue main;
                }
                itemi++;
            }
            valid = true;
        }
        if (!valid) {
            errors.add("Expected one of " + fixes + " at " + (furthest + 1) + " but got " + items.get(furthest).getItem());
        }

        int y = 0;
        for (String line : errors) {
            Component text = TextUtil.colorCodesToTextComponent("§c" + line);
            Recode.MC.font.draw(matrices, text, Recode.MC.screen.width - font.width(text) - 10, 10 + y, 0xffffff);
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

        switch (type) {
            case "ITEM":
            case "BLOCK":
            case "PROJECTILE":
            case "VEHICLE":
            case "SPAWN_EGG":
            case "ENTITY_TYPE":
                if (item.getItem() != Items.AIR) {
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

    private void showDesc(Action a, PoseStack matrices) {
        DisplayItem icon = a.getIcon();

        List<Component> desc = icon.toItemStack().getTooltipLines(Recode.MC.player, Default.NORMAL);

        int y = 0;
        for (Component line : desc) {
            Recode.MC.font.draw(matrices, line, 10, 10 + y, 0xffffff);
            y += 10;
        }
    }
}
