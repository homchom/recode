package scripting;

public enum TokenType {
    BRACKET_LEFT("{"),
    BRACKET_RIGHT("}"),
    STRING("\""),
    TOKEN("");

    String regex;

    TokenType(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public static TokenType getType(String s) {
        for(TokenType tokenType:values()) {
            if(tokenType.getRegex().equals(s)) {
                return tokenType;
            }
        }
        return null;
    }
}