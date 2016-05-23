package org.red5.core;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
public class User implements Externalizable {
	private String id;
	private String name;
	
	public User(String id, String name)
	{
		this.id=id;
		this.name=name;
	}
	
	public void setid(String id)
	{
		this.id=id;
	}
	public void setname(String name)
	{
		this.name=name;
	}
	public String getid()
	{
		return this.id;
	}
	public String getname()
	{
		return this.name;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeObject(id);
		out.writeObject(name);
		
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		id=(String)in.readObject();
		this.name=(String)in.readObject();
		
	}
	
}
