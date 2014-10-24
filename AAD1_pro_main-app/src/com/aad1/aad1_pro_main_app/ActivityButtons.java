package com.aad1.aad1_pro_main_app;

import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class ActivityButtons extends Activity {

	ServiceTCP mService;
    boolean mBound = false;
    private Helper helper = new Helper();
	
	private ImageButton btn_forward, btn_backward, btn_left, btn_right;
	RelativeLayout layout_joystick;
	ClassJoyStick js;
	private int motorLeft = 0;
	private int motorRight = 0;
	private int pwmBtnMotorLeft;
	private int pwmBtnMotorRight;
	
	private int valForward = 0;
	private int valBackward = 0;
	private int valRight = 0;
	private int valLeft = 0;
	
	private String commandLeft; // command symbol for left motor from settings						
	private String commandRight; // command symbol for right motor from settings							
	private boolean show_Debug; // show debug information (from settings)
								
	
	private String cmdSend;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buttons);
		VideoView vidView = (VideoView) findViewById(R.id.myVideo);
		String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
		Uri vidUri = Uri.parse(vidAddress);
		vidView.setVideoURI(vidUri);
		MediaController vidControl = new MediaController(this);
		vidControl.setAnchorView(vidView);
		vidView.setMediaController(vidControl);
		//vidView.start();

		//address = (String) getResources().getText(R.string.default_MAC);
		pwmBtnMotorLeft = Integer.parseInt((String) getResources().getText(
				R.string.default_pwmBtnMotorLeft));
		pwmBtnMotorRight = Integer.parseInt((String) getResources().getText(
				R.string.default_pwmBtnMotorRight));
		commandLeft = (String) getResources().getText(
				R.string.default_commandLeft);
		commandRight = (String) getResources().getText(
				R.string.default_commandRight);

		//loadPref();

		// String CameraURL =
		// "http://iris.not.iac.es/axis-cgi/mjpg/-eo.cgi?resolution=320x240"; //
		// Public MJPEG Camera for test's

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		btn_forward = (ImageButton) findViewById(R.id.move_forward);
		btn_backward = (ImageButton) findViewById(R.id.move_back);
		btn_left = (ImageButton) findViewById(R.id.move_left);
		btn_right = (ImageButton) findViewById(R.id.move_right);
		layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);

		js = new ClassJoyStick(getApplicationContext(), layout_joystick,
				R.drawable.image_button);
		js.setStickSize(100, 100);
		js.setLayoutSize(300, 300);
		js.setLayoutAlpha(200);
		js.setStickAlpha(150);
		js.setOffset(75);
		js.setMinimumDistance(10);

		layout_joystick.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				js.drawStick(arg1);
				if (arg1.getAction() == MotionEvent.ACTION_DOWN
						|| arg1.getAction() == MotionEvent.ACTION_MOVE);
				int direction = js.get8Direction();
				if(direction == ClassJoyStick.STICK_UP) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_UPRIGHT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_RIGHT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_DOWNRIGHT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_DOWN) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_DOWNLEFT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_LEFT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_UPLEFT) {
					Log.d("Direction:", ""+direction);
				} else if(direction == ClassJoyStick.STICK_NONE) {
					Log.d("Direction:", ""+direction);
				}
				return true;
			}
		});

		btn_forward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					valForward = 100;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					valForward = 0;
				}
				commandBuilder();
				return false;
			}
		});

		btn_left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					valLeft = 100;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					valLeft = 0;
				}
				commandBuilder();
				return false;
			}
		});

		btn_right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					valRight = 100;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					valRight = 0;
				}
				commandBuilder();
				return false;
			}
		});

		btn_backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					valBackward = 100;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					valBackward = 0;
				}
				commandBuilder();
				return false;
			}
		});

	}
	
	
	private void commandBuilder(){
		
		String valFW = String.valueOf(valForward);
		
		while(valFW.length() <= 2){
			valFW = "0" + valFW;
		}
		
		String valBW = String.valueOf(valBackward);
		
		while(valBW.length() <= 2){
			valBW = "0" + valBW;
		}
		
		String valR = String.valueOf(valRight);
		
		while(valR.length() <= 2){
			valR = "0" + valR;
		}
		
		
		String valL = String.valueOf(valLeft);
		
		while(valL.length() <= 2){
			valL = "0" + valL;
		}
		
		String message = valFW + valR + valBW + valL;
					     
		
		JsonObject command = helper.packageBuilder(helper.getIPAddress(), "192.168.1.7","carvalues" ,  message);
		mService.send2Server(command.toString());		
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	if(intent.getExtras() != null){
		    	if(intent.getExtras().containsKey("Object")){
		    		ParserPackages parsed = helper.messageParser(intent.getStringExtra("Object"));
		    		Toast.makeText(getApplicationContext(), parsed.origin + " send " + parsed.message, Toast.LENGTH_SHORT).show();
		    	}
	    	}
	    }
	};

	
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
}
