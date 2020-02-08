package scripting;

public class Main {
    public static void main(String[] args) {
        for(Token token:Lexer.lex("event(Join) {\n" +
                "\tplayer.sendMessage(\"hi\");\n" +
                "}")) {
            System.out.println(token.getTokenType() + "; " + token.getData());
        }
    }
}
