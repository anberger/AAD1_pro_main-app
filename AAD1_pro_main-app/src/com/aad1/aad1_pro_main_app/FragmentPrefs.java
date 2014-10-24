package com.aad1.aad1_pro_main_app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class FragmentPrefs extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
  
		// Load the preferences from an XML resource
        
		addPreferencesFromResource(R.xml.pref);
	}
}