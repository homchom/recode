package dfcode.parameter;

public class StringType extends Type
{

	private String name;

	@SuppressWarnings("all")
	public StringType(String name) 
	{
		super(BuiltInType.STRING);
		
		String formname = "";
		String prevs = "";
		for(String s : name.split("")) 
		{
			if(prevs.equals("&"))
			{
				formname += "�";
			}
			else if(!s.equals("&")&&!prevs.equals("&"))
			{
				formname += s;
			}
			prevs = s;
		}
		
		this.name = formname;
	}
	
	public String getName() 
	{
		return name;
	}
}
