package com.aad1.aad1_pro_main_app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMain extends Activity implements OnClickListener {

	Button btnActButtons, btnActHelp, btnActAbout;

	// Called when the activity is first created. 
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    TextView textv = (TextView) findViewById(R.id.textView1);
	    //textv.setShadowLayer(1, 3, 3, Color.GRAY);
	    
	    btnActButtons = (Button) findViewById(R.id.button_buttons);
	    btnActButtons.setOnClickListener(this);
	    
	    btnActHelp = (Button) findViewById(R.id.button_help);
	    btnActHelp.setOnClickListener(this);
	    
	    btnActAbout = (Button) findViewById(R.id.button_about);
	    btnActAbout.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {    	
	    case R.id.button_buttons:
	    	Intent intent_buttons = new Intent(this, ActivityButtons.class);
	    	startActivity(intent_buttons);

	    	Toast.makeText(getApplicationContext(),"Engines Turning On ", Toast.LENGTH_LONG).show();
	    	break;  
	    case R.id.button_help:
	    	Intent intent_help = new Intent(this, ActivityHelp.class);
	    	startActivity(intent_help);
	    	Toast.makeText(getApplicationContext(),"We Can Help You ", Toast.LENGTH_LONG).show();
	    	break;
	    case R.id.button_about:
	    	Intent intent_about = new Intent(this, ActivityAbout.class);
	    	startActivity(intent_about);
	    	Toast.makeText(getApplicationContext(),"Thats All About Our Project ", Toast.LENGTH_LONG).show();
	    	break;	
	    default:
	    	break;
	    }
	}
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  
		Intent intent = new Intent();
		intent.setClass(ActivityMain.this, ActivitySetPreference.class);
		startActivityForResult(intent, 0); 
	  
		return true;
	}
}
