package com.cyumus.dynet;

public enum TypeOfMessage {
	SIMPLE ("S"), 
	IMPORTANT ("*"), 
	INPUT ("I"),
	OUTPUT ("O"),
	BROADCAST ("B"),
	ERROR ("ERROR"),
	ADMIN ("A"),
	CONFIG ("C");
	
	private String strFlag;
	TypeOfMessage(String flag){
		this.strFlag = flag;
	}
	public String toString(){
		return this.strFlag;
	}
}