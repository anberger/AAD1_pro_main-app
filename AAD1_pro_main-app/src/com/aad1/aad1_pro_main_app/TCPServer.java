package com.aad1.aad1_pro_main_app;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import android.util.Log;

public class TCPServer implements Runnable {
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<TCPClientComm> Clients = new ArrayList<TCPClientComm>();
	private static String TAG = "ServerActivity";
	public static final int PORT = 6000;
	
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket SERVER = new ServerSocket(PORT);
			Log.d(TAG,"Waiting for Clients");
			
			while(true){
				Socket SOCK = SERVER.accept();	
				checkClients();
				if(SOCK != null){
					ConnectionArray.add(SOCK);
					
					String ipAddress = null;
					ipAddress = SOCK.getInetAddress().toString();
					Log.d(TAG,"Client connected with IP " + ipAddress);	
					
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
		} finally{
			
		}
	};
	
	public static void sendBroadCast(String message) {
		
		TCPClientComm TMPClient = null;
		
		for(int i = 1; i<= TCPServer.Clients.size();i++){
			TMPClient = TCPServer.Clients.get(i-1);
			TMPClient.send(message);
		}		
	}
	
	public static void checkClients(){		
		for(int i = 1; i<= TCPServer.Clients.size();i++){
			if(!TCPServer.Clients.get(i-1).connected){
				TCPServer.Clients.remove(i);
				TCPServer.ConnectionArray.remove(i);
				sendBroadCast("ohje Client left");
				Log.d(TAG,"Client left");
			}
		}
	}
}
