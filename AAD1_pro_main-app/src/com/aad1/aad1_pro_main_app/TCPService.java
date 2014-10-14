package com.aad1.aad1_pro_main_app;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class TCPService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new TCPBinder();
    private final int listenerPort = 6000;
    private boolean isAlreadyConnected = false;
    
	private static String TAG = "ServerActivity";

    Bundle  bundle = new Bundle(); 
    private TCPServer mTCPServer;   
    
    
    @SuppressLint("HandlerLeak") 
	public Handler mHandler = new Handler(){   //handles the INcoming msgs 
        @Override public void handleMessage(Message msg) 
        {     
        	bundle = msg.getData();
        	
        	if(bundle.containsKey("MSG")){
        		Log.d(TAG,bundle.getString("MSG"));
        		Log.d(TAG,"FUCK");
        	}
        } 
    };
    
    public void send2Server(String message){
    	Message msg = mTCPServer.getHandler().obtainMessage(); 
    	bundle.putString("MSG", message);
        msg.setData(bundle);
        mTCPServer.getHandler().sendMessage(msg);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TCPBinder extends Binder {
    	TCPService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TCPService.this;
        }
    } 
    

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
    	startTCP();
		return START_STICKY;
	}
    
    private void startTCP(){
    	if(!this.isAlreadyConnected){
    		this.isAlreadyConnected = true;
    		mTCPServer = new TCPServer(mHandler);
         	mTCPServer.setPort(listenerPort);
         	mTCPServer.start();
    	}
    }

	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
