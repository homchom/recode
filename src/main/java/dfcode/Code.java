package dfcode;

import com.mojang.realmsclient.gui.ChatFormatting;
import dfcode.block.*;
import dfcode.parameter.Parameter;
import dfcode.parser.*;
import dfcode.tokenizer.Tokenizer;
import me.reasonless.codeutilities.util.GzFormat;
import me.reasonless.codeutilities.util.TemplateNBT;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import java.nio.charset.StandardCharsets;

public class Code 
{
	private String jsoncode = "{\"blocks\":[";
	private ItemStack item = new ItemStack(Items.ENDER_CHEST).setCustomName(new LiteralText(ChatFormatting.AQUA +"Code Template 1"));
	private int num = 1;
	private boolean firstcode = true;
	
	public Code(String code) 
	{	
		Parser<?>[] parsers = new Parser<?>[] {new PlayerEventParser(), new FunctionParser()
			, new PlayerActionParser(), new CallFunctionParser(), new SetVarParser()};
		
		boolean success = false;
		
		Block block = null;
		
		for(String line : code.trim().split("\n")) 
		{
			success = false;
			line = line.trim();
			Tokenizer tokenizer = new Tokenizer(line);
			
			for (Parser<?> parser : parsers) {
				if (parser.shouldParse(line)) {
					Block newBlock = parser.parse(block, tokenizer);
					
					if(!firstcode) 
					{
						jsoncode += ",";
					}
					if(newBlock instanceof PlayerEvent) 
					{
						if(!firstcode)giveCode();
						jsoncode +="{\"id\":\"block\",\"block\":\"event\",\"action\":\""+((PlayerEvent) newBlock).getName()+"\"}";
					}
					else if(newBlock instanceof Function)
					{
						if(!firstcode)giveCode();
						jsoncode +="{\"id\":\"block\",\"block\":\"func\",\"data\":\""+((Function) newBlock).getName()+"\",\"args\":{\"items\":[{\"item\":{\"id\":\"bl_tag\",\"data\":{\"option\":\"False\",\"tag\":\"Is Hidden\",\"action\":\"dynamic\",\"block\":\"func\"}},\"slot\":26}]}}";
					}
					else if(newBlock instanceof CallFunction) 
					{
						jsoncode +="{\"id\":\"block\",\"block\":\"call_func\",\"data\":\""+((CallFunction) newBlock).getName()+"\"}";
					}
					else if(newBlock instanceof PlayerAction)
					{
						String par = "[";
						int i = 0;
						for(Parameter param : ((PlayerAction) newBlock).getParams()) 
						{
							if(i==0) 
							{
								par += param.getName();
							}
							else 
							{
								par += ","+param.getName();
							}
							i++;
						}
						par += "]";
						jsoncode +="{\"id\":\"block\",\"block\":\"player_action\",\"action\":\""+((PlayerAction) newBlock).getAction()+
						"\",\"args\":{\"items\":"+par+"},\"target\":\""+((PlayerAction) newBlock).getSelect()+"\"}";
					}
					else if(newBlock instanceof SetVar)
					{
						String par = "[";
						int i = 0;
						for(Parameter param : ((SetVar) newBlock).getParams()) 
						{
							if(i==0) 
							{
								par += param.getName();
							}
							else 
							{
								par += ","+param.getName();
							}
							i++;
						}
						par += "]";
						jsoncode +="{\"id\":\"block\",\"block\":\"set_var\",\"action\":\""+((SetVar) newBlock).getName()+
						"\",\"args\":{\"items\":"+par+"}}";
					}
					firstcode = false;
					block = newBlock;
					success = true;
					break;
				}
			}
			
			if (!success) 
			{
				MinecraftClient.getInstance().player.sendMessage(new LiteralText(ChatFormatting.RED + "Failed to parse code"));
			}
		}
		giveCode();
	}
	
	public void giveCode()
	{
		if(num==1&&jsoncode.endsWith(",")) 
		{
			jsoncode = jsoncode.substring(0, jsoncode.length()-1);
		}
		jsoncode += "]}";
		firstcode = true;
		String encoded = null;
		try {
			encoded = new String(GzFormat.encryptBase64(GzFormat.compress(jsoncode.getBytes(StandardCharsets.UTF_8))));
		} catch (Exception e) {
			e.printStackTrace();
		}

		TemplateNBT.setTemplateNBT(item, "Code", MinecraftClient.getInstance().player.getName().getString(), encoded);

		MinecraftClient.getInstance().interactionManager.clickCreativeStack(item, 36 + MinecraftClient.getInstance().player.inventory.selectedSlot);
		
		jsoncode = "{\"blocks\":[";
		num++;
		item = new ItemStack(Items.ENDER_CHEST).setCustomName(new LiteralText(ChatFormatting.AQUA+"Code Template "+num));
	}
}
