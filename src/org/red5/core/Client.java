package org.red5.core;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Client  implements Externalizable
{
	private String name;
	private String address;
	private int vote_status;
	private int client_id;
	private int client_cer;
	private String client_type;
	private String client_icon_name;
	private String client_icon_position;
  
	public String getclient_type() {
		return client_type;
	}

	public void setclient_type(String client_type) {
		this.client_type = client_type;
	}
	  
	public int getclient_id() {
		return client_id;
	}
	
	public void setclient_id(int _client_id) {
		this.client_id = _client_id;
	}
	
	public int getclient_cer() {
		return client_cer;
	}
	
	public void setclient_cer(int _client_cer) {
		this.client_cer = _client_cer;
	}
	
	public Client(String name, String address, int vote_status,int client_id, int client_cer, String client_type, String client_icon_name, String client_icon_position){
	    setName(name);
	    setAddress(address);
	    setVote_status(vote_status);
	    setclient_id(client_id);
	    setclient_cer(client_cer);
	    setclient_type(client_type);
	    this.setclient_icon_name(client_icon_name);
	    this.setclient_icon_position(client_icon_position);
	}
	
	public int getVote_status() {
		return vote_status;
	}
	
	public void setVote_status(int vote_status) {
		this.vote_status = vote_status;
	}
	
	/**
	 * GET/SET icon name
	 */
	public String getclient_icon_name() {
		return this.client_icon_name;
	}

	public void setclient_icon_name(String client_icon_name) {
		this.client_icon_name = client_icon_name;
	}
	
	/**
	 * GET/SET icon position
	 */
	public String getclient_icon_position() {
		return this.client_icon_position;
	}

	public void setclient_icon_position(String client_icon_position) {
		this.client_icon_position = client_icon_position;
	}
	
	public void writeExternal(ObjectOutput out)  throws IOException
	{
	    out.writeObject(this.name);
	    out.writeObject(this.address);
	    out.writeObject(this.vote_status);
	    out.writeObject(this.client_id);
	    out.writeObject(this.client_cer);
	    out.writeObject(this.client_type);
	    out.writeObject(this.client_icon_name);
	    out.writeObject(this.client_icon_position);
	}

	public void readExternal(ObjectInput in)  throws IOException, ClassNotFoundException
  	{
	    this.name = ((String)in.readObject());
	    this.address = ((String)in.readObject());
	    this.vote_status = ((Integer)in.readObject());
	    this.client_id = ((Integer)in.readObject());
	    this.client_cer = ((Integer)in.readObject());
	    this.client_type = ((String)in.readObject());
	    this.client_icon_name = ((String)in.readObject());
	    this.client_icon_position = ((String)in.readObject());
  	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAddress()
	{
		return this.address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}
}