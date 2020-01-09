package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterPotion extends CodeParameter{
    private String potionType;
    private int duration;
    private int amplifier;

    public String getPotionType() {
        return potionType;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public CodeParameterPotion(int slot, String potionType, int duration, int amplifier) {
        super(slot);
        this.potionType = potionType;
        this.duration = duration;
        this.amplifier = amplifier;
        this.type = Mapping.CodeParameterNames.POTION;
    }

    @Override
    public String toString() {
        return "CodeParameterPotion{" +
                "potionType='" + potionType + '\'' +
                ", duration=" + duration +
                ", amplifier=" + amplifier +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
