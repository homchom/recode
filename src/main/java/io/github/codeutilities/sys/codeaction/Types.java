package io.github.codeutilities.sys.codeaction;

public class Types {
    private final String id;
    private final String color;

    Types(String id, String color){
        this.id = id;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return id;
    }
}
