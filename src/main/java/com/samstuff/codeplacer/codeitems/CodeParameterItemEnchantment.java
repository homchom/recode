package com.samstuff.codeplacer.codeitems;

public class CodeParameterItemEnchantment {
    private String name;
    private int level;

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public CodeParameterItemEnchantment(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public String toString() {
        return "CodeParameterItemEnchantment{" +
                "name='" + name + '\'' +
                ", level=" + level +
                '}';
    }
}
