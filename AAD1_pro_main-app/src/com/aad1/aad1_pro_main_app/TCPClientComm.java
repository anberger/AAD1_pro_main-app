package com.aad1.aad1_pro_main_app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class TCPClientComm implements Runnable {
	public Socket SOCK = null;
	public boolean connected = false;
	private static String TAG = "ServerActivity";
	public Handler mHandler;
	
	public void setSocket(Socket sock){
		this.SOCK = sock;
	}
	public void setConnected(boolean value){
		this.connected = value;
	}
	
	public void send(String message){
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(SOCK.getOutputStream())), true);
			out.println(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(connected){
				BufferedReader in = new BufferedReader(new InputStreamReader(SOCK.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
                  Log.d(TAG, line);
				}
				
				if(!SOCK.isConnected() || SOCK.isClosed() || !SOCK.isBound()){
					Log.d(TAG,"Client left");
					this.connected = false;
				}
			}
			
		} catch(Exception X){
			try {
				Log.d(TAG,"Client");
				SOCK.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG,e.toString());
			}finally{
				Log.d(TAG,"Client left");
			}
		} finally{
			Log.d(TAG,"kick him out");
		}
	};
}

