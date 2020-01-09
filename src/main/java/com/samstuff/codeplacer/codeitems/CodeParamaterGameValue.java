package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParamaterGameValue extends CodeParameter{
    private String valueType;
    private String selection;

    public String getValueType() {
        return valueType;
    }

    public String getSelection() {
        return selection;
    }

    public CodeParamaterGameValue(int slot, String valueType, String selection) {
        super(slot);
        this.valueType = valueType;
        this.selection = selection;
        this.type = Mapping.CodeParameterNames.GAME_VALUE;
    }

    @Override
    public String toString() {
        return "CodeParamaterGameValue{" +
                "valueType='" + valueType + '\'' +
                ", selection='" + selection + '\'' +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
