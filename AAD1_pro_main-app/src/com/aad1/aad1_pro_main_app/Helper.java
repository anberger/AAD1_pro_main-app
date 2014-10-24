package com.aad1.aad1_pro_main_app;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import org.apache.http.conn.util.InetAddressUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.annotation.SuppressLint;
import android.os.Bundle;

public class Helper {
	public boolean containsCheck(Bundle b, String[] list){
		
		for(int i=0;i<list.length;i++){
			if(!b.containsKey(list[i].toString())){
				return false;
			}
		}
		return true;
	}
	
	/**
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    @SuppressLint("DefaultLocale") 
    public String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                            if (isIPv4) 
                                return sAddr;
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
    
    public JsonObject packageBuilder(String origin, String destination, String type, String message){
		
		JsonObject jObject = new JsonObject();
		
		jObject.addProperty("origin", origin);		
		jObject.addProperty("destination", destination);
		jObject.addProperty("type", type);
		jObject.addProperty("message", message);
		
		return jObject;
	}
    
    public ParserPackages messageParser(String sObject){
		JsonObject jObject = new JsonParser().parse(sObject).getAsJsonObject();
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( jObject , ParserPackages.class);	
    	return parsed;
    }
    
    @SuppressLint("DefaultLocale") 
    public boolean[] updateDeviceState(String sObject, boolean[] state){
		JsonObject jObject = new JsonParser().parse(sObject).getAsJsonObject();
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( jObject , ParserPackages.class);	
		
		if(parsed.type.equals("car") ||
		   parsed.type.equals("video") ||
		   parsed.type.equals("gps")){
		
			Devices enumval = Devices.valueOf(parsed.type.toUpperCase());
			switch (enumval) {
			   case CAR: 
				   if(parsed.message.equals("online")) state[0] = true;
				   else state[0] = false;
				   break;
			   case VIDEO: 
				   if(parsed.message.equals("online")) state[1] = true;
				   else state[1] = false;
				   break;
			   case GPS: 
				   if(parsed.message.equals("online")) state[2] = true;
				   else state[2] = false;
				   break;
			}
		}
		return state;
    }
    public enum Devices {
        CAR,
        VIDEO,
        GPS;

		public static Devices fromString(String type) {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
