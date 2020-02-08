package dfcode.block;

public class PlayerEvent extends Block
{
	private String name;
	
	public PlayerEvent(String name) 
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
