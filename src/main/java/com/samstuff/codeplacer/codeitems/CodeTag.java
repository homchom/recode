package com.samstuff.codeplacer.codeitems;

public class CodeTag {
    private String name;
    private String value;
    private int slot;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "CodeTag{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", slot=" + slot +
                '}';
    }

    public CodeTag(String name, String value, int slot) {
        this.name = name;
        this.value = value;
        this.slot = slot;
    }
}
