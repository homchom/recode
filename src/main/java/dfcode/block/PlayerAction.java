package dfcode.block;

import dfcode.parameter.Parameter;

import java.util.ArrayList;

public class PlayerAction extends Block
{
	private String selector;
	private String action;
	private ArrayList<Parameter> params;
	
	public PlayerAction(String selector, String action, ArrayList<Parameter> params) 
	{
		super(null);
		
		this.selector = selector;
		this.action = action;
		this.params = params;
	}

	public String getSelect() 
	{
		return selector;
	}
	
	public String getAction() 
	{
		return action;
	}
	
	public ArrayList<Parameter> getParams()
	{
		return params;
	}
	
	@Override
	public void run() 
	{
		
	}

}
