//package javaFlap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.imageio.ImageIO;

public class FlappyB implements ActionListener, MouseListener, KeyListener{

	public static FlappyB flapz;
	public final int WIDTH=800, HEIGHT=800;
	public Renderer renderer;
	public Rectangle bird;
	public ArrayList<Rectangle> columns;
	public int ticks,yMotion,score;
	public boolean gameOver, started;
	public Random rand;
	public BufferedImage brd_img,bckg_img,fg_img,pipeN_img,pipeS_img;
	public int counter=0;
	public float speed=10;
	public FontMetrics fm;
	
	public FlappyB() {
		JFrame f = new JFrame();
		Timer timer = new Timer(20,this);
		renderer=new Renderer();
		rand=new Random();
		loadImgs();
		
		f.add(renderer);
		f.setTitle("Flappy bird!");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addMouseListener(this);
		f.addKeyListener(this);
		f.setResizable(true);
		f.setVisible(true);
		f.setMinimumSize(new Dimension(WIDTH,HEIGHT));
		f.setMaximumSize(new Dimension(WIDTH,HEIGHT));
		f.pack();
		
		bird=new Rectangle(WIDTH/2-210,HEIGHT/2-10,20,20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		timer.start();
	}

	private void loadImgs(){
		try{
			brd_img=ImageIO.read(new File("bird.png"));
			bckg_img=ImageIO.read(new File("bg1.jpg"));
			fg_img=ImageIO.read(new File("fg.png"));
			pipeN_img=ImageIO.read(new File("pipeNorth.png"));
			pipeS_img=ImageIO.read(new File("pipeSouth.png"));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void addColumn(boolean b) {
		int space=300;
		int width=100;
		int height=50+rand.nextInt(300);
		
		if(b){
			columns.add(new Rectangle(WIDTH+width+columns.size()*300,
					HEIGHT-height-120,width,height));
			columns.add(new Rectangle(WIDTH+width+(columns.size()-1)*300,0,
					width,HEIGHT-height-space));
		}
		else{
			columns.add(new Rectangle(columns.get(columns.size()-1).x+600,
					HEIGHT-height-120,width,height));
			columns.add(new Rectangle(columns.get(columns.size()-1).x,0,
					width,HEIGHT-height-space));
		}
	}
	
	public void paintColumn(Graphics g,Rectangle col) {
		if(counter%50==0&&!gameOver)
			speed+=.1;
		//g.setColor(Color.green.darker());
		//g.fillRect(col.x, col.y, col.width, col.height);
		if(col.y<50)
		g.drawImage(pipeN_img,col.x,col.y,col.width,col.height,null);
		else
		g.drawImage(pipeS_img,col.x,col.y,col.width,col.height,null);
		//g.drawImage(bckg_img,col.x,col.y,null);
	}
	
	public void jump() {
		if(gameOver) {
			bird=new Rectangle(WIDTH/2-50,HEIGHT/2-10,20,20);
			columns.clear();
			yMotion=0;
			score=0;
			
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			speed=10;
			counter=0;
			gameOver=false;
		}
		
		if(!started) {
			speed=10;
			counter=0;
			started=true;
			//codigo de salto
		}else if(!gameOver){
			if(yMotion>0) {
				yMotion=0;
			}
			yMotion-=10;
		}
	}

	public void keyTyped(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE){
			jump();
		}
	}

	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_SPACE) {
			jump();
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {
		jump();
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {
		ticks++;
		
		if(started && !gameOver) {
			for(int i=0;i<columns.size();i++) {
				Rectangle column= columns.get(i);
				
				column.x-=speed;
			}
			
			if(ticks%2==0&&yMotion<15)
				yMotion+=2;
				
			for(int i=0;i<columns.size();i++) {
				//screen boundaries
				Rectangle column = columns.get(i);
				
				if(column.x+column.width<0) {
					columns.remove(column);
					
					if(column.y==0)
						addColumn(false);
				}
			}
			bird.y+=yMotion;
			
			for(Rectangle col:columns) {
				if(col.y==0 && bird.x+bird.width/2>col.x+col.width/2-10 
						&& bird.x+bird.width/2<col.x+col.width/2+10) {
					score++;
				}
				//collision
				if(col.intersects(bird)) {
					gameOver=true;
					
					if(bird.x<=col.x) {
						bird.x=col.x-bird.width;
					}else {
						
						if(col.y!=0) {
							bird.y=col.y-bird.height;
						}else if(bird.y<col.height) {
							bird.y=col.height;
						}
					}
				}
			}
			
			if(bird.y>HEIGHT-120||bird.y<0)
				gameOver=true;
			
			if(bird.y+yMotion>=HEIGHT-120) {
				bird.y=HEIGHT-120-bird.height;
				gameOver=true;
			}
		}
		
		renderer.repaint();
	}
	
	public void repaint(Graphics g) {
		//Backgrd
		g.setColor(Color.cyan);
		//g.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(bckg_img,0,0,null);
		g.drawImage(bckg_img,bckg_img.getWidth(),0,null);
		
		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT-120, WIDTH, 120);
		//Tubos
		g.setColor(Color.green);
		//g.fillRect(0,HEIGHT-120,WIDTH,20);
		g.drawImage(fg_img,0,HEIGHT-fg_img.getHeight()-40,fg_img.getWidth()+100,fg_img.getHeight(),null);
		g.drawImage(fg_img,fg_img.getWidth(),HEIGHT-fg_img.getHeight()-40,fg_img.getWidth()+100,fg_img.getHeight(),null);
		//bird
		g.setColor(Color.yellow);
		//g.fillRect(bird.x, bird.y, bird.width, bird.height);
		g.drawImage(brd_img,bird.x, bird.y, bird.width, bird.height,null);
		
		fm=g.getFontMetrics();
		g.setFont(new Font("Arial",1,40));
		int strW = fm.stringWidth(String.valueOf(counter));
		g.drawString(""+counter,WIDTH-strW*4,30);

		for(Rectangle col:columns)
			paintColumn(g,col);
			
		g.setColor(Color.ORANGE);
		g.setFont(new Font("Arial",1 ,100));
		fm=g.getFontMetrics();
		
		if(!started){
			int strWidth = fm.stringWidth("START");
			g.drawString("START", WIDTH/2-strWidth/2, HEIGHT/2-50);
		}
		if(gameOver){
			int strWidth = fm.stringWidth("GameOver");
			g.drawString("GameOver", WIDTH/2-strWidth/2, HEIGHT/2-50);
			strWidth = fm.stringWidth(String.valueOf(score));
			g.drawString(String.valueOf(score),WIDTH/2-strWidth/2,100);
		}


		if(!gameOver&&started)
			counter++;
	}
	
		public static void main(String[] args) {
			flapz = new FlappyB();
		}
	}
