package com;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.MainFrame;


/**
 * 绘制Canvas线程，每个设备都有一个;包含整个显示ｃａｎｖａｓ模块的逻辑
 * @author zhuo
 *
 */
public class DrawImageThread extends Thread{
	private String serial;
	private int port;
	private SelfParse parser;
	private BufferedImage image;
	private DrawCanvas drawArea;
	private Image offScreenImage;	//双缓冲解决图像闪烁
	
	public DrawImageThread(String serial, int port, JPanel parPanel){
		this.serial = serial;
		this.port = port;
		
		this.drawArea = new DrawCanvas();
		this.drawArea.setPreferredSize(new Dimension(MainFrame.AREA_WIDTH, MainFrame.AREA_HEIGHT));
		parPanel.setLayout(new BorderLayout());
		parPanel.add(this.drawArea, BorderLayout.CENTER);
		JPanel jpBotinfo = new JPanel();
		jpBotinfo.setLayout(new GridLayout(2,1));
		JLabel jlPort = new JLabel("pc端口号："+new Integer(this.port).toString(), JLabel.LEFT);
		jpBotinfo.add(jlPort);
		JLabel jlSerial = new JLabel("设备号："+this.serial, JLabel.LEFT);
		jpBotinfo.add(jlSerial);
		parPanel.add(jpBotinfo, BorderLayout.SOUTH);
	
		parPanel.setBackground(new Color(255,255,255));
		//启动adb连接
		this.parser = new SelfParse(this.serial, this.port);
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.parser.start();	//在绘制线程中启动解析线程	
		try {
			while(true){
				this.image = this.parser.getFrame();		//获取ｐａｒｓｅｒ中解析出来的帧
				this.drawArea.repaint();
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
	}
	
	class DrawCanvas extends Canvas{
		@Override
		public void update(Graphics g) {
			// TODO Auto-generated method stub
			if(offScreenImage == null)
				offScreenImage = this.createImage(MainFrame.AREA_WIDTH, MainFrame.AREA_HEIGHT);
			Graphics gImage = offScreenImage.getGraphics();
			paint(gImage);
			g.drawImage(offScreenImage, 0, 0, null);
		}

		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			g.drawImage(image, 0, 0, null);
		}
	}
}