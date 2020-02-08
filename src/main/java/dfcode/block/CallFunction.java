package dfcode.block;

public class CallFunction extends Block
{
	private String name;
	
	public CallFunction(String name) 
	{
		super(null);
		
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}
	
	@Override
	public void run() {}

}
