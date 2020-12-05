package io.github.codeutilities.schem.utils;

public class DFNumber {

    public double number;

    public DFNumber(double number) {
        this.number = number;
    }

    public String asJson() {return "{\"id\":\"num\",\"data\":{\"name\":\"" + number + "\"}}";}
}
