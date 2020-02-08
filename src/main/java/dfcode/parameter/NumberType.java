package dfcode.parameter;

public class NumberType extends Type
{
	private String num;
	
	public NumberType(String num) 
	{
		super(BuiltInType.NUMBER);
		
		this.num = num;
	}

	public String getNum() 
	{
		return num;
	}
	
}
