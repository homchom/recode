package me.reasonless.codeutilities.templates.variables;

public class TextVariable implements Variable {
    private String value;

    public TextVariable(String value) {
        this.value = value;
    }

    @Override
    public String toJson(int slot) {
        return "{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\"" + value + "\"}},\"slot\":" + slot + "}";
    }

}
