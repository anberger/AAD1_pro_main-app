package com.aad1.aad1_pro_main_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentLoading extends Fragment {
	
	private TextView txtPort = null;
	private TextView txtIP = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_loading,container,false);
		
		txtPort = (TextView) view.findViewById(R.id.txt_port);
		txtIP = (TextView) view.findViewById(R.id.txt_ip);
		
		Bundle b = getArguments();
		
		if(b != null){
			if(b.containsKey("settings")){
				txtIP.setText(b.getStringArray("settings")[0].toString());
				txtPort.setText(b.getStringArray("settings")[1].toString());
			}
		}
		
		return view;
	}
}
