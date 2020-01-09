package com.samstuff.codeplacer.codeitems;

public abstract class CodeParameter {
    protected int slot;
    protected String type;

    public int getSlot() {
        return slot;
    }

    public String getType() {
        return type;
    }

    public CodeParameter(int slot) {
        this.slot = slot;
    }
}
