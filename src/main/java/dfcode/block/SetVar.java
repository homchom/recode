package dfcode.block;

import dfcode.parameter.Parameter;

import java.util.ArrayList;

public class SetVar extends Block
{
	
	private String name;
	private ArrayList<Parameter> params;
	
	public SetVar(String name, ArrayList<Parameter> params) 
	{
		super(null);

		this.name = name;
		this.params = params;
	}

	public String getName() 
	{
		return name;
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
