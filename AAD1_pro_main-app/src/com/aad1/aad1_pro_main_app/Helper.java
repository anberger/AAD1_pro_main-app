package com.aad1.aad1_pro_main_app;

import android.os.Bundle;

public class Helper {
	public boolean containsCheck(Bundle b, String[] list){
		
		for(int i=0;i<list.length;i++){
			if(!b.containsKey(list[i].toString())){
				return false;
			}
		}
		return true;
	}
}
