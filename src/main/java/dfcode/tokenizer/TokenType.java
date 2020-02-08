package dfcode.tokenizer;

public enum TokenType 
{
	EMPTY,
	
	/** A Token ( ) = , */
	TOKEN,
	
	/** First Character is a letter, any proceeding characters are letters or number */
	IDENTIFIER,
	
	/** A decimal-number*/
	NUMBER_LITERAL,
	
	/** Anything enclosed in double quotes. "Hello" "1"*/
	STRING_LITERAL
}
