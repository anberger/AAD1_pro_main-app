package com.aad1.aad1_pro_main_app;

import android.app.Activity;
import android.os.Bundle;

public class ActivitySetPreference extends Activity {
	

		@Override
		protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    
		getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new FragmentPrefs()).commit();
		
		getFragmentManager().beginTransaction().replace(android.R.id.content,
	            new FragmentHelp()).commit();
		}
	}