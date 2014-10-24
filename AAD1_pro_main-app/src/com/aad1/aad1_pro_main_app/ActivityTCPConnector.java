package com.aad1.aad1_pro_main_app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

/*
 * Activity TCPConnectionActivity 
 * This Activity gets called when the APP is launching at the first time.
 */

public class ActivityTCPConnector extends FragmentActivity implements FragmentStartServer.FragmentCommunicator {

	private String TAG = "ServerActivity";
	ServiceTCP mService;
    boolean mBound = false;
    private String listenerPort = "6000";
	private Helper helper = new Helper();
	private boolean[] modes = {false,false,false};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_connection);
		
		if(savedInstanceState == null){
			fragLogo();
			fragTCPStart();
		}
		else {
			modes = savedInstanceState.getBooleanArray("modes");
			fragLoading();
			fragClients(modes);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putBooleanArray("modes", modes);
		
		super.onSaveInstanceState(outState);
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(intent.getExtras() != null){
		    	if(intent.getExtras().containsKey("Object")){
		    		ParserPackages parsed = helper.messageParser(intent.getStringExtra("Object"));
		    		
		    		if(parsed.type.equals("car")){
		    			if(parsed.message.equals("online")){
		    				modes[0] = true;
		    				fragClients(modes);
		    			}
		    			if(parsed.message.equals("offline")){
		    				modes[0] = false;
		    				fragClients(modes);
		    			}
		    		}	
		    		Log.d(TAG,parsed.message);
		    		Toast.makeText(getApplicationContext(), parsed.origin + " send " + parsed.message, Toast.LENGTH_SHORT).show();
		    	}
	    	}
	    }
	};
	
	/*
	 * Loading Functions for the Fragments
	 */
	
	private void fragLoading(){
		
		String[] settings = new String[2];
		settings[0] = helper.getIPAddress();
		settings[1] = this.listenerPort;
		
		if (findViewById(R.id.frame_up) != null) {
            FragmentLoading frag = new FragmentLoading();
            Bundle b = new Bundle();
            b.putStringArray("settings", settings);
            frag.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_up, frag).commit();
        }
	}
	
	private void fragClients(boolean[] modes){
		if (findViewById(R.id.frame_down) != null) {
            FragmentClients frag = new FragmentClients();
            Bundle b = new Bundle();
            b.putBooleanArray("modes", modes);
            frag.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_down, frag).commit();
        }
	}
	
	private void fragLogo(){
		if (findViewById(R.id.frame_up) != null) {
            FragmentLogo frag = new FragmentLogo();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_up, frag).commit();
        }
	}
	
	private void fragTCPStart(){
		if (findViewById(R.id.frame_down) != null) {
            FragmentStartServer frag = new FragmentStartServer();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_down, frag).commit();
        }
	}
	
	/*
	 * @see android.app.Activity#onResume() 	
	 * Bind to the TCP Server Service
	 */
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Intent intent= new Intent(this, ServiceTCP.class);
	    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("messages"));
	}	
	
	/*
	 * Handle the Service Connection	
	 */
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, 
	        IBinder binder) {
	    	ServiceTCP.TCPBinder b = (ServiceTCP.TCPBinder) binder;
	    	mService = b.getService();
	    	mBound = true;
	    }

	    public void onServiceDisconnected(ComponentName className) {
	    	mService= null;
	    	mBound = false;
	    }
	};
	
	
	/*
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if(mBound){
			unbindService(mConnection);
		}
		if (mMessageReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            mMessageReceiver = null;
        }
	}
	
	
	
	/*
	 * @see com.aad1.aad1_pro_main_app.FragmentStartServer.FragmentCommunicator#startTCPService()
	 */

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void startTCPService() {
		if(mService != null){
			Intent service = new Intent(this, ServiceTCP.class);
			service.putExtra("serverid", helper.getIPAddress());
			service.putExtra("port", this.listenerPort);
			startService(service);
			fragLoading();
			fragClients(modes);
		}
	} 	
}
