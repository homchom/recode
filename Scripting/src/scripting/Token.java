package scripting;

public class Token {
    TokenType tokenType;
    String data;

    public Token(TokenType tokenType, String data) {
        this.tokenType = tokenType;
        this.data = data;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getData() {
        return data;
    }
}
