package com;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javax.imageio.ImageIO;

import main.MainFrame;

public class SelfParse extends Thread{
	public static final int MAX_FRAME = 50000;
	private BufferedImage bi1;
	private String serial;
	private int port;

	public SelfParse(String serial, int port){			//startConnect函数一直没有结束，所以SelfParse实例一直为null
		this.bi1 = new BufferedImage(MainFrame.AREA_WIDTH, MainFrame.AREA_HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.serial = serial;
		this.port = port;
	}
	
	@Override
	public void run() {			//startConnect一直执行不完，所以需要单独一个线程来处理；否则会阻塞主线程
		// TODO Auto-generated method stub
		startConnect();
	}

	/**
	 * 拷贝数组
	 * 返回下次该插入的index
	 * @param sour
	 * @param dest
	 * @param sourSt　原数组开始index
	 * @param destSt 目标数组开始index
	 */
	private int copyByteArray(byte[] sour, byte[] dest, int sourSt, int sourEn, int destSt){
		int k=0;
//		System.out.println(sour.length+"   "+dest.length+"    "+sourSt+"    "+sourEn+"    "+destSt);
		for(int i=sourSt; i<=sourEn; i++){
			if(destSt+k >= dest.length) continue;
			dest[destSt+k] = sour[i];
			k++;
		}
		return destSt+k;
	}
	
	private void startConnect(){
		Socket socket;
		int actualFrameSize = 0;					//记录每个ｆｒａｍｅ的实际大小
		byte[] frameArr = new byte[MAX_FRAME];
		boolean HEAD_ONCE_FLAG = true;
		
		try {
			//create a local forward，把minicap中的数据转发到pc机1313接口
			Util.runCommand(Util.ADB+" -s "+this.serial+" forward tcp:"+this.port+" localabstract:minicap");
			//start minicap
			Util.runCommand(Util.ADB+" -s "+this.serial+" shell LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap -P 1080x1920@"+MainFrame.AREA_WIDTH+"x"+MainFrame.AREA_HEIGHT+"/0");
			
			//创建socket连接
			socket = new Socket("127.0.0.1", this.port);
			System.out.println(this.port+"端口连接成功");
			InputStream is = socket.getInputStream();
			byte[] bytes = new byte[Util.FRAME_SIZE];
			byte[] fourbts = new byte[4];
			int hasread = 0, destSt = 0;
			while((hasread = is.read(bytes)) > 0){
				if(HEAD_ONCE_FLAG){		//第一次只接收header,bytes[24]为空
					HEAD_ONCE_FLAG = false;
				} else {			//不是第一次发送，就不含有ｈｅａｄｅｒ，头四个字节直接表示Frame size
					if(destSt <= 0){
						fourbts[0] = bytes[3];
						fourbts[1] = bytes[2];
						fourbts[2] = bytes[1];
						fourbts[3] = bytes[0];
						actualFrameSize = Util.bytes2int(fourbts);
						if(actualFrameSize > MAX_FRAME || actualFrameSize <= 0)
							continue;
//						System.out.println("frame size: " + actualFrameSize);
						destSt = this.copyByteArray(bytes, frameArr, 4, hasread-1, 0);
						
					} else {
						destSt = this.copyByteArray(bytes, frameArr, 0, hasread-1, destSt);
						if(destSt >= actualFrameSize){		//已经记录完成一帧
							ByteArrayInputStream bais = new ByteArrayInputStream(frameArr, 0, actualFrameSize-1);      
							this.bi1 =ImageIO.read(bais);
							bais.close();
							destSt = 0;
						}
					}
//					System.out.println(actualFrameSize +"      "+ hasread + "     " + destSt);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getFrame(){
		return this.bi1;
	}
}
