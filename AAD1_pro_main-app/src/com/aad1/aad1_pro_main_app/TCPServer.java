package com.aad1.aad1_pro_main_app;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TCPServer extends Thread {
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<TCPClientComm> Clients = new ArrayList<TCPClientComm>();
	private static String TAG = "ServerActivity";
	public static final int PORT = 6000;
	Bundle bundle = new Bundle();

	private Handler outHandler;  
	private Handler inHandler = new Handler(){
        @Override public void handleMessage(Message msg) 
        { 
        	bundle = msg.getData();
        	sendBroadCast(bundle.getString("message2thread"));
        } 
    };
	
	public Handler getHandler(){
        return inHandler;
    }
	
	public TCPServer(Handler mHandler) {
        outHandler = mHandler;
    }
	
	public void run() {
		try {
	        Looper.prepare();
			@SuppressWarnings("resource")
			ServerSocket SERVER = new ServerSocket(PORT);
			Log.d(TAG,"Waiting for Clients");
			
			while(true){
				Socket SOCK = SERVER.accept();	
				//checkClients();
				if(SOCK != null){
					ConnectionArray.add(SOCK);
					
					String ipAddress = null;
					ipAddress = SOCK.getInetAddress().toString();
					Log.d(TAG,"Client connected with IP " + ipAddress);				    

					sendMsgToMainThread("Client with IP: "+ ipAddress + " joined");
					
					TCPClientComm client = new TCPClientComm();
					client.setSocket(SOCK);
					client.setConnected(true);
					
					Thread cThreads = new Thread(client);
					cThreads.start();
					
					TCPServer.Clients.add(client);
					sendBroadCast("new Client arrived with IP: " + ipAddress);
				}
			}
			
			
		} catch(Exception X){
			Log.d(TAG, X.toString());
			Looper.myLooper().quit();
		} finally{
			
		}
	};
	
	private void sendMsgToMainThread(String message){
        Message msg = outHandler.obtainMessage();   
        bundle.putString("message2activity", message);
        msg.setData(bundle);
        outHandler.sendMessage(msg);
    }
	
	public static void sendBroadCast(String message) {
		
		TCPClientComm TMPClient = null;
		
		Log.d(TAG,"SendBroadcast: " + message);
		
		for(int i = 1; i<= TCPServer.Clients.size();i++){
			TMPClient = TCPServer.Clients.get(i-1);
			TMPClient.send(message);
		}		
	}
	
//	public static void checkClients(){		
//		for(int i = 1; i<= TCPServer.Clients.size();i++){
//			if(!TCPServer.Clients.get(i-1).connected){
//				TCPServer.Clients.remove(i);
//				TCPServer.ConnectionArray.remove(i);
//				sendBroadCast("ohje Client left");
//				Log.d(TAG,"Client left");
//			}
//		}
//	}
}
