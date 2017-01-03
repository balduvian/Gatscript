import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Script {
	
	BufferedImage[] glyph;
	String gi;
	int[][] gp = new int[0][0];
	
	public Script(){
		getimages();
		new Window();
		gi = "i  can  wash  all  of  your  dirty  stuff";
		gp = convert(gi);
		disp2d(gp);
	}
	
	public class Window extends JFrame{
		
		Canvas canvas;
		int car = 32;
		int off = 10;
		
		public Window(){
			setSize(640,480);
			setTitle("Gatscript");
			canvas = new Canvas();
			add(canvas);
			setVisible(true);
			addWindowListener(new WindowListener(){
				public void windowActivated(WindowEvent arg0) {
				}
				public void windowClosed(WindowEvent arg0) {	
				}
				public void windowClosing(WindowEvent arg0) {
					System.exit(0);
				}
				public void windowDeactivated(WindowEvent arg0) {
				}
				public void windowDeiconified(WindowEvent arg0) {
				}
				public void windowIconified(WindowEvent arg0) {
				}
				public void windowOpened(WindowEvent arg0) {
				}
			});
		}
		
		public class Canvas extends JPanel{
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				for(int i=0;i<gp.length;i++){
					for(int c=0;c<gp[i].length;c++){
						if(gp[i][c] != 0){
							if(c==0){
								g.drawImage(glyph[gp[i][c]-1], i*car+off, car+off, car, car, this);
							}else{
								if(gp[i][1]!=0 && gp[i][2]!=0){
									g.drawImage(glyph[gp[i][c]-1+32], i*car+off, (c-1)*(car/2)+off, car, car, this);
								}else{
									g.drawImage(glyph[gp[i][c]-1], i*car+off, (c-1)*car+off, car, car, this);
								}
							}
						}
					}
				}
				repaint();
			}
		}
	}
	
	public void getimages(){
		File f = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"glyph.png");
		BufferedImage over = null;
		try{
			over = ImageIO.read(f);
		}catch(IOException ex){}
		int w = over.getWidth()/16;
		int h = over.getHeight()/16;
		glyph = new BufferedImage[w*h];
		for(int y=0;y<h;y++){
			for(int x=0;x<w;x++){
				glyph[(y*w)+(x)] = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
				for(int yy=0;yy<16;yy++){
					for(int xx=0;xx<16;xx++){
						glyph[(y*w)+(x)].setRGB(xx, yy, over.getRGB(x*16+xx, y*16+yy));
					}
				}
			}
		}
	}
	
	public void disp2d(int[][] ar){
		for(int y=0;y<ar.length;y++){
			String temp = "";
			for(int x=0;x<ar[y].length;x++){
				temp += (ar[y][x]+" ");
			}
			System.out.println(temp);
		}
	}
	
	public String strip(String in){
		String temp = "";
		String[][] rp = {{"q","cw"},{"x","cs"},{"j","g"},{"k","c"},{"z","s"}};
		for(int i=0;i<in.length();i++){
			if(in.charAt(i)== 'u' && in.charAt(i-1) == 'q'){
				
			}else if(i == in.length()-1 && in.charAt(i) == 'e' && in.charAt(i-1) != 'e'){
				
			}else{
				temp += in.charAt(i);
			}
		}
		in = temp;
		temp = "";
		for(int i=0;i<in.length();i++){
			iter: while(true){
				for(int g=0;g<rp.length;g++){//iterate over replacements;
					if((in.charAt(i)+"").equals(rp[g][0])){//if match
						for(int n=0;n<rp[g][1].length();n++){
							temp += rp[g][1].charAt(n);
						}
						break iter;
					}
				}
				//only happening if top doesn't break
				temp += in.charAt(i);
				break iter;
			}
		}
		return temp;
	}
	
	public int[][] convert(String in){
		//preparations
		in = strip(in);
		//
		LinkedList<int[]> block = new LinkedList<int[]>();
		String co = "bcdfghlmnprstvwy";
		String[][] vg = {{"ai","ae"},{"ee","ea","ei"},{"ie"},{"ou"},{"oo","uu"},{"a"},{"e"},{"io","i"},{"o"},{"u"}};
		int[] plc = new int[2];//counter 0 is horizontal, 1 is vertical
		block.add(new int[3]);//add origin
		LinkedList<Integer> ins = new LinkedList<Integer>();//what to insert
		boolean gon;
		for(int i=0;i<in.length();i++){
			gon = false;
			ins.clear();
			iter: while(true){
				for(int g=0;g<co.length();g++){//iterate over constanents
					if(in.charAt(i) == co.charAt(g)){//if match
						ins.add(g);
						break iter;
					}
				}
				for(int g=0;g<vg.length;g++){//iterate over VOWELS (here we go)
					for(int h=0;h<vg[g].length;h++){//iterate over all possible for that vowel
						for(int b=0;b<vg[g][h].length();b++){//ierate over length of eng vowel
							if((i+b > in.length()-1) || in.charAt(i+b) != vg[g][h].charAt(b)){//now we test similarity
								break;
							}
							if(b == vg[g][h].length()-1){
								ins.add(g+co.length());
								gon = true;
								i += vg[g][h].length()-1;
								break iter;
							}
							
						}
					}
				}
				gon = true;
				break iter;
			}
			if(gon){//switching over to next line
				for(int v=0;v<ins.size();v++){
					block.get(plc[0])[0] = ins.get(v)+1;//plus 1 to distinguish 0 as null
					plc[1]++;
				}
				plc[0]++;//advance
				plc[1] = 0;//reset
				block.add(new int[3]);//add next line
			}else{
				for(int v=0;v<ins.size();v++){
					if(plc[1]+1 < 3){
						block.get(plc[0])[plc[1]+1] = ins.get(v)+1;//plus 1 to distinguish 0 as null
						plc[1]++;
					}
				}
			}
		}
		int[][] out = new int[block.size()][3];
		for(int i=0;i<block.size();i++){
			out[i] = block.get(i);
		}
		return out;
	}
	
	public static void main(String[] args) {
		new Script();

	}

}
