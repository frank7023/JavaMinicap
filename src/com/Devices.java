package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Devices {
	public static boolean checkFlag = true;
	public static HashMap<String, Thread> devices = new HashMap<String, Thread>();
	public static ArrayList<String> listArr;
	
//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		try {
//			while(checkFlag){
//				getDevices();
//				Thread.sleep(1000);
//			}
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public void getDevices(){
		listArr = Util.runCommand(Util.ADB+" devices");
		for(int i=1; i<listArr.size()-1; i++){		//第一行和最后一行的回车要去掉
			String st = listArr.get(i);
			String[] tp = st.split("\\s+");			//分开多个空格
//			System.out.println(tp[0]+"-------"+tp[1]);
			if(devices.containsKey(tp[0]))
				continue;
			devices.put(tp[0], null);
		}
	} 
}
