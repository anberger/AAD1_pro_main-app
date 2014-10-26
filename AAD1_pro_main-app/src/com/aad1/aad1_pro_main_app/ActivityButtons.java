package com.aad1.aad1_pro_main_app;

import com.google.gson.JsonObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class ActivityButtons extends Activity {

	ServiceTCP mService;
    boolean mBound = false;
    private Helper helper = new Helper();
	
	private ImageButton btn_forward, btn_backward, btn_left, btn_right;
	RelativeLayout layout_joystick;
	ImageView img;
	ClassJoyStick js;
	
	private int valForward = 0;
	private int valBackward = 0;
	private int valRight = 0;
	private int valLeft = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buttons);

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
		js.setLayoutSize(600, 600);
		js.setLayoutAlpha(200);
		js.setStickAlpha(150);
		js.setOffset(75);
		js.setMinimumDistance(10);

		layout_joystick.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				js.drawStick(arg1);
				
				if (arg1.getAction() == MotionEvent.ACTION_DOWN
						|| arg1.getAction() == MotionEvent.ACTION_MOVE){
					
					float valX = arg1.getX();
					float valY = arg1.getY();
					
					valX = (float) ((valX - js.getLayoutWidth()/2) / 2.5);
					valY = (float) ((valY - js.getLayoutHeight()/2) / 2.5);
					
					if(valX < -100)	valX = -100;
					else if(valX > 100) valX = 100;
					
					if(valY < -100)	valY = -100;
					else if(valY > 100) valY = 100;
					
					if(valX < 0){
						valRight = 0;
						valLeft = (int) (valX * -1);
					} // links
					else {
						valLeft = 0;
						valRight = (int) valX;
					} // rechts
					
					
					if(valY < 0){
						valForward = (int) (valY * -1);
						valBackward = 0;
					} // unten
					else {
						valForward = 0;
						valBackward = (int) valY;
					} // oben
					
					commandBuilder();
					
				}
				else {
					valLeft = 0;
					valRight = 0;
					valForward = 0;
					valBackward = 0;
					commandBuilder();
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
			
		ClassDeviceState device = mService.getDeviceStates("car");
		if(device != null){
			if(device.getDeviceState()){
				JsonObject command = helper.packageBuilder(helper.getIPAddress(), device.getDeviceIP(),"carvalues",message);
				mService.send2Server(command.toString());	
			}
			else {
				Toast.makeText(getApplicationContext(), "Device is offline", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Bundle b = intent.getExtras();
	    	if(b != null){
		    	if(b.containsKey("Object")){
		    		ParserPackages parsed = helper.messageParser(intent.getStringExtra("Object"));
		    		//Toast.makeText(getApplicationContext(), parsed.origin + " send " + parsed.message, Toast.LENGTH_SHORT).show();
		    	}
		    	
		    	if(b.containsKey("Image")){
		    		byte[] img = intent.getByteArrayExtra("Image");
		    		setImage(img);
		    		
		    		
		    	}
	    	}
	    }
	};
	
	private void setImage(byte[] image){
		
	    Bitmap b = BitmapFactory.decodeByteArray(image,0,image.length);   
	    
		//img.setImageBitmap(b);
		
		Drawable dr = new BitmapDrawable(b);
		RelativeLayout r;
		r = (RelativeLayout) findViewById(R.id.layout);
		r.setBackgroundDrawable(dr);
		
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
}
