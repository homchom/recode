package dfcode.parser;

import dfcode.block.Block;
import dfcode.block.CallFunction;
import dfcode.tokenizer.Tokenizer;

public class CallFunctionParser extends Parser<CallFunction>
{

	@Override
	public boolean shouldParse(String line) 
	{
		return line.matches("callFunc\\([^ \\\"]*\\)");
	}

	@Override
	public CallFunction parse(Block superBlock, Tokenizer tokenizer) 
	{
		tokenizer.nextToken();
		tokenizer.nextToken();
		
		String name = tokenizer.nextToken().getToken();
		
		return new CallFunction(name);
	}

}
