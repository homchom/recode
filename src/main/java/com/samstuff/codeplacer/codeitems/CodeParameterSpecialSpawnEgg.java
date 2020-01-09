package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterSpecialSpawnEgg extends CodeParameter {
    private String eggType;

    public String getEggType() {
        return eggType;
    }

    public CodeParameterSpecialSpawnEgg(int slot, String eggType) {
        super(slot);
        this.eggType = eggType;
        this.type = Mapping.CodeParameterNames.SPECIAL_SPAWN_EGG;
    }

    @Override
    public String toString() {
        return "CodeParameterSpecialSpawnEgg{" +
                "eggType='" + eggType + '\'' +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
