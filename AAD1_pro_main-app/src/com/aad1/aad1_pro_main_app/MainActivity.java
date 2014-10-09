package com.aad1.aad1_pro_main_app;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

//import com.aad1.aad1_pro_main_app.TCPServerService.ServerThread;

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
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	TextView txt_resp = null;
	TextView txt_ip = null;
	Intent serviceIntent;
	private static String TAG = "ServerActivity";
	
	private TCPServer mTCPServer;   
	Bundle  bundle = new Bundle(); 
	
	public Handler mHandler = new Handler(){   //handles the INcoming msgs 
        @Override public void handleMessage(Message msg) 
        {     
        	bundle = msg.getData();
            Toast.makeText(getApplicationContext(), bundle.getString("message2activity"), Toast.LENGTH_SHORT).show(); 
        } 
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     	LocalBroadcastManager.getInstance(this).registerReceiver(MessageReceiver,new IntentFilter("message"));
     	txt_resp = (TextView)findViewById(R.id.txt_response);
     	txt_ip = (TextView)findViewById(R.id.txt_ip);
     	
     	mTCPServer = new TCPServer(mHandler);
     	mTCPServer.start();
        
        txt_ip.setText(wifiIpAddress(getApplicationContext()));      
        
    }
    
    public void clicker(View view){
    	Message msg = mTCPServer.getHandler().obtainMessage(); 
    	bundle.putString("message2thread", "Männer figt euch doch");
        msg.setData(bundle);
        mTCPServer.getHandler().sendMessage(msg);
	};

    
 // Our handler for received Intents. This will be called whenever an Intent sends an action
 	private BroadcastReceiver MessageReceiver = new BroadcastReceiver() {
 		@Override
 		public void onReceive(Context context, Intent intent) {
 			String data = intent.getStringExtra("data");
 			txt_resp.setText(data);
 		}
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
