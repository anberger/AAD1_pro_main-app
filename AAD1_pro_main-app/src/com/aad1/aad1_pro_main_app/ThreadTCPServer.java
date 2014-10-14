package com.aad1.aad1_pro_main_app;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


public class ThreadTCPServer extends Thread {
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<ThreadTCPClientComm> Clients = new ArrayList<ThreadTCPClientComm>();
	private static String TAG = "ServerActivity";
	private String SERVERID = "";
	private int PORT = 0;
	private static ThreadTCPClientComm mTCPClientComm;   
	private Handler mhandlerOutActivity;
	
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
	
	@SuppressLint("HandlerLeak")
	private Handler mhandlerActivity = new Handler(){
        @Override public void handleMessage(Message msg) 
        { 
        	Bundle bundleActivity = msg.getData();
        	SendBroadCast(bundleActivity.getString("message2thread"));
        } 
    };
    
    @SuppressLint("HandlerLeak") 
    public Handler mhandlerClient = new Handler(){   //handles the INcoming msgs 
        @Override public void handleMessage(Message msg) 
        {             	
        	Bundle bundle = msg.getData();
 
        	if(bundle != null){
        		MessageHandler(bundle);
        	}
        } 
    };
	
	/*
	 * Main Server Thread
	 * @see java.lang.Thread#run()
	 */
    
	public void run() {
		try {
	        Looper.prepare();
			@SuppressWarnings("resource")
			ServerSocket SERVER = new ServerSocket(PORT);
			Log.d(TAG,"Waiting for Clients");
			
			while(true){
				Socket SOCK = SERVER.accept();	
				if(SOCK != null){
					ConnectionArray.add(SOCK);
					
					String CLIENTID = getIpFromSocket(SOCK);    

					Send2Activity("Client with IP: "+ CLIENTID + " joined");
					
					mTCPClientComm = new ThreadTCPClientComm(mhandlerClient);
					mTCPClientComm.setConnected(true);
					mTCPClientComm.setSocket(SOCK);
					mTCPClientComm.setClientID(CLIENTID);
					mTCPClientComm.setServerID(this.SERVERID);
					mTCPClientComm.start();
					
					ThreadTCPServer.Clients.add(mTCPClientComm);
					SendBroadCast("new Client arrived with IP: " + CLIENTID);
				}
			}
		} catch(Exception X){
			Log.d(TAG, X.toString() + "Der oasch thread");
			Looper.myLooper().quit();
		} finally{
			Looper.myLooper().quit();
		}
	};
	
	public void Send2Activity(String message){
		Bundle bundle = new Bundle();
        Message msg = mhandlerOutActivity.obtainMessage();   
        bundle.putString("MSG", message);
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
				TMPClient.Send2Client(packageBuilder(
						this.SERVERID,
						ip,
						"Info",
						message));
			}
		}		
	}
	
	public void Send2Client(JsonObject object){
		
	}
	
	public void MessageHandler(Bundle b){
		
		String sObject = b.getString("object");
		JsonObject obj = new JsonParser().parse(sObject).getAsJsonObject();
		
		Gson gson = new GsonBuilder().create();
		ParserPackages parsed = gson.fromJson( obj , ParserPackages.class);	
		Log.d(TAG,parsed.destination);
		Log.d(TAG,parsed.type);
		Log.d(TAG,parsed.message);
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
	
	private JsonObject packageBuilder(String origin, String destination, String type, String message){
		
		JsonObject jObject = new JsonObject();
		
		jObject.addProperty("origin", origin);		
		jObject.addProperty("destination", destination);
		jObject.addProperty("type", type);
		jObject.addProperty("message", message);
		
		return jObject;
	}
}
