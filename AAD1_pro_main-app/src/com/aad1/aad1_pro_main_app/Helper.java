package com.aad1.aad1_pro_main_app;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
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
    
    public ArrayList<ClassDeviceState> updateDeviceState(String sObject, ArrayList<ClassDeviceState> devices){
		JsonObject jObject = new JsonParser().parse(sObject).getAsJsonObject();
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( jObject , ParserPackages.class);	
		
		if(parsed.type.equals("car") ||
		   parsed.type.equals("video") ||
		   parsed.type.equals("gps")){
			
			ClassDeviceState device = getDeviceFromArrayList(devices, parsed.type);
			
			boolean state = false;
			if(parsed.message.equals("online")) state = true; 
			
			if(device != null){
				int i = devices.indexOf(device);
				
				if(i != -1){					
					devices.get(i).setDeviceState(state);
				}
			}
			else {
				device = new ClassDeviceState();
				
				device.setDeviceIP(parsed.origin);
				device.setDeviceName(parsed.type);
				device.setDeviceState(state);
				
				devices.add(device);
			}	
		}
		return devices;
    }
    
    public ClassDeviceState getDeviceFromArrayList(ArrayList<ClassDeviceState> devices, String deviceName){
    	for(int i = 0; i < devices.size(); i++){
			if(devices.get(i).getDeviceName().equals(deviceName)){
				return devices.get(i);
			}
		} 	
    	return null;
    }
}
