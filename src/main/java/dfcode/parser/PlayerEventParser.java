package dfcode.parser;

import dfcode.block.Block;
import dfcode.block.PlayerEvent;
import dfcode.tokenizer.Tokenizer;

public class PlayerEventParser extends Parser<PlayerEvent>
{

	@Override
	public boolean shouldParse(String line) 
	{
		return line.matches("playerEvent [a-zA-Z]+");
	}

	@Override
	public PlayerEvent parse(Block superBlock, Tokenizer tokenizer) 
	{
		tokenizer.nextToken();
		
		String name = tokenizer.nextToken().getToken();
		
		return new PlayerEvent(name);
	}

}
