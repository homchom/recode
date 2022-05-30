package io.github.homchom.recode.mod.commands.impl.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import io.github.homchom.recode.mod.commands.Command;
import io.github.homchom.recode.mod.commands.arguments.ArgBuilder;
import io.github.homchom.recode.mod.commands.arguments.types.ChoiceArgumentType;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.*;

import java.util.Arrays;

public class RelativeLocCommand extends Command {

    private static final String[] TARGET_TYPES = {"selection", "default", "damager", "killer", "victim", "shooter", "projectile"};

    @Override
    public void register(Minecraft mc, CommandDispatcher<FabricClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("relativeloc")
                .then(ArgBuilder.argument("target", ChoiceArgumentType.choice(TARGET_TYPES))
                        .then(ArgBuilder.argument("forwards", FloatArgumentType.floatArg())
                                .then(ArgBuilder.argument("upwards", FloatArgumentType.floatArg())
                                        .then(ArgBuilder.argument("right", FloatArgumentType.floatArg())
                                                .then(ArgBuilder.argument("rot_down", FloatArgumentType.floatArg())
                                                        .then(ArgBuilder.argument("rot_right", FloatArgumentType.floatArg())
                                                                .executes(ctx -> {
                                                                    if (this.isCreative(mc)) {
                                                                        String target = ctx.getArgument("target", String.class);
                                                                        Float forwards = ctx.getArgument("forwards", float.class);
                                                                        Float upwards = ctx.getArgument("upwards", float.class);
                                                                        Float right = ctx.getArgument("right", float.class);
                                                                        Float rot_down = ctx.getArgument("rot_down", float.class);
                                                                        Float rot_right = ctx.getArgument("rot_right", float.class);
                                                                        return this.run(target, forwards, upwards, right, rot_down, rot_right);
                                                                    } else {
                                                                        return -1;
                                                                    }
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public String getDescription() {
        return "[blue]/relativeloc <target> <forwards> <upwards> <right> <rot_down> <rot_right>[reset]\n"
                + "\n"
                + "Gives you the Relative Location item that you can use in your code."
                + "[red]Disclaimer[reset]: Relative Locations are deprecated code item that is not recommended to use. Use them on your own risk.";
    }

    @Override
    public String getName() {
        return "/relativeloc";
    }

    private int run(String target, float forwards, float upwards, float right, float rot_down, float rot_right) {
        String[] targetNames = {"Selected Object", "Default", "Damager", "Killer", "Victim", "Shooter", "Projectile"};
        String finalTarget = targetNames[Arrays.asList(TARGET_TYPES).indexOf(target)];

        CompoundTag publicBukkitNBT = new CompoundTag();
        CompoundTag itemNBT = new CompoundTag();
        CompoundTag codeNBT = new CompoundTag();
        CompoundTag dataNBT = new CompoundTag();

        dataNBT.putString("target", finalTarget);
        dataNBT.putFloat("forward", forwards);
        dataNBT.putFloat("up", upwards);
        dataNBT.putFloat("right", right);
        dataNBT.putFloat("rot_down", rot_down);
        dataNBT.putFloat("rot_right", rot_right);
        codeNBT.put("data", dataNBT);

        codeNBT.putString("id", "r_loc");
        publicBukkitNBT.putString("hypercube:varitem", codeNBT.toString());

        ItemStack item = new ItemStack(Items.PAPER);
        itemNBT.put("PublicBukkitValues", publicBukkitNBT);

        itemNBT.putInt("CustomModelData", 500);

        item.setTag(itemNBT);

        CompoundTag display = new CompoundTag();
        ListTag lore = new ListTag();

        TextComponent itemName = new TextComponent("Relative Location");
        itemName.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(false));
        display.put("Name", StringTag.valueOf(Component.Serializer.toJson(itemName)));

        TextComponent lore1 = new TextComponent("Target: ");
        TextComponent lore2 = new TextComponent(finalTarget);
        lore1.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false));
        lore.addTag(0, StringTag.valueOf(Component.Serializer.toJson(lore1.append(lore2))));

        lore1 = new TextComponent("Forwards: ");
        craftLore(forwards, lore, lore1);

        lore1 = new TextComponent("Upwards: ");
        craftLore(upwards, lore, lore1);

        lore1 = new TextComponent("Right: ");
        craftLore(right, lore, lore1);

        lore1 = new TextComponent("Rot Down: ");
        craftLore(rot_down, lore, lore1);

        lore1 = new TextComponent("Rot Right: ");
        craftLore(rot_right, lore, lore1);

        display.put("Lore", lore);
        item.getTag().put("display", display);

        ItemUtil.giveCreativeItem(item, true);
        return 1;
    }

    private void craftLore(float upwards, ListTag lore, TextComponent lore1) {
        TextComponent lore2;
        lore2 = new TextComponent("" + upwards);
        lore1.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.valueOf(Component.Serializer.toJson(lore1.append(lore2))));
    }
}
