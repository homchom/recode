package dfcode.parameter;

public class Parameter 
{
	private String name;
	private Type type;
	
	public Parameter(Type type, int num) 
	{
		this.type = type;
		
		if(type instanceof StringType) 
		{
			this.name = "{\"item\":{\"id\":\"txt\",\"data\":{\"name\":\""+((StringType) type).getName()+"\"}},\"slot\":"+num+"}";
		}
		else if(type instanceof NumberType) 
		{
			this.name = "{\"item\":{\"id\":\"num\",\"data\":{\"name\":\""+((NumberType) type).getNum()+"\"}},\"slot\":"+num+"}";
		}
		else if(type instanceof ItemType) 
		{
			if(((ItemType) type).getName().equals("")) 
			{
				this.name = "{\"item\":{\"id\":\"item\",\"data\":{\"item\":\"{DF_NBT:1976,id:\\\"minecraft:"+
						((ItemType) type).getId()+"\\\",Count:"+((ItemType) type).getCount()+"b}\"}},\"slot\":"+num+"}";
			}
			else 
			{
				this.name = "{\"item\":{\"id\":\"item\",\"data\":{\"item\":\"{DF_NBT:1976,id:\\\"minecraft:"+
				((ItemType) type).getId()+"\\\",Count:"+((ItemType) type).getCount()+"b,tag:{display:{Name:'{\\\"text\\\":\\\""+
				((ItemType) type).getName()+"\\\"}'}}}\"}},\"slot\":"+num+"}";
			}
		}
		else if(type instanceof VarType) 
		{
			this.name = "{\"item\":{\"id\":\"var\",\"data\":{\"name\":\""+((VarType) type).getName()+"\",\"scope\":\"unsaved\"}},\"slot\":"+num+"}";
		}
		else if(type instanceof LocType) 
		{
			this.name = "{\"item\":{\"id\":\"loc\",\"data\":{\"isBlock\":false,\"loc\":{\"x\":"+((LocType) type).getX()+
			",\"y\":"+((LocType) type).getY()+",\"z\":"+((LocType) type).getZ()+",\"pitch\":"+((LocType) type).getPitch()+",\"yaw\":"+((LocType) type).getYaw()+"}}},\"slot\":"+num+"}";
		}
	}
	
	public String getName() 
	{
		return name;
	}
	
	public Type getType() 
	{
		return type;
	}
}
