package com.aad1.aad1_pro_main_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FragmentStartServer extends Fragment {
	
	private ImageButton imgBtn = null;
	public FragmentCommunicator frag;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_start_server,container,false);
		
		imgBtn = (ImageButton)view.findViewById(R.id.btn_tcp_start);
		
		imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				frag.startTCPService();
			}
		});
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    try {
	    	frag = (FragmentCommunicator) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement FragmentCommunicator");
	    }
	}

	// Interface to communicate with the TCP Connection Activity
	public interface FragmentCommunicator  {
		public void startTCPService();
	}	
}
