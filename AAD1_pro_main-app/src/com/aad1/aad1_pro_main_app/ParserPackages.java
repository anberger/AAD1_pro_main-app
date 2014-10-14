package com.aad1.aad1_pro_main_app;

import com.google.gson.annotations.SerializedName;

public class ParserPackages {

	@SerializedName("origin")
	public String origin;
	
	@SerializedName("destination")
	public String destination;
	
	@SerializedName("type")
	public String type;
	
	@SerializedName("message")
	public String message;
	
	public ParserPackages(){
		
	}
}

