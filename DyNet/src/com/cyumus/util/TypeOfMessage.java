package com.cyumus.util;

public enum TypeOfMessage {
	SIMPLE ("M"), 
	IMPORTANT ("/!\\"), 
	INPUT ("I"),
	OUTPUT ("O"),
	BROADCAST ("B"),
	ERROR ("ERROR"),
	ADMIN ("ADMIN"),
	CONFIG ("CONFIG"),
	SERVER ("SERVER");
	
	private String strFlag;
	TypeOfMessage(String flag){
		this.strFlag = flag;
	}
	public String toString(){
		return this.strFlag;
	}
}
