package io.github.homchom.recode.sys.util;

import com.google.gson.JsonParser;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.homchom.recode.Recode;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;

import java.nio.charset.*;
import java.util.*;

public class ItemUtil {
    public static void giveCreativeItem(ItemStack item, boolean preferHand) {
        Minecraft mc = Recode.MC;
        NonNullList<ItemStack> inv = mc.player.getInventory().items;

        if (preferHand) {
            if (mc.player.getMainHandItem().isEmpty()) {
                mc.gameMode.handleCreativeModeItemAdd(item, mc.player.getInventory().selected + 36);
                return;
            }
        }

        for (int index = 0; index < inv.size(); index++) {
            ItemStack i = inv.get(index);
            ItemStack compareItem = i.copy();
            compareItem.setCount(item.getCount());
            if (item == compareItem) {
                while (i.getCount() < i.getMaxStackSize() && item.getCount() > 0) {
                    i.setCount(i.getCount() + 1);
                    item.setCount(item.getCount() - 1);
                }
            } else {
                if (i.getItem() == Items.AIR) {
                    if (index < 9)
                        mc.gameMode.handleCreativeModeItemAdd(item, index + 36);
                    inv.set(index, item);
                    return;
                }
            }
        }
    }

    /**
     * Sets the item at a container slot. (Only works in creative)
     *
     * @param slot      The slot you want to change.
     * @param itemStack The item stack to replace it with
     */
    public static void setContainerItem(int slot, ItemStack itemStack) {
        Minecraft mc = Recode.MC;

        // this method kinda doesnt work in survival mode so let's throw an exception if this happens.
        if (!mc.player.isCreative()) {
            throw new IllegalStateException("Player is not in creative mode.");
        }

        // replace the 8th slot with the item we want to set.
        ItemStack replacedItem = mc.player.getInventory().getItem(7);
        Recode.MC.gameMode.handleCreativeModeItemAdd(itemStack, 43);
        mc.player.getInventory().setItem(7, itemStack);

        // simulates pressing the 8 key on the slot we want to change.
        Recode.MC.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, slot, 7, ClickType.SWAP, Recode.MC.player);

        // change the 8th slot back to what it was before.
        Recode.MC.gameMode.handleCreativeModeItemAdd(replacedItem, 43);
        mc.player.getInventory().setItem(7, replacedItem);
    }

    public static List<ItemStack> fromItemContainer(ItemStack container) {
        ListTag nbt = container.getOrCreateTag().getCompound("BlockEntityTag").getList("Items", 10);
        return fromListTag(nbt);
    }

    public static ItemStack fromID(String id) {
        return new ItemStack(Registry.ITEM.get(new ResourceLocation(id.toLowerCase())));
    }

    public static void setLore(ItemStack itemStack, Component[] lores){
        ListTag loreTag = new ListTag();
        for(Component lore : lores) {
            if (lore == null){
                itemStack.getTagElement("display").put("Lore", loreTag);
                return;
            }
            loreTag.add(StringTag.valueOf("{\"extra\":[{\"bold\":" + lore.getStyle().isBold() + ",\"italic\":" + lore.getStyle().isItalic() + ",\"underlined\":" + lore.getStyle().isUnderlined() + ",\"strikethrough\":" + lore.getStyle().isStrikethrough() + ",\"obfuscated\":" + lore.getStyle().isObfuscated() + ",\"color\":\"" + lore.getStyle().getColor() + "\",\"text\":\"" + lore.getString() + "\"}],\"text\":\"\"}"));
        }
        itemStack.getTagElement("display").put("Lore", loreTag);
    }

    public static ItemStack setLore(ItemStack itemStack, String[] lores){
        ListTag loreTag = new ListTag();
        for(String lore : lores) {
            if (lore == null){
                itemStack.getTagElement("display").put("Lore", loreTag);
                return itemStack;
            }
            loreTag.add(StringTag.valueOf(lore));
        }
        itemStack.getTagElement("display").put("Lore", loreTag);
        return itemStack;
    }

    public static ItemStack addLore(ItemStack itemStack, String[] lores){
        ListTag loreTag = new ListTag();
        if (itemStack.getOrCreateTagElement("display").contains("Lore")){
            loreTag = itemStack.getTagElement("display").getList("Lore", 8);
        }
        for(String lore : lores) {
            if (lore == null){
                break;
            }
            loreTag.add(StringTag.valueOf(lore));
        }
        itemStack.getTagElement("display").put("Lore", loreTag);
        return itemStack;
    }

    public static void givePlayerHead(String texture) throws CommandSyntaxException {
        ItemStack item = new ItemStack(Items.PLAYER_HEAD);

        if (texture.contains(".minecraft.net")) {
            Charset charset = StandardCharsets.UTF_8;
            String rawString = "{\"textures\":{\"SKIN\":{\"url\":\"" + texture + "\"}}}";

            byte[] a = Base64.getEncoder().encode(rawString.getBytes(charset));
            texture = new String(a, charset);
        }

        CompoundTag nbt = TagParser.parseTag("{SkullOwner:{Id:" + StringUtil.genDummyIntArray() + ",Properties:{textures:[{Value:\"" + texture + "\"}]}}}");
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
                return JsonParser.parseString(publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject().get("id").getAsString().equalsIgnoreCase(type);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static ListTag toListTag(List<ItemStack> stacks) {
        ListTag listTag = new ListTag();
        for (ItemStack stack : stacks) {
            listTag.add(stack.save(new CompoundTag()));
        }

        return listTag;
    }

    public static List<ItemStack> fromListTag(ListTag listTag) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Tag tag : listTag) {
            stacks.add(ItemStack.of((CompoundTag) tag));
        }
        return stacks;
    }
}
