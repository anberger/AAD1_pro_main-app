package com.aad1.aad1_pro_main_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FragmentClients extends Fragment {
	
	private ImageView imgCar = null;
	private ImageView imgVideo = null;
	private ImageView imgGps = null;
	private ImageButton imgBtn = null;

	private View view = null;
	private boolean[] modes = null;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_clients,container,false);
		
		imgBtn = (ImageButton)view.findViewById(R.id.btn_app_start);
		
		imgBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				frag.startTCPService();
				Intent intent = new Intent(getActivity(), ActivityMain.class);
				startActivity(intent);
			}
		});
		
		
		Bundle b = getArguments();
	
		if(b != null){
			if(b.containsKey("modes")){
				modes = b.getBooleanArray("modes");
				updateGUI();
			}
		}
		
		return view;
	}
	
	private void updateGUI(){
		imgCar = (ImageView) view.findViewById(R.id.img_car);
		imgVideo = (ImageView) view.findViewById(R.id.img_video);
		imgGps = (ImageView) view.findViewById(R.id.img_gps);
		
		if(modes[0]) {
			imgCar.setImageResource(R.drawable.icons_startup_car);
			imgBtn.setVisibility(View.VISIBLE);
		}
		else {
			imgCar.setImageResource(R.drawable.icons_startup_car_offline);
			imgBtn.setVisibility(View.GONE);
		}
		
		if(modes[1]) {
			imgVideo.setImageResource(R.drawable.icons_startup_video);
		}
		else {
			imgVideo.setImageResource(R.drawable.icons_startup_video_offline);
		}
		
		if(modes[2]) {
			imgGps.setImageResource(R.drawable.icons_startup_gps);
		}
		else {
			imgGps.setImageResource(R.drawable.icons_startup_gps_offline);
		}
	}
}
