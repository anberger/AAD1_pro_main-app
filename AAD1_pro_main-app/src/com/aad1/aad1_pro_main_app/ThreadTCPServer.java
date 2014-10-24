package com.aad1.aad1_pro_main_app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ThreadTCPServer extends Thread {
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<ThreadTCPClientComm> Clients = new ArrayList<ThreadTCPClientComm>();
	private static String TAG = "ServerActivity";
	private String SERVERID = "";
	private ServerSocket SERVER = null;
	private int PORT = 0;
	private static ThreadTCPClientComm mTCPClientComm;   
	private Handler mhandlerOutActivity;	
	
	private Helper helper = new Helper();
	
	/*
	 * Getter and Setter Methods
	 */
	
	public Handler getHandler(){
        return mhandlerActivity;
    }
	
	public ThreadTCPServer(Handler mHandler) {
		mhandlerOutActivity = mHandler;
    }
	
	public void setPort(int port){
		this.PORT = port;
	}
	
	public void setServerId(String id){
		this.SERVERID = id;
	}
	
	/*
	 * Message Handlers
	 */
	
	private Handler mhandlerActivity = new Handler(new Handler.Callback() {	
        @Override 
        public boolean handleMessage(Message msg) 
        { 
        	Bundle bundleActivity = msg.getData();
        	
        	if(bundleActivity != null){
	        	if(bundleActivity.containsKey("Object")){
	        		SendBroadCast(bundleActivity.getString("Object"));
	        	}
        	}
        	return false;
        } 
    });
    
    public Handler mhandlerClient = new Handler(new Handler.Callback(){   //handles the INcoming msgs 
        @Override 
        public boolean handleMessage(Message msg) 
        {             	
        	Bundle bundle = msg.getData();
 
        	if(bundle != null){
        		if(bundle.containsKey("Object")){
        			MessageHandler(bundle.getString("Object"));
        		}
        	}
        	return false;
        } 
    });
	
	
	/*
	 * Main Server Thread
	 * @see java.lang.Thread#run()
	 */
    
	public void run() {			
		try {
			SERVER = new ServerSocket(PORT);
			Log.d(TAG,"Waiting for Clients");
			
			while(true){
				Socket SOCK = SERVER.accept();	
				if(SOCK != null){
					ConnectionArray.add(SOCK);
					
					String CLIENTID = getIpFromSocket(SOCK);    
					
					mTCPClientComm = new ThreadTCPClientComm(mhandlerClient);
					mTCPClientComm.setSocket(SOCK);
					mTCPClientComm.setClientID(CLIENTID);
					mTCPClientComm.setServerID(this.SERVERID);
					mTCPClientComm.start();
					
					ThreadTCPServer.Clients.add(mTCPClientComm);
					SendBroadCast("new Client arrived with IP: " + CLIENTID);
					Log.d(TAG,"new Client arrived with IP: " + CLIENTID);
				}
			}
		} catch(Exception X){
			Log.d(TAG, X.toString());
		} finally{			
			try {
				SERVER.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	public void Send2Activity(JsonObject jObject){
		Bundle bundle = new Bundle();
        Message msg = mhandlerOutActivity.obtainMessage();   
        bundle.putString("Object", jObject.toString());
        msg.setData(bundle);
        mhandlerOutActivity.sendMessage(msg);
    }
	
	public void SendBroadCast(String message) {
		
		ThreadTCPClientComm TMPClient = null;
		Socket TMPSocket = null;
		
		for(int i = 1; i<= ThreadTCPServer.Clients.size();i++){
			TMPClient = ThreadTCPServer.Clients.get(i-1);
			TMPSocket = ThreadTCPServer.ConnectionArray.get(i-1);
			String ip = getIpFromSocket(TMPSocket);
			
			if(TMPClient != null && TMPSocket != null){
				TMPClient.Send2Client(helper.packageBuilder(
						this.SERVERID,
						ip,
						"Info",
						message));
			}
		}		
	}
	
	public void Send2Client(JsonObject jObject){
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( jObject , ParserPackages.class);	
		
		ThreadTCPClientComm TMPClient = null;
		Socket TMPSocket = null;
		
		for(int i = 1; i<= ThreadTCPServer.Clients.size();i++){
			TMPClient = ThreadTCPServer.Clients.get(i-1);
			TMPSocket = ThreadTCPServer.ConnectionArray.get(i-1);
			String ip = getIpFromSocket(TMPSocket);
			
			if(TMPClient != null && TMPSocket != null){
				if(ip.equals(parsed.destination)){
					TMPClient.Send2Client(jObject);
				}
			}
		}	
		
	}
	
	public void MessageHandler(String sObject){
		JsonObject jObject = new JsonParser().parse(sObject).getAsJsonObject();
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( jObject , ParserPackages.class);	
		
		if(parsed.destination.equals(this.SERVERID)){
			Send2Activity(jObject);
		}
		else {
			Send2Client(jObject);
		}
	}
	
	public static void removeClient(){	
		for(int i=0; i<ConnectionArray.size();i++){
			if(ConnectionArray.get(i).isClosed()){
				ThreadTCPServer.Clients.remove(i);
				ThreadTCPServer.ConnectionArray.remove(i);
				Log.d(TAG,"client kicked");
			}
		}
			
	}
	
	public String getIpFromSocket(Socket SOCK){
		return SOCK.getInetAddress().toString().replace("/", "");	
	}
}
