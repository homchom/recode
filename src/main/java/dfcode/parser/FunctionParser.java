package dfcode.parser;

import dfcode.block.Block;
import dfcode.block.Function;
import dfcode.tokenizer.Tokenizer;

public class FunctionParser extends Parser<Function>
{

	@Override
	public boolean shouldParse(String line) 
	{
		return line.matches("func [a-zA-Z]+");
	}

	@Override
	public Function parse(Block superBlock, Tokenizer tokenizer) 
	{
		tokenizer.nextToken();
		
		String name = tokenizer.nextToken().getToken();
		
		return new Function(name);
	}

}
