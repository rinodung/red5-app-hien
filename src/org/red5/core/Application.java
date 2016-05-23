package org.red5.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IBasicScope;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;

public class Application extends MultiThreadedApplicationAdapter
{
private String so_name;
private IScope appscope;
private HashMap<String,String> HashMap_OldMessage;
private HashMap<String,Integer> HashMap_ClientHash;
  public Application()
  {
    System.out.println("****** Hello Hien Application ****");
    this.so_name="OnlineList";//SharedObject name
    this.HashMap_OldMessage=new HashMap<String,String>();
    this.HashMap_ClientHash=new HashMap<String,Integer>();
  }

  public boolean appStart(IScope scope)
  {
	  System.out.println("appStart, name's scope is: " + scope.getName());
	  this.appscope=scope;	  
   
    return true;
  }

  public void appStop(IScope scope) {
  }

  public boolean appConnect(IConnection conn, Object[] params)
  {
   System.out.println("appConnect Method: " + params.length);

    return true;
  }

  public boolean appJoin(IClient client, IScope app)
  {
	  System.out.println("appJoin Method: " + app.getName());
    return true;
  }

  public void appLeave(IClient client, IScope app)
  {
  System.out.println("appLeave Method: " + app.getName());
  }

  public void appDisconnect(IConnection conn)
  {
/*    IScope scope = conn.getScope();
    String scope_name = "empty";
    if (scope != null)
    {
      scope_name = scope.getName();
    }

    String ip = conn.getRemoteAddress();
    int port = conn.getRemotePort();
    System.out.println("App Disconnect " + scope_name + " from " + ip + ": " + port);*/
  }
//Event when first client connect to a room, once!!!
  public boolean roomStart(IScope scope)
  {
    System.out.println("Start roome name: " + scope.getName());

    
    createSharedObject(scope, so_name, false);   
    ISharedObject so_ol = getSharedObject(scope, so_name);

    if (so_ol != null)
    {
      Map onlinelist = new HashMap();
      so_ol.beginUpdate();
      so_ol.setAttribute("count", Integer.valueOf(0));
      so_ol.setAttribute("ol", onlinelist);
      so_ol.endUpdate();
    }

    System.out.println("Create SO: " + so_name);

    return true;
  }
//Event when client connect to a room, then "roomJoin"
  public boolean roomConnect(IConnection conn, Object[] params)
  {
    IScope scope = conn.getScope();
    String scope_name = "empty";
    String client_cer = (String) params[1];
    String client_type = (String) params[2];
    
    //nc.connect(this.input_host, this.user_name, this.user_id, this.type_client, this.icon_name);		
    String user_name = (String)params[0];
    String user_id = (String)params[1];
    String type_client = (String)params[2];
    String icon_name = (String)params[3];
    String icon_position = (String)params[4];
    
    
    System.out.println("user id: "+ user_id + "user name: " + user_name + "type_client: " + type_client + " icon name: " + icon_name);
    
    int vote_status = 1;
    IClient client = conn.getClient();
    if (scope != null)
    {
      scope_name = scope.getName();
    }
    HashMap_ClientHash.put(client_cer, Integer.valueOf(client.getId()));
    String ip = conn.getRemoteAddress();
    int port = conn.getRemotePort();
    System.out.println("Connecting to " + scope_name + " from " + ip + ": " + port);
    if (params.length > 0)
    {
      String name = (String)params[0];
      String address = conn.getRemoteAddress()+":"+conn.getRemotePort();

      Client info = new Client(name, address,vote_status,Integer.valueOf(client.getId()),Integer.valueOf(client_cer),client_type, icon_name, icon_position);
      client.setAttribute("info", info);
    }

    return true;
  }
  
//event when client join a room after "roomConnect"
  public boolean roomJoin(IClient client, IScope scope)
  {
	System.out.println("Room Join Event");
    Client info = (Client)client.getAttribute("info");
    int num_client = get_num_client(scope.getName()) + 1;
    int client_id = Integer.valueOf(client.getId()).intValue();

    

    ISharedObject so_ol = getSharedObject(scope, so_name);
 //   System.out.println("Scope Name: " + scope.getName());
    System.out.println("Add to online_list: " + client_id + " - " + info.getName() + " - " + info.getAddress());
    Map online_list = (HashMap)so_ol.getAttribute("ol");
    if (online_list == null)
    {
      System.out.println("Online list current null");
    }
    else
    {
      online_list.put(Integer.valueOf(client_id), info);
    }

    if (so_ol != null)
    {
      so_ol.beginUpdate();
      so_ol.removeAttribute("ol");
      so_ol.setAttribute("ol", online_list);
      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));

      so_ol.endUpdate();


//      System.out.println("Total Client: " + online_list.size());
//      System.out.println(online_list);
    }

    return true;
  }

  //event when client leave a room
  public void roomLeave(IClient client, IScope scope)
  {
	Client info = (Client)client.getAttribute("info");
    int num_client = get_num_client(scope.getName());
    int client_id = Integer.valueOf(client.getId()).intValue();

    HashMap_ClientHash.remove(String.valueOf(info.getclient_cer()));
    ISharedObject so_ol = getSharedObject(scope, this.so_name);
    //get current online_list
    Map online_list = (HashMap)so_ol.getAttribute("ol");
    //remove client by id
    online_list.remove(Integer.valueOf(client_id));
    //keep trace
    System.out.println("Remove online_list " + info.getclient_cer());
    
    //update SO OnlineList
    if (so_ol != null)
    {
      so_ol.beginUpdate();
      so_ol.removeAttribute("ol");
      so_ol.setAttribute("ol", online_list);
      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
      so_ol.endUpdate();
    }
    
    //only keep trace here
 
    
  }

  public void roomStop(IScope scope)
  {
	  System.out.println("roomstop method: " + scope.getName());
  }
  //event after roomLeave excute to notify client disconnected to room
  public void roomDisconnect(IConnection conn)
  {
   /* IScope scope = conn.getScope();
    String scope_name = "empty";
    if (scope != null)
    {
      scope_name = scope.getName();
    }

    String ip = conn.getRemoteAddress();
    int port = conn.getRemotePort();
    System.out.println("Room Disconnect " + scope_name + " from " + ip + ": " + port);*/
  }//end roomDisconnect
  
  //event when Application add ChildScope
  public boolean addChildScope(IBasicScope scope)
  {
   // System.out.println("addChildScope " + scope.getName());
    return true;
  }
//	event when Application remove ChildScope
  public void removeChildScope(IBasicScope scope)
  {
	  String room=scope.getName();
   // System.out.println("removeChildScope " +room);
    this.HashMap_OldMessage.put(room,null);
  }



  public int get_num_client(String scope_name) {
    int count = 0;
    IScope scope = getChildScope(scope_name);
    if (scope != null) count = scope.getClients().size();
    return count;
  }

 
  public void sendMessage(String room,String message)
  {
	  IScope scope=this.appscope.getScope(room);
	  ISharedObject so_ol = getSharedObject(scope, this.so_name);
	  List<String> l= new ArrayList<String>();
	  l.add(message);
	  so_ol.sendMessage("receiveMessage", l);
	  String old_message=this.HashMap_OldMessage.get(room);
	  if(old_message==null)
	  {
		  String list_message=message+"\n";
		this.HashMap_OldMessage.put(room, list_message);
		System.out.println("OldMessage: "+list_message);
	  }
	  else
	  {
		  String list_message=old_message+message+"\n";
		  this.HashMap_OldMessage.put(room,list_message);
		  System.out.println("OldMessage: "+list_message);
	  }
  }
  public void sendCommand(String room,String command, String client_cer)
  {
	  IScope scope=this.appscope.getScope(room);
	  ISharedObject so_ol = getSharedObject(scope, this.so_name);
	  List<String> l= new ArrayList<String>();
	  System.out.println("Client id: "+HashMap_ClientHash);
	  // update list.
	  if(command.equals("vote"))
	  {
		   int client_id =  HashMap_ClientHash.get(client_cer);
	
//		  	l.add(command+"-"+String.valueOf(client_id));
//		  	so_ol.sendMessage("receiveCommand", l);
		  	System.out.println("Client id: "+client_id);	  
		    Map online_list = (HashMap)so_ol.getAttribute("ol");
		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
		    client.setVote_status(2);
		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client);

		    System.out.println("Value: "+String.valueOf(client.getVote_status()));
		    

		    if (so_ol != null)
		    {
		      so_ol.beginUpdate();
		      so_ol.removeAttribute("ol");
		      so_ol.setAttribute("ol", online_list);
		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
		      so_ol.endUpdate();
		    }
	  }
	  if(command.equals("canvote"))
	  {
		   int client_id =  HashMap_ClientHash.get(client_cer);

		
		  	System.out.println("Client id: "+client_id);	  
		    Map online_list = (HashMap)so_ol.getAttribute("ol");
		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
		    client.setVote_status(1);
		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client);

		    System.out.println("Value: "+String.valueOf(client.getVote_status()));
		    

		    if (so_ol != null)
		    {
		      so_ol.beginUpdate();
		      so_ol.removeAttribute("ol");
		      so_ol.setAttribute("ol", online_list);
		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
		      so_ol.endUpdate();
		    }
	  }
	  if(command.equals("accept"))
	  {
		  l.add(command+"-"+String.valueOf(client_cer));
        	so_ol.sendMessage("receiveCommand", l);
        	
        	 Map online_list = (HashMap)so_ol.getAttribute("ol");
 		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
 		    client.setVote_status(3);
 		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client);

 		    System.out.println("Value: "+String.valueOf(client.getVote_status()));
 		    

 		    if (so_ol != null)
 		    {
 		      so_ol.beginUpdate();
 		      so_ol.removeAttribute("ol");
 		      so_ol.setAttribute("ol", online_list);
 		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
 		      so_ol.endUpdate();
 		    }
	  }
	  if(command.equals("reject"))
	  {
		  	l.add(command+"-"+String.valueOf(client_cer));
        	so_ol.sendMessage("receiveCommand", l);
        	
        	 Map online_list = (HashMap)so_ol.getAttribute("ol");
 		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
 		    client.setVote_status(1);
 		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client);

 		    System.out.println("Value: "+String.valueOf(client.getVote_status()));
 		    

 		    if (so_ol != null)
 		    {
 		      so_ol.beginUpdate();
 		      so_ol.removeAttribute("ol");
 		      so_ol.setAttribute("ol", online_list);
 		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
 		      so_ol.endUpdate();
 		    }
	  }
	  
	  if(command.equals("setPosition"))
	  {
		  	l.add(command+"-"+String.valueOf(client_cer));
        	so_ol.sendMessage("receiveCommand", l);
        	
        	Map online_list = (HashMap)so_ol.getAttribute("ol");
 		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
 		    client.setclient_icon_position("0,0");
 		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client); 		    
 		    

 		    if (so_ol != null)
 		    {
 		      so_ol.beginUpdate();
 		      so_ol.removeAttribute("ol");
 		      so_ol.setAttribute("ol", online_list);
 		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
 		      so_ol.endUpdate();
 		    }
	  }	  

  }// set command function
  
  public void sendCommand(String room,String command, String client_cer, String agrs)
  {
	  IScope scope=this.appscope.getScope(room);
	  ISharedObject so_ol = getSharedObject(scope, this.so_name);
	  List<String> l= new ArrayList<String>();
	 
	  // update list.	 
	  
	  if(command.equals("setPosition"))
	  {
		  System.out.println("Client id: "+HashMap_ClientHash);
		  	l.add(command+"-"+String.valueOf(client_cer));
        	so_ol.sendMessage("receiveCommand", l);
        	
        	Map online_list = (HashMap)so_ol.getAttribute("ol");
 		    Client client  = (Client) online_list.get(Integer.valueOf(HashMap_ClientHash.get(client_cer)));
 		    client.setclient_icon_position(agrs);
 		    online_list.put(Integer.valueOf(HashMap_ClientHash.get(client_cer)), client); 		    
 		    

 		    if (so_ol != null)
 		    {
 		      so_ol.beginUpdate();
 		      so_ol.removeAttribute("ol");
 		      so_ol.setAttribute("ol", online_list);
 		      so_ol.setAttribute("count", Integer.valueOf(online_list.size()));
 		      so_ol.endUpdate();
 		    }
	  }	  

  }
  public String getOldMessage(String room)
  {
	  String result=this.HashMap_OldMessage.get(room);
	  if(result==null) result="";
	  System.out.println("getOldMessage method: "+result);
	  return result;
  }
}