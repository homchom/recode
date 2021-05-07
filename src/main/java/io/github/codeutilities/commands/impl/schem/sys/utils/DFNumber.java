package io.github.codeutilities.commands.impl.schem.sys.utils;

public class DFNumber {

    private final long number;

    public DFNumber(double number) {
        this.number = Math.round(number);
    }

    public String asJson() {
        return "{\"id\":\"num\",\"data\":{\"name\":\"" + number + "\"}}";
    }
}