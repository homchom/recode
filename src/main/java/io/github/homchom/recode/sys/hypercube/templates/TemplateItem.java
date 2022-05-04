package io.github.homchom.recode.sys.hypercube.templates;

import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class TemplateItem {

    final String code;
    final ItemStack stack;

    public TemplateItem(ItemStack stack) {
        this.code = TemplateUtils.fromItemStack(stack).get("code").getAsString();
        this.stack = stack;
    }

    public TemplateItem(String code, ItemStack stack) {
        this.code = code;
        this.stack = stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateItem that = (TemplateItem) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public ItemStack getStack() {
        return stack;
    }
}
