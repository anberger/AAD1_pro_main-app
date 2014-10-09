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
import android.os.Message;
import android.util.Log;

public class TCPClientComm implements Runnable {
	public Socket SOCK = null;
	public boolean connected = false;
	private static String TAG = "ServerActivity";
	
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
                  Log.d("ServerActivity", line);
                  threadMsg(line);
              }
			}
			
		} catch(Exception X){
			Log.d(TAG, X.toString());
		} finally{
			
		}
	};
	
	private void threadMsg(String msg) {
		 
        if (!msg.equals(null) && !msg.equals("")) {
            Message msgObj = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("message", msg);
            msgObj.setData(b);
            handler.sendMessage(msgObj);
        }
    }

    // Define the Handler that receives messages from the thread and update the progress
    private final Handler handler = new Handler() {

        public void handleMessage(Message msg) {
             
            String aResponse = msg.getData().getString("message");

            if ((null != aResponse)) {
            	Log.d(TAG,"got a response from client");
            }
            else
            {
            	Log.d(TAG,"Got nothing");
            }    

        }
    };
}

