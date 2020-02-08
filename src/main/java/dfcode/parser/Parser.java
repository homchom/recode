package dfcode.parser;

import dfcode.block.Block;
import dfcode.tokenizer.Tokenizer;

public abstract class Parser<T extends Block> 
{
	public abstract boolean shouldParse(String line);
		
	public abstract T parse(Block superBlock, Tokenizer tokenizer);
}

