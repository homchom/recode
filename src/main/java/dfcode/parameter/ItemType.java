package dfcode.parameter;

public class ItemType extends Type
{

	private String id;
	private String name;
	private String count;
	
	public ItemType(String id, String name, String count) 
	{
		super(BuiltInType.ITEM);
		
		this.id = id;
		this.name = new StringType(name).getName();
		this.count = count;
	}
	
	public String getId() 
	{
		return id;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getCount() 
	{
		return count;
	}
}
