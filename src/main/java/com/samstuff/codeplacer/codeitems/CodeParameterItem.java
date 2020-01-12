package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

import java.util.List;

public class CodeParameterItem extends CodeParameter {
    private String material;
    private int count;
    private String customName;
    private List<String> lore;
    private List<CodeParameterItemEnchantment> enchantments;
    private int durability;
    private boolean unbreakable = false;
    private boolean hideFlags = false;
    private String headOwner;

    private boolean hasCustomName = false;
    private boolean hasLore = false;
    private boolean hasEnchantments = false;
    private boolean hasDurability = false;
    private boolean isHead = false;

    public String getMaterial() {
        return material;
    }

    public int getCount() {
        return count;
    }

    public String getCustomName() {
        return customName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<CodeParameterItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public int getDurability() {
        return durability;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public boolean isHideFlags() {
        return hideFlags;
    }

    public String getHeadOwner() {
        return headOwner;
    }

    public boolean getHasCustomName() {
        return hasCustomName;
    }

    public boolean getHasLore() {
        return hasLore;
    }

    public boolean getHasEnchantments() {
        return hasEnchantments;
    }

    public boolean getHasDurability() {
        return hasDurability;
    }

    public boolean getHead() {
        return isHead;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
        hasCustomName = true;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
        hasLore = true;
    }

    public void setEnchantments(List<CodeParameterItemEnchantment> enchantments) {
        this.enchantments = enchantments;
        hasEnchantments = true;
    }

    public void setDurability(int durability) {
        this.durability = durability;
        hasDurability = true;
    }

    public void setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public void setHideFlags(boolean hideFlags) {
        this.hideFlags = hideFlags;
    }

    public void setHeadOwner(String headOwner) {
        this.headOwner = headOwner;
        isHead = true;
    }

    public CodeParameterItem(int slot, String material, int count) {
        super(slot);
        this.material = material;
        this.count = count;
        this.type = Mapping.CodeParameterNames.ITEM;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "CodeParameterItem{" +
                        "material='" + material + '\'' +
                        ", count=" + count +
                        ", slot=" + slot +
                        ", type='" + type + '\''
        );
        if (hasCustomName) sb.append(", customName='" + customName + '\'');
        if (hasLore) sb.append(", lore=" + lore);
        if (hasEnchantments) sb.append(", enchantments=" + enchantments);
        if (hasDurability) sb.append(", durability=" + durability);
        if (unbreakable) sb.append(", unbreakable=" + unbreakable);
        if (hideFlags) sb.append(", hideFlags=" + hideFlags);
        if (isHead) sb.append(", headOwner='" + headOwner + '\'');

        sb.append("}");
        return sb.toString();
    }
}
