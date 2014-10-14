package com.aad1.aad1_pro_main_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentClients extends Fragment {
	
	private ImageView imgCar = null;
	private ImageView imgVideo = null;
	private ImageView imgGps = null;
	
	private TextView txtPort = null;
	private TextView txtIP = null;
	
	private View view = null;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_clients,container,false);
		
		txtPort = (TextView) view.findViewById(R.id.txt_port);
		txtIP = (TextView) view.findViewById(R.id.txt_ip);
		
		
		Bundle b = getArguments();
	
		if(b != null){
			if(b.containsKey("modes")){
				setImages(b.getBooleanArray("modes"));
			}
			if(b.containsKey("settings")){
				txtIP.setText(b.getStringArray("settings")[0].toString());
				txtPort.setText(b.getStringArray("settings")[1].toString());
			}
		}
		
		return view;
	}
	
	private void setImages(boolean[] modes){
		imgCar = (ImageView) view.findViewById(R.id.img_car);
		imgVideo = (ImageView) view.findViewById(R.id.img_video);
		imgGps = (ImageView) view.findViewById(R.id.img_gps);
		
		if(modes[0]) imgCar.setImageResource(R.drawable.icons_startup_car);
		else imgCar.setImageResource(R.drawable.icons_startup_car_offline);
		
		if(modes[1]) imgVideo.setImageResource(R.drawable.icons_startup_video);
		else imgVideo.setImageResource(R.drawable.icons_startup_video_offline);
		
		if(modes[2]) imgGps.setImageResource(R.drawable.icons_startup_gps);
		else imgGps.setImageResource(R.drawable.icons_startup_gps_offline);
	}
}
