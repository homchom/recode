package io.github.homchom.recode.sys.hypercube.templates;

import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.sys.player.chat.ChatType;
import io.github.homchom.recode.sys.player.chat.ChatUtil;
import io.github.homchom.recode.sys.util.ItemUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

public class Templates {

    public static void giveInternalTemplate(Item item, String name, String author, String codeData) {
        ItemStack stack = new ItemStack(item)
                .setHoverName(
                        Component.literal("")
                                .append(Component.translatable("recode.title").setStyle(Style.EMPTY.withColor(0xaa7fff).withBold(true)))
                                .append(Component.literal(" » ").setStyle(Style.EMPTY.withColor(0x7f2ad4)))
                                .append(Component.literal(name).setStyle(Style.EMPTY.withColor(0xaa7fff)))
                );
        stack.enchant(Enchantments.FISHING_SPEED, 1);
        stack.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS);

        giveInternalTemplate(stack, name, author, codeData);
    }

    public static void giveInternalTemplate(ItemStack stack, String name, String author, String codeData) {
        giveInternalTemplate(stack, name, author, codeData, TemplateUtil.VERSION);
    }

    public static void giveInternalTemplate(ItemStack stack, String name, String author, String codeData, int version) {
        giveTemplate(stack, name, author, codeData, version);
        ChatUtil.sendMessage("You've received §6" + name + "§b! Place it down in your codespace and open the chest to get functions!", ChatType.INFO_BLUE);
    }

    public static void giveUserTemplate(Item item, String name, String codeData) {
        ItemStack stack = new ItemStack(item)
                .setHoverName(
                        Component.literal("")
                                .append(Component.translatable("recode.title").setStyle(Style.EMPTY.withColor(0xaa7fff).withBold(true)))
                                .append(Component.literal(" » ").setStyle(Style.EMPTY.withColor(0x7f2ad4)))
                                .append(Component.literal(name).setStyle(Style.EMPTY.withColor(0xaa7fff)))
                );
        stack.enchant(Enchantments.FISHING_SPEED, 1);
        stack.hideTooltipPart(ItemStack.TooltipPart.ENCHANTMENTS);

        giveUserTemplate(stack, name, codeData);
    }

    public static void giveUserTemplate(ItemStack stack, String name, String codeData) {
        giveRawTemplate(stack, name, LegacyRecode.MC.player.getName().getString(), codeData);
    }

    public static void giveRawTemplate(ItemStack stack, String name, String author, String codeData) {
        TemplateUtil.compressTemplateNBT(stack, name, author, codeData);
        ItemUtil.giveCreativeItem(stack, true);
    }

    public static void giveTemplate(ItemStack stack, String name, String author, String codeData) {
        giveTemplate(stack, name, author, codeData, TemplateUtil.VERSION);
    }

    public static void giveTemplate(ItemStack stack, String name, String author, String codeData, int version) {
        TemplateUtil.applyRawTemplateNBT(stack, Component.literal(name), author, codeData, version);
        ItemUtil.giveCreativeItem(stack, true);
    }
}
