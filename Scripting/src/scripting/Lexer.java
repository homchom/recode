package scripting;

import java.util.ArrayList;
import java.util.List;

// used stackoverflow bcz i suck
public class Lexer {
    public static String getAtom(String s, int i) {
        int j = i;
        for( ; j < s.length(); ) {
            if(isToken(s, j)) {
                j++;
            } else {
                return s.substring(i, j);
            }
        }
        return s.substring(i, j);
    }

    public static boolean isToken(String s, int character) {
        Character charBefore = null;
        Character dChar = s.charAt(character);
        if(character != 0) {
            charBefore = s.charAt(character - 1);
        }

        if(charBefore != null) {
            if(charBefore == '\\') {
                return true;
            }
        }

        return TokenType.getType(dChar.toString()) == null;

    }

    public static List<Token> lex(String code) {
        List<Token> toReturn = new ArrayList<>();
        for(int i = 0; i < code.length();) {
            Character character = code.charAt(i);
            TokenType type = TokenType.getType(character.toString());

            if(type == null) {
                if(Character.isWhitespace(code.charAt(i))) {
                    i++;
                } else {
                    String atom = getAtom(code, i);
                    i += atom.length();
                    toReturn.add(new Token(TokenType.TOKEN, atom));
                }
            }else {
                toReturn.add(new Token(type, character.toString()));
                i++;
            }
        }
        return toReturn;
    }
}
