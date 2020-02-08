package dfcode.parameter;

public abstract class Type 
{
	private BuiltInType type;
	
	public Type(BuiltInType type)
	{
		this.type = type;
	}
	
	public BuiltInType getType() 
	{
		return type;
	}
}
