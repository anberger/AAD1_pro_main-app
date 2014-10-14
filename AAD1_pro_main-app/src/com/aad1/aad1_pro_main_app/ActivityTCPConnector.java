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
import android.widget.Toast;

/*
 * Activity TCPConnectionActivity 
 * This Activity gets called when the APP is launching at the first time.
 */

public class ActivityTCPConnector extends FragmentActivity implements FragmentStartServer.FragmentCommunicator {

	ServiceTCP mService;
    boolean mBound = false;
    private String listenerPort = "6000";
	private Helper helper;
	private boolean[] modes = {false,false,false};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tcp_connection);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("messages"));
		
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
	        Toast.makeText(getApplicationContext(), intent.getStringExtra("MSG"), Toast.LENGTH_SHORT).show();
	    }
	};
	
	/*
	 * Loading Functions for the Fragments
	 */
	
	private void fragLoading(){
		if (findViewById(R.id.frame_up) != null) {
            FragmentLoading frag = new FragmentLoading();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_up, frag).commit();
        }
	}
	
	@SuppressWarnings("static-access")
	private void fragClients(boolean[] modes){
		
		String[] settings = new String[2];
		settings[0] = helper.getIPAddress();
		settings[1] = this.listenerPort;
		
		if (findViewById(R.id.frame_down) != null) {
            FragmentClients frag = new FragmentClients();
            Bundle b = new Bundle();
            b.putBooleanArray("modes", modes);
            b.putStringArray("settings", settings);
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
	}
	
	/*
	 * @see com.aad1.aad1_pro_main_app.FragmentStartServer.FragmentCommunicator#startTCPService()
	 */

	@SuppressWarnings("static-access")
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