package com.aad1.aad1_pro_main_app;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMain extends Activity {
	@SuppressWarnings("unused")
	private static String TAG = "ServerActivity";
	TextView txt_port = null;
	TextView txt_ip = null;
	ListView list = null;
	private int listenerPort = 6000;
	ArrayList<String> logList = null;
	ServiceTCP mService;
    boolean mBound = false;
    ArrayAdapter<String> arrayAdapter = null;

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
 
    @Override
	protected void onResume() {
	    super.onResume();
	    Intent intent= new Intent(this, ServiceTCP.class);
	    bindService(intent, mConnection,
	        Context.BIND_AUTO_CREATE);
	}
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if(mBound){
    		unbindService(mConnection);
    	}
	}    
    
    private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, 
	        IBinder binder) {
	    	ServiceTCP.TCPBinder b = (ServiceTCP.TCPBinder) binder;
	    	mService = b.getService();
	    	Toast.makeText(ActivityMain.this, "Connected", Toast.LENGTH_SHORT)
	          .show();
	    	mBound = true;
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	mService= null;
	    	mBound = false;
	    }
	  };
    
    
    // ListView Updates
    
    public void clearList(View view){
    	startService(new Intent(ActivityMain.this, ServiceTCP.class));
    	logList.removeAll(logList);
    	arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, logList);
    	
    	// Set The Adapter
    	this.list.setAdapter(arrayAdapter); 
    }

	private void updateList(){
    	arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, logList);
    	
    	// Set The Adapter
    	this.list.setAdapter(arrayAdapter); 
    }
    
    public void clicker(View view){
    	
	};
 	
	// Getting the IP Address of the device
	
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
