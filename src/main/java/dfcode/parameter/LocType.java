package dfcode.parameter;

public class LocType extends Type 
{

	private String x;
	private String y;
	private String z;
	private String pitch;
	private String yaw;
	
	public LocType(String x, String y, String z, String pitch, String yaw) 
	{
		super(BuiltInType.LOC);
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public String getX() 
	{
		return x;
	}
	
	public String getY() 
	{
		return y;
	}
	
	public String getZ() 
	{
		return z;
	}

	public String getPitch() 
	{
		return pitch;
	}
	
	public String getYaw() 
	{
		return yaw;
	}
}
