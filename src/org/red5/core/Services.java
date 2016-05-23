package org.red5.core;

import java.util.HashMap;
import java.util.Map;




public class Services {
	public Services()
	{
		System.out.println("Create Services");
	}
	public String saysomething()
	{
		return "Hello something";
	}
	public String echo(String str)
	{
		return str;
	}
	public User getuser(String id)
	{
		System.out.println("GetUser id: "+id);
		User obj=new User(id,"Nguyễn Minh Tùng");
		return obj;
	}
	public Map<String,User> getlistuser()
	{
		System.out.println("GetListUser");
		Map<String , User> list=new HashMap<String,User>();
		list.put("09520044", new User("09520044","Đồng Tiến Dũng"));
		list.put("09520032",new User("09520032","Nguyễn Quý Danh"));
		return list;
		
	}
	
	

}
