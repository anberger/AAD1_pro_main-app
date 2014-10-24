package com.aad1.aad1_pro_main_app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class ActivityButtons extends Activity {

	private ImageButton btn_forward, btn_backward, btn_left, btn_right;
	RelativeLayout layout_joystick;
	ClassJoyStick js;
	private int motorLeft = 0;
	private int motorRight = 0;
	private String address; // MAC-address from settings 
	private int pwmBtnMotorLeft;
	private int pwmBtnMotorRight;
	
	
	private String commandLeft; // command symbol for left motor from settings
								
	private String commandRight; // command symbol for right motor from settings
									
	private String commandHorn; // command symbol for optional command from
								// settings (for example - horn) 
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
		vidView.start();

		//address = (String) getResources().getText(R.string.default_MAC);
		pwmBtnMotorLeft = Integer.parseInt((String) getResources().getText(
				R.string.default_pwmBtnMotorLeft));
		pwmBtnMotorRight = Integer.parseInt((String) getResources().getText(
				R.string.default_pwmBtnMotorRight));
		commandLeft = (String) getResources().getText(
				R.string.default_commandLeft);
		commandRight = (String) getResources().getText(
				R.string.default_commandRight);
		commandHorn = (String) getResources().getText(
				R.string.default_commandHorn);

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
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					motorLeft = pwmBtnMotorLeft;
					motorRight = pwmBtnMotorRight;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);
					Log.d("Direction:", ""+cmdSend);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					motorLeft = 0;
					motorRight = 0;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);
					Log.d("Direction:", ""+cmdSend);
				}
				return false;
			}
		});

		btn_left.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					motorLeft = -pwmBtnMotorLeft;
					motorRight = pwmBtnMotorRight;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					motorLeft = 0;
					motorRight = 0;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);
					Log.d("Direction:", ""+cmdSend);
				}
				return false;
			}
		});

		btn_right.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					motorLeft = pwmBtnMotorLeft;
					motorRight = -pwmBtnMotorRight;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					motorLeft = 0;
					motorRight = 0;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);
					Log.d("Direction:", ""+cmdSend);
				}
				return false;
			}
		});

		btn_backward.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					motorLeft = -pwmBtnMotorLeft;
					motorRight = -pwmBtnMotorRight;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					motorLeft = 0;
					motorRight = 0;
					cmdSend = String.valueOf(commandLeft + motorLeft + "\r"
							+ commandRight + motorRight + "\r");
					ShowTextDebug(cmdSend);
					Log.d("Direction:", ""+cmdSend);
				}
				return false;
			}
		});

	}

	private void ShowTextDebug(String txtDebug) {
		TextView textCmdSend = (TextView) findViewById(R.id.textViewCmdSend);
		if (show_Debug) {
			textCmdSend.setText(String.valueOf("Send:" + txtDebug));
		} else {
			textCmdSend.setText("");
		}
	}

	
	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		
	}
}
