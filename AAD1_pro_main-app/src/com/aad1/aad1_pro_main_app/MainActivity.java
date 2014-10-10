package com.aad1.aad1_pro_main_app;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static String TAG = "ServerActivity";
	TextView txt_port = null;
	TextView txt_ip = null;
	ListView list = null;
	ArrayList<String> logList = null;
	private int listenerPort = 6000;
	
	private TCPServer mTCPServer;   
	Bundle  bundle = new Bundle(); 
	
	@SuppressLint("HandlerLeak") 
	public Handler mHandler = new Handler(){   //handles the INcoming msgs 
        @Override public void handleMessage(Message msg) 
        {     
        	bundle = msg.getData();
        	
        	if(bundle.containsKey("MSG")){
	            logList.add(bundle.getString("MSG"));
	            Log.d(TAG,bundle.getString("MSG"));
	            updateList();
        	}
        } 
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     	txt_ip = (TextView)findViewById(R.id.deviceIP);
     	txt_port = (TextView)findViewById(R.id.localPort);
     	list = (ListView)findViewById(R.id.listView1);
     	txt_ip.setText(wifiIpAddress(getApplicationContext())); 
        txt_port.setText(String.valueOf(this.listenerPort));
     	
     	if(savedInstanceState == null){
	     	// TCP Server Connection
	     	mTCPServer = new TCPServer(mHandler);
	     	mTCPServer.setPort(listenerPort);
	     	mTCPServer.start();
	     	logList = new ArrayList<String>();
     	}
     	else {
     		logList = savedInstanceState.getStringArrayList("logList");
     	}
     	updateList();
    } 
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
    	outState.putStringArrayList("logList", logList);
		super.onSaveInstanceState(outState);
	}
    
    public void clearList(View view){
    	logList.removeAll(logList);
    	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, logList);
    	
    	// Set The Adapter
    	this.list.setAdapter(arrayAdapter); 
    }

	private void updateList(){
    	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, logList);
    	
    	// Set The Adapter
    	this.list.setAdapter(arrayAdapter); 
    }
    
    public void clicker(View view){
    	Message msg = mTCPServer.getHandler().obtainMessage(); 
    	bundle.putString("message2thread", "Männer figt euch doch");
        msg.setData(bundle);
        mTCPServer.getHandler().sendMessage(msg);
	};
 	
 	protected String wifiIpAddress(Context context) {
 	    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
 	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

 	    // Convert little-endian to big-endianif needed
 	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
 	        ipAddress = Integer.reverseBytes(ipAddress);
 	    }

 	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

 	    String ipAddressString;
 	    try {
 	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
 	    } catch (UnknownHostException ex) {
 	        Log.e("WIFIIP", "Unable to get host address.");
 	        ipAddressString = null;
 	    }

 	    return ipAddressString;
 	} 	
}
