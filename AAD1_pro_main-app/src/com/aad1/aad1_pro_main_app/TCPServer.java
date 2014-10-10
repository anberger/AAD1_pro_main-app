package com.aad1.aad1_pro_main_app;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TCPServer extends Thread {
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<TCPClientComm> Clients = new ArrayList<TCPClientComm>();
	private static String TAG = "ServerActivity";
	public int PORT = 6000;
	Bundle bundleActivity = new Bundle();
	static Bundle bundleClient = new Bundle(); 
	private Helper helper = new Helper();
	private static TCPClientComm mTCPClientComm;   
	private Handler mhandlerOutActivity;
	
	@SuppressLint("HandlerLeak")
	private Handler mhandlerActivity = new Handler(){
        @Override public void handleMessage(Message msg) 
        { 
        	bundleActivity = msg.getData();
        	SendBroadCast(bundleActivity.getString("message2thread"));
        } 
    };
    
    @SuppressLint("HandlerLeak") 
    public Handler mhandlerClient = new Handler(){   //handles the INcoming msgs 
        @Override public void handleMessage(Message msg) 
        {             	
        	bundleClient = msg.getData();
        	ClientMessage(bundleClient);
        } 
    };
	
	public Handler getHandler(){
        return mhandlerActivity;
    }
	
	public TCPServer(Handler mHandler) {
		mhandlerOutActivity = mHandler;
    }
	
	public void setPort(int port){
		this.PORT = port;
	}
	
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
					
					String ipAddress = null;
					ipAddress = SOCK.getInetAddress().toString();
					Log.d(TAG,"Client connected with IP " + ipAddress);				    

					Send2Activity("Client with IP: "+ ipAddress + " joined");
					
					mTCPClientComm = new TCPClientComm(mhandlerClient);
					mTCPClientComm.setConnected(true);
					mTCPClientComm.setSocket(SOCK);
					mTCPClientComm.start();
					
					TCPServer.Clients.add(mTCPClientComm);
					SendBroadCast("new Client arrived with IP: " + ipAddress);
				}
			}
		} catch(Exception X){
			Log.d(TAG, X.toString());
			Looper.myLooper().quit();
		} finally{
			Looper.myLooper().quit();
		}
	};
	
	private void Send2Activity(String message){
        Message msg = mhandlerOutActivity.obtainMessage();   
        bundleActivity.putString("MSG", message);
        msg.setData(bundleActivity);
        mhandlerOutActivity.sendMessage(msg);
    }
	
	public static void SendBroadCast(String message) {
		
		TCPClientComm TMPClient = null;
		Log.d(TAG,"SendBroadcast: " + message);
		
		for(int i = 1; i<= TCPServer.Clients.size();i++){
			TMPClient = TCPServer.Clients.get(i-1);
			TMPClient.send(message);
		}		
	}
	
	private void ClientMessage(Bundle b){
		String[] list = {"ID","TYPE","MSG"};
		String message = "", id = "";
    	int type = -1;
    	
    	if(bundleClient != null && helper.containsCheck(bundleClient, list)){
    		id = bundleClient.getString("ID");
        	type = bundleClient.getInt("TYPE");
        	message = bundleClient.getString("MSG");
        	Send2Activity("DEVICE: " + id + " SEND: " + message);
        	
        	switch(type){
        		case TCPClientComm.CLIENTMSG : {
        			Log.d(TAG,id);
        			Log.d(TAG,String.valueOf(type));
        			Log.d(TAG,message);
        			break;
        		}
        		case TCPClientComm.CLIENTLEFT : {
        			removeClient();
        			Log.d(TAG,id);
        			Log.d(TAG,String.valueOf(type));
        			Log.d(TAG,message);
        			break;
        		}
        		case TCPClientComm.ERROR : {
        			Log.d(TAG,id);
        			Log.d(TAG,String.valueOf(type));
        			Log.d(TAG,message);
        			break;
        		}
        	}
    	}
	}
	
	public static void removeClient(){	
		for(int i=0; i<ConnectionArray.size();i++){
			if(ConnectionArray.get(i).isClosed()){
				TCPServer.Clients.remove(i);
				TCPServer.ConnectionArray.remove(i);
				Log.d(TAG,"client kicked");
			}
		}
			
	}
}
