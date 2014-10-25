package com.aad1.aad1_pro_main_app;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

public class ServiceTCP extends Service {

    // Binder given to clients
    private final IBinder mBinder = new TCPBinder();
    private String PORT = "";
    private String SERVERID = "";
    private boolean isAlreadyConnected = false;
    private Helper helper = new Helper();
    
    // These states indicates which device is online 
    private ArrayList<ClassDeviceState> devices = new ArrayList<ClassDeviceState>();
    
    Bundle  bundle = new Bundle(); 
    private ThreadTCPServer mTCPServer;   
    
    // Getter method for the DeviceState
    public ClassDeviceState getDeviceStates(String deviceName){
    	return helper.getDeviceFromArrayList(devices, deviceName);
    }
    
	public Handler mHandler = new Handler(new Handler.Callback(){   //handles the INcoming msgs 
        @Override 
        public boolean handleMessage(Message msg) 
        {     
        	bundle = msg.getData();
        	if(bundle != null){
	        	if(bundle.containsKey("Object")){
	        		
	        		String message = bundle.getString("Object");
	        		
	        		// Send the message to the Activity
	        		send2Activity(message);
	        		
	        		// If the devices change his states then update the states in this service
	        		devices = helper.updateDeviceState(message, devices);
	        	}
        	}
        	return false;
        } 
    });
     
    private void send2Activity(String msg){
    	Intent intent = new Intent("messages");
        intent.putExtra("Object", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }    
    
    public void send2Server(String message){
		Message msg = mTCPServer.getHandler().obtainMessage(); 
    	bundle.putString("Object", message);
        msg.setData(bundle);
        mTCPServer.getHandler().sendMessage(msg);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TCPBinder extends Binder {
    	ServiceTCP getService() {
            // Return this instance of LocalService so clients can call public methods
            return ServiceTCP.this;
        }
    } 
    

    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	Bundle bundle = intent.getExtras();
    	if(bundle != null){
    		if(bundle.containsKey("port"))
    			this.PORT = bundle.getString("port");
    		if(bundle.containsKey("serverid"))
    			this.SERVERID = bundle.getString("serverid");
    	}
    	startTCP();
		return START_STICKY;
	}
    
    private void startTCP(){
    	if(!this.isAlreadyConnected){
    		this.isAlreadyConnected = true;
    		mTCPServer = new ThreadTCPServer(mHandler);
         	mTCPServer.setPort(Integer.parseInt(this.PORT));
         	mTCPServer.setServerId(this.SERVERID);
         	mTCPServer.start();
    	}
    }

	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
