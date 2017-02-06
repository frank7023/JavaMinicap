package com;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static final int FRAME_SIZE = 1024;		//最大４字节无符号整数4294967295L,但一张图片实际应该不会这么大
	public static final String ADB = "/home/zhuo/Android/Sdk/platform-tools/adb";
	
	public static ArrayList<String> runCommand(String command){
		System.out.println(command);
		String line = "null";
		ArrayList<String> linelist = new ArrayList<String>();
		try {
			Process proc = Runtime.getRuntime().exec(command);
			int i = proc.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while((line=buf.readLine())!=null){
				linelist.add(line);
//				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return linelist;
	}
	
	public static int byte2int(byte bt){
		int result = 0;
		result = 0x000000ff & bt;
		return result;
	}
	
	public static int bytes2int(byte[] bts){	//int最高位为负数还是会出现负数的情况，但是协议中表示的值一般不会超过２＾３１－１＝2147483647
		int a, b, c, d;
		int result = 0;
		if(bts.length==4){
			a = (bts[0] & 0xff) << 24; 
			b = (bts[1] & 0xff) << 16;
			c = (bts[2] & 0xff) << 8;
			d = bts[3] & 0xff;
			result = a | b | c | d;
		}
		return result;
	}
	
	public static String getBinaryFromByte(byte b)	//-2 原码:10000010　反码(原码除符号位求反)11111101  补码(反码+1)11111110
    {
        String result ="";
        byte a = b; ;
        for (int i = 0; i < 8; i++)
        {
         byte c=a;
         a=(byte)(a>>1);
         a=(byte)(a<<1);
         if(a==c){
          result="0"+result;
         }else{
          result="1"+result;
         }
         a=(byte)(a>>1);
        }
        return result;
    }
	
}
