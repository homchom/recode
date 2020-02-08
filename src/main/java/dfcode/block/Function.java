package dfcode.block;

public class Function extends Block
{

	private String name;
	
	public Function(String name) 
	{
		super(null);
		
		this.name = name;
	}

	public String getName() 
	{
		return name;
	}
	
	@Override
	public void run() 
	{
		
	}
}
