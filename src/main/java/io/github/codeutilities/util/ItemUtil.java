package io.github.codeutilities.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.codeutilities.CodeUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.util.collection.DefaultedList;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ItemUtil {

    public static void giveCreativeItem(ItemStack item, boolean preferHand) {

        MinecraftClient mc = MinecraftClient.getInstance();

        DefaultedList<ItemStack> mainInventory = mc.player.inventory.main;

        if (preferHand) {
            if (mc.player.getMainHandStack().isEmpty()) {
                mc.interactionManager.clickCreativeStack(item, mc.player.inventory.selectedSlot + 36);
                return;
            }
        }

        for (int index = 0; index < mainInventory.size(); index++) {
            ItemStack i = mainInventory.get(index);
            ItemStack compareItem = i.copy();
            compareItem.setCount(item.getCount());
            if (item == compareItem) {
                while (i.getCount() < i.getMaxCount() && item.getCount() > 0) {
                    i.setCount(i.getCount() + 1);
                    item.setCount(item.getCount() - 1);
                }
            } else {
                if (i.getItem() == Items.AIR) {
                    if (index < 9)
                        MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, index + 36);
                    mainInventory.set(index, item);
                    return;
                }
            }
        }
    }

    public static List<ItemStack> fromItemContainer(ItemStack container) {
        ListTag nbt = container.getOrCreateTag().getCompound("BlockEntityTag").getList("Items", 10);
        return fromListTag(nbt);
    }

    public static void givePlayerHead(String texture) throws CommandSyntaxException {
        ItemStack item = new ItemStack(Items.PLAYER_HEAD);

        if (texture.contains(".minecraft.net")) {
            Charset charset = StandardCharsets.UTF_8;
            String rawString = "{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}";

            byte[] a = Base64.getEncoder().encode(rawString.getBytes(charset));
            texture = new String(a, charset);
        }

        CompoundTag nbt = StringNbtReader.parse("{SkullOwner:{Id:" + StringUtil.genDummyIntArray() + ",Properties:{textures:[{Value:\"" + texture + "\"}]}}}");
        item.setTag(nbt);
        ItemUtil.giveCreativeItem(item, true);
    }

    public static boolean isVar(ItemStack stack, String type) {
        try {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                return false;
            }

            CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
            if (publicBukkitNBT == null) {
                return false;
            }

            if (publicBukkitNBT.getString("hypercube:varitem").length() > 0) {
                return CodeUtilities.JSON_PARSER.parse(publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject().get("id").getAsString().equalsIgnoreCase(type);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static ListTag toListTag(List<ItemStack> stacks) {
        ListTag listTag = new ListTag();
        for (ItemStack stack : stacks) {
            listTag.add(stack.toTag(new CompoundTag()));
        }

        return listTag;
    }

    public static List<ItemStack> fromListTag(ListTag listTag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Tag tag : listTag) {
            stacks.add(ItemStack.fromTag((CompoundTag) tag));
        }
        return stacks;
    }
}
