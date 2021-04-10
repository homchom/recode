package io.github.codeutilities.commands.item;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import io.github.codeutilities.commands.Command;
import io.github.codeutilities.commands.arguments.ArgBuilder;
import io.github.codeutilities.commands.arguments.types.StringListArgumentType;
import io.github.codeutilities.util.ChatType;
import io.github.codeutilities.util.ChatUtil;
import io.github.codeutilities.util.ItemUtil;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Arrays;

public class RelativeLocCommand extends Command {

    private static final String[] targetTypes = {"selection", "default", "damager", "killer", "victim", "shooter", "projectile"};

    @Override
    public void register(MinecraftClient mc, CommandDispatcher<CottonClientCommandSource> cd) {
        cd.register(ArgBuilder.literal("relativeloc")
            .then(ArgBuilder.argument("target", StringListArgumentType.string(targetTypes))
                .then(ArgBuilder.argument("forwards", FloatArgumentType.floatArg())
                    .then(ArgBuilder.argument("upwards", FloatArgumentType.floatArg())
                        .then(ArgBuilder.argument("right", FloatArgumentType.floatArg())
                            .then(ArgBuilder.argument("rot_down", FloatArgumentType.floatArg())
                                .then(ArgBuilder.argument("rot_right", FloatArgumentType.floatArg())
                                    .executes(ctx -> {
                                        if (mc.player.isCreative()) {
                                            String target = ctx.getArgument("target", String.class);
                                            Float forwards = ctx.getArgument("forwards", float.class);
                                            Float upwards = ctx.getArgument("upwards", float.class);
                                            Float right = ctx.getArgument("right", float.class);
                                            Float rot_down = ctx.getArgument("rot_down", float.class);
                                            Float rot_right = ctx.getArgument("rot_right", float.class);
                                            return this.run(mc, target, forwards, upwards, right, rot_down, rot_right);
                                        }else {
                                            ChatUtil.sendTranslateMessage("codeutilities.command.require_creative_mode", ChatType.FAIL);
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

    private int run(MinecraftClient mc, String target, float forwards, float upwards, float right, float rot_down, float rot_right) {
        String[] targetNames = {"Selected Object", "Default", "Damager", "Killer", "Victim", "Shooter", "Projectile"};
        String finalTarget = targetNames[Arrays.asList(targetTypes).indexOf(target)];

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

        LiteralText itemName = new LiteralText("Relative Location");
        itemName.setStyle(Style.EMPTY.withColor(Formatting.GREEN).withItalic(false));
        display.put("Name", StringTag.of(Text.Serializer.toJson(itemName)));

        LiteralText lore1 = new LiteralText("Target: ");
        LiteralText lore2 = new LiteralText(finalTarget);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(0, StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        lore1 = new LiteralText("Forwards: ");
        lore2 = new LiteralText("" + forwards);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        lore1 = new LiteralText("Upwards: ");
        lore2 = new LiteralText("" + upwards);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        lore1 = new LiteralText("Right: ");
        lore2 = new LiteralText("" + right);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        lore1 = new LiteralText("Rot Down: ");
        lore2 = new LiteralText("" + rot_down);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        lore1 = new LiteralText("Rot Right: ");
        lore2 = new LiteralText("" + rot_right);
        lore1.setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false));
        lore2.setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(false));
        lore.addTag(lore.size(), StringTag.of(Text.Serializer.toJson(lore1.append(lore2))));

        display.put("Lore", lore);
        item.getTag().put("display", display);

        ItemUtil.giveCreativeItem(item, true);
        return 1;
    }
}
