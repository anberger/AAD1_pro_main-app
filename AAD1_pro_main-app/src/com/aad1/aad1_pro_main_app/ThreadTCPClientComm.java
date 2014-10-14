package com.aad1.aad1_pro_main_app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ThreadTCPClientComm extends Thread {
	public Socket SOCK = null;
	public boolean connected = false;
	public boolean firstTime = true;
	private BufferedOutputStream outputStream = null;
	private BufferedInputStream inputStream = null;
	
	// Constants for identification of the message
	public String CLIENTID = null; 
	public String SERVERID = null;
	public static final int CLIENTMSG = 1;
	public static final int CLIENTLEFT = 2;
	public static final int ERROR = 3;
	
	// Output Handler
	Bundle bundle = new Bundle();
	private Handler outHandler; 
	
	// Setter Methods
	public ThreadTCPClientComm(Handler mHandler) {
        outHandler = mHandler;
    }
	public void setSocket(Socket sock){
		this.SOCK = sock;
	}
	public void setConnected(boolean value){
		this.connected = value;
	}
	public void setClientID(String id){
		this.CLIENTID = id;
	}
	public void setServerID(String id){
		this.SERVERID = id;
	}
	
	// This Method sends Data to the TCP Server Class
	private void Send2TCPServer(JsonObject jObject){
        Message msg = outHandler.obtainMessage(); 
        bundle.putString("object", jObject.toString());        
        msg.setData(bundle);
        outHandler.sendMessage(msg);
    }

	// Send Data to Client
	public void Send2Client(JsonObject jObject){
		if(SOCK.isConnected()){
			try {
				outputStream.write(jObject.toString().getBytes());
				outputStream.flush();
			} catch (IOException e) {
				this.connected = false;
				e.printStackTrace();
			}
		}
	}
	
	private JsonObject packageBuilder(String origin, String destination, String type, String message){
		
		JsonObject jObject = new JsonObject();
		
		jObject.addProperty("origin", origin);		
		jObject.addProperty("destination", destination);
		jObject.addProperty("type", type);
		jObject.addProperty("message", message);
		
		return jObject;
	}
	
	// Run Method
	public void run() {
		try {
			Looper.prepare();
			
			// Set IP as identifier
			setClientID(SOCK.getInetAddress().toString().replace("/", ""));
			
			outputStream = new BufferedOutputStream(SOCK.getOutputStream());
			inputStream = new BufferedInputStream(SOCK.getInputStream());
			
			while(connected){
			
				byte[] buff = new byte[256];
			    int len = 0;
			    String msg = null;
			 
			    while ((len = inputStream.read(buff)) != -1) {
			        msg = new String(buff, 0, len);
			        
					JsonParser parser = new JsonParser();
				    JsonObject jObject = parser.parse(msg).getAsJsonObject();
				    Send2TCPServer(jObject);
			    }
				
				// Check connection
				if(!SOCK.isConnected() || SOCK.isClosed() || !SOCK.isBound()){
					this.connected = false;
				}
			}
			
		} catch(Exception X){
			Looper.myLooper().quit();
			try {
				SOCK.close();
			} catch (IOException e) {
				e.printStackTrace();
				Send2TCPServer(packageBuilder(
						this.CLIENTID,
						this.SERVERID,
						"Error",
						"Unable to close socket"));
			}finally{
				Send2TCPServer(packageBuilder(
						this.CLIENTID,
						this.SERVERID,
						"Error",
						"Socket closed due to Error " + X.toString()));
			}
		} finally{
			Send2TCPServer(packageBuilder(
					this.CLIENTID,
					this.SERVERID,
					"Info",
					"Client left normally"));
			Looper.myLooper().quit();
		}
	};
}

