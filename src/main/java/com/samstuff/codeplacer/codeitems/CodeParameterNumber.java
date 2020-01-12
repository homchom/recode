package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterNumber extends CodeParameter {
    private double value;

    public double getValue() {
        return value;
    }

    public CodeParameterNumber(int slot, double value) {
        super(slot);
        this.value = value;
        this.type = Mapping.CodeParameterNames.NUMBER;
    }

    @Override
    public String toString() {
        return "CodeParameterNumber{" +
                "value=" + value +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
