package com.aad1.aad1_pro_main_app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TCPClientComm extends Thread {
	public Socket SOCK = null;
	public boolean connected = false;
	private static String TAG = "ServerActivity";
	private PrintWriter out;
	
	// Constants for identification of the message
	public String CLIENTID = ""; 
	public static final int CLIENTMSG = 1;
	public static final int CLIENTLEFT = 2;
	public static final int ERROR = 3;
	
	// Output Handler
	Bundle bundle = new Bundle();
	private Handler outHandler; 
	
	// Setter Methods
	public TCPClientComm(Handler mHandler) {
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
	
	// This Method sends Data to the TCP Server Class
	private void send2TCPServer(int type, String message){
        Message msg = outHandler.obtainMessage(); 
        bundle.putString("ID",CLIENTID);
        bundle.putInt("TYPE", type);
        bundle.putString("MSG", message);
        
        msg.setData(bundle);
        outHandler.sendMessage(msg);
    }

	// Send Data to Client
	public void send(String message){
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SOCK.getOutputStream())), true);
			out.println(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Run Method
	public void run() {
		try {
			Looper.prepare();
			
			// Set IP as identifier
			setClientID(SOCK.getInetAddress().toString());
			
			while(connected){
				
				
				// Read Inputstream
				BufferedReader in = new BufferedReader(new InputStreamReader(SOCK.getInputStream()));
				String line = null;
				
				// If there are messages available send them to TCPServer
				while ((line = in.readLine()) != null) {
                  send2TCPServer(CLIENTMSG,line);
				}
				
				// Check connection
				if(!SOCK.isConnected() || SOCK.isClosed() || !SOCK.isBound()){
					this.connected = false;
				}
			}
			
		} catch(Exception X){
			Looper.myLooper().quit();
			try {
				send2TCPServer(ERROR,"Client left unexpected");
				SOCK.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG,e.toString());
			}finally{
			}
		} finally{
			send2TCPServer(CLIENTLEFT,"Client left normally");
			Looper.myLooper().quit();
		}
	};
}

