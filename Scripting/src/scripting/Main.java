package scripting;

public class Main {
    public static void main(String[] args) {
        for(Token token:Lexer.lex("{\"hi\"}")) {
            System.out.println(token.getTokenType() + "; " + token.getData());
        }
    }
}
