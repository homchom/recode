package dfcode.parameter;

public class VarType extends Type 
{
	private String name;

	public VarType(String name) 
	{
		super(BuiltInType.VAR);
		
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}
}
