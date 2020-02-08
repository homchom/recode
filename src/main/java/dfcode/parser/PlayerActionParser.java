package dfcode.parser;

import dfcode.block.Block;
import dfcode.block.PlayerAction;
import dfcode.parameter.*;
import dfcode.tokenizer.Token;
import dfcode.tokenizer.TokenType;
import dfcode.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerActionParser extends Parser<PlayerAction>
{

	@Override
	public boolean shouldParse(String line) 
	{
		return line.matches("player\\.[a-zA-Z]+\\(( *[a-zA-Z]*+ *)(, *.* *)*\\)");
	}

	@Override
	public PlayerAction parse(Block superBlock, Tokenizer tokenizer) 
	{
		tokenizer.nextToken();
		tokenizer.nextToken();
		
		String action = tokenizer.nextToken().getToken();
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		
		tokenizer.nextToken();
		
		String sel = tokenizer.nextToken().getToken();
		if(sel.toLowerCase().equals("none")) 
		{
			sel = "";
		}
		
		tokenizer.nextToken();
		
		int i = 0;
		while(tokenizer.hasNextToken()) 
		{
			Token token = tokenizer.nextToken();
			if(token.getType()==TokenType.STRING_LITERAL) 
			{
				params.add(new Parameter(new StringType(token.getToken()), i));
			}
			else if(token.getType()==TokenType.NUMBER_LITERAL) 
			{
				params.add(new Parameter(new NumberType(token.getToken()), i));
			}
			else if(token.getType()==TokenType.IDENTIFIER) 
			{
				String str = token.getToken().trim()+tokenizer.getStr().trim();
				//Item
				Pattern item = Pattern.compile("^(Item\\((?: *([A-Za-z_]+) *)(?:, *\"([ &a-zA-Z0-9*]+)\" *)?(?:, *(\\d+) *)?\\))");
				Matcher mitem = item.matcher(str);
				if(mitem.find()) 
				{
					tokenizer.nextToken();
					tokenizer.nextToken();
					tokenizer.nextToken();
					
					String id = mitem.group(2).toLowerCase();
					String name = "";
					String count = "1";
					
					System.out.println(mitem.groupCount());
					
					try
					{
						name = mitem.group(3);
						if(!(name == null))
						{
							tokenizer.nextToken();
							tokenizer.nextToken();
						}
						else {name = "";}
					}
					catch(Exception e) {name = "";}
					
					try 
					{
						count = mitem.group(4);
						if(!(count == null))
						{
							tokenizer.nextToken();
							tokenizer.nextToken();
						}
						else {count = "1";}
					}
					catch(Exception e) {count = "1";}
					
					params.add(new Parameter(new ItemType(id, name, count), i));
				}
				//Variable
				Pattern var = Pattern.compile("^(Var\\( *([^)]+ *)\\))");
				Matcher mvar = var.matcher(str);
				if(mvar.find()) 
				{
					tokenizer.nextToken();
					tokenizer.nextToken();
					tokenizer.nextToken();
					tokenizer.nextToken();
					
					String name = mvar.group().substring(4, mvar.end()-1);
					
					params.add(new Parameter(new VarType(name), i));
				}
				//Location
				Pattern loc = Pattern.compile("^(Loc\\( *(\\d+(?:\\.\\d*)?) *, *(\\d+(?:\\.\\d*)?) *, *(\\d+(?:\\.\\d*)?) *(, *(\\d+(?:\\.\\d*)?) *, *(\\d+(?:\\.\\d*)?) *)?\\))");
				Matcher mloc = loc.matcher(str);
				if(mloc.find()) 
				{
					tokenizer.nextToken();
					tokenizer.nextToken();
					tokenizer.nextToken();
					
					String x = mloc.group(2);
					String y = mloc.group(3);
					String z = mloc.group(4);
					String pitch = mloc.group(6);
					String yaw = mloc.group(7);
					
					if(yaw==null) 
					{
						yaw = "0";
						pitch = "0";
					}
					else 
					{
						tokenizer.nextToken();
						tokenizer.nextToken();
					}
					
					Tokenizer xtok = new Tokenizer(x);
					Tokenizer ytok = new Tokenizer(y);
					Tokenizer ztok = new Tokenizer(z);
					Tokenizer yawtok = new Tokenizer(yaw);
					Tokenizer pitchtok = new Tokenizer(pitch);
					
					while(xtok.hasNextToken()) 
					{
						xtok.nextToken();
						tokenizer.nextToken();
					}
					while(ytok.hasNextToken()) 
					{
						ytok.nextToken();
						tokenizer.nextToken();
					}
					while(ztok.hasNextToken()) 
					{
						ztok.nextToken();
						tokenizer.nextToken();
					}
					while(yawtok.hasNextToken()) 
					{
						yawtok.nextToken();
						tokenizer.nextToken();
					}
					while(pitchtok.hasNextToken()) 
					{
						pitchtok.nextToken();
						tokenizer.nextToken();
					}
					
					params.add(new Parameter(new LocType(x, y, z, pitch, yaw), i));
				}
			}
			i++;
		}
		return new PlayerAction(sel, action, params);
	}

}
