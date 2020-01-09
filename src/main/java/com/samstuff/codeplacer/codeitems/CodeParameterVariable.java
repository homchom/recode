package com.samstuff.codeplacer.codeitems;

import com.samstuff.codeplacer.Mapping;

public class CodeParameterVariable extends CodeParameter {
    private String name;
    private String variableType;

    public String getName() {
        return name;
    }

    public String getVariableType() {
        return variableType;
    }

    public CodeParameterVariable(int slot, String name, String variableType) {
        super(slot);
        this.name = name;
        this.variableType = variableType;
        this.type = Mapping.CodeParameterNames.VARIABLE;
    }

    @Override
    public String toString() {
        return "CodeParameterVariable{" +
                "name='" + name + '\'' +
                ", variableType='" + variableType + '\'' +
                ", slot=" + slot +
                ", type='" + type + '\'' +
                '}';
    }
}
