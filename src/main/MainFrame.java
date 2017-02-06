package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.Devices;
import com.DrawImageThread;
import com.SelfParse;
import com.WrapLayout;

public class MainFrame {
	public static final int FRAME_WIDTH = 1000;
	public static final int FRAME_HEIGHT = 600;
	public static final int AREA_WIDTH = 180;
	public static final int AREA_HEIGHT = 320;
	public static final int ROW_DEVICE_NUM = 5;

	private JFrame f;
	private JSplitPane jsp;
	private JPanel jpDevice,jpControl;
	private JScrollPane jspDevice;
	private JButton jbSendZone;
	
	public static void main(String[] str){
		//启动设备数量轮询
		new Devices().getDevices();
		//启动窗口
		new MainFrame().init();
	}
	
	public void init(){
		JFrame.setDefaultLookAndFeelDecorated(true);
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		f.setTitle("群控");       
		f.setResizable(true);   
		f.setLocationRelativeTo(null);
		
		jpControl = new JPanel();
		jbSendZone = new JButton("发送朋友圈");
		jpControl.add(jbSendZone);
		
		jpDevice = new JPanel();
//		jpDevice.setLayout(new FlowLayout(FlowLayout.LEADING, 20, 5));
		jpDevice.setLayout(new WrapLayout(WrapLayout.LEFT));
		Iterator<Entry<String, Thread>> iter = Devices.devices.entrySet().iterator();
		int port = 1313, count=0;
		while(iter.hasNext()){
			Map.Entry<String, Thread> entry = (Map.Entry<String, Thread>)iter.next();
			String serial = entry.getKey();
			JPanel jp = new JPanel();
			DrawImageThread dit = new DrawImageThread(serial, port++, jp);
			dit.start();			//启动绘制线程
			entry.setValue(dit);	//在device　list中保存线程句柄
			
			count++;
			jpDevice.add(jp);
		}
		jspDevice = new JScrollPane(jpDevice);
		jspDevice.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
		jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jpControl, jspDevice);
		jsp.setDividerLocation(0.3);
		jsp.setDividerSize(10);
		jsp.setOneTouchExpandable(true);
		f.add(jsp);
		
		f.pack();
		f.setVisible(true);
	}
	

}
