package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterText extends CodeParameter {
    private String value;

    public String getValue() {
        return value;
    }

    public CodeParameterText(int slot, String value) {
        super(slot);
        this.value = value;
        this.type = Mapping.CodeParameterNames.TEXT;
    }

    @Override
    public String toString() {
        return "CodeParameterText{" +
                "value='" + value + '\'' +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
