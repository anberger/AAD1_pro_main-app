package com.aad1.aad1_pro_main_app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ThreadTCPClientComm extends Thread {
	public Socket SOCK = null;
	public boolean connected = false;
	public boolean firstTime = true;
	private DataOutputStream outputStream = null;
	private DataInputStream inputStream = null;
	private Helper helper = new Helper();
	
	@SuppressWarnings("unused")
	private String TAG = "ServerActivity";
	
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
        bundle.putString("Object", jObject.toString());        
        msg.setData(bundle);
        outHandler.sendMessage(msg);
    }

	// Send Data to Client
	public void Send2Client(JsonObject jObject){
		if(!SOCK.isClosed() && connected){
			try {
				String msg = jObject.toString() + "\n";
				outputStream.write(msg.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				this.connected = false;
				e.printStackTrace();
			}
		}
	}
	
	// Run Method
	public void run() {
		try {			
			// Set IP as identifier
			setClientID(SOCK.getInetAddress().toString().replace("/", ""));
			
			outputStream = new DataOutputStream(SOCK.getOutputStream());
			inputStream = new DataInputStream(SOCK.getInputStream());
			
			connected = true;	
			
			while(connected){
			 
			    if(inputStream.available() > 0){
				    	BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				        String inputLine;
				        while ((inputLine = in.readLine()) != null){
							JsonParser parser = new JsonParser();
						    JsonObject jObject = parser.parse(inputLine).getAsJsonObject();
						    Send2TCPServer(jObject);
				        }			           
			    }
				
				// Check connection
				if(!SOCK.isConnected() || SOCK.isClosed() || !SOCK.isBound()){
					this.connected = false;
				}
			}
			
		} catch(Exception X){
			try {
				SOCK.close();
			} catch (Exception e) {
				e.printStackTrace();
				Send2TCPServer(helper.packageBuilder(
						this.CLIENTID,
						this.SERVERID,
						"Error",
						"Unable to close socket"));
			}finally{
				Send2TCPServer(helper.packageBuilder(
						this.CLIENTID,
						this.SERVERID,
						"Error",
						"Socket closed due to Error " + X.toString()));
			}
		} finally{
			Send2TCPServer(helper.packageBuilder(
					this.CLIENTID,
					this.SERVERID,
					"Info",
					"Client left normally"));
		}
	};
}

