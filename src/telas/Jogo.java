package telas;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import objetos.Bloco;
import objetos.Jogador;
import framework.InputTeclado;
import framework.ObjectId;
import framework.Objeto_Jogo;
import framework.Texturas;
import main.Main;

import javax.swing.JButton;
public class Jogo extends Canvas implements Runnable {
	private static final long serialVersionUID = -8704436632459519948L;
	private boolean JogoRodando = false;
	private Thread thread;
	public static int LARG, ALT;
	private BufferedImage level = null,nuvem=null;
	 
	// objeto
	Controlador controlador;
	Camera cam;
	static Texturas tex;	
	Random rand = new Random();

	private void init() {
		LARG = getWidth();    
		ALT = getHeight();
		tex = new Texturas();
		BufferedImageLoader loader = new BufferedImageLoader();		
		level = loader.loadImage("/level.png");//carrega o level
		 //nuvem = loader.loadImage("/nuvem.png");
		//explosao = loader.loadImage("/explosao.gif");
		controlador = new Controlador();
		cam = new Camera(0,0);		
		LoadImageLevel(level);		
		this.addKeyListener(new InputTeclado(controlador));
	}

	
	 
	 
		public void actionPerformed(ActionEvent e) {
			Main m = new Main();	
		}
	
	
	public synchronized void start() {
		if (JogoRodando) {
			return;
		}
		JogoRodando = true;
		thread = new Thread(this);
		thread.start();
	}
	
	
	public void run() {
		init();
		long ultimoTempo = System.nanoTime();
		double tick = 60;
		double ns = 190000000 / tick;
		double delta = 0;
		long tempo = System.currentTimeMillis();
		int atualizacoes = 0;
		int frames = 0;
		while (JogoRodando) {
			long agoraT = System.nanoTime();
			delta += (agoraT - ultimoTempo) / ns;
			ultimoTempo = agoraT;
			while (delta >= 1) {
				tick();
				atualizacoes++;
				delta--;
			}
			try {
				render();
			} catch (FontFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frames++;
			if (System.currentTimeMillis() - tempo > 1000) {
				tempo += 1000;
				//System.out.println("FPS: " + frames + " Tick: " + atualizacoes);
				frames = 0;
				atualizacoes = 0;
			}
		}
	}

	private void tick() {
		controlador.tick();
		for (int i = 0; i < controlador.objeto.size(); i++) {
			if(controlador.objeto.get(i).getId()==ObjectId.Player){
				cam.tick(controlador.objeto.get(i));
			}
		}
	}

	// imagem/fundo/grafico
	private void render() throws FontFormatException, IOException {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);// basic 3 windows
			return;
		}		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D)g; // camera
		// ------------ desenha aqui --------------------
		g.setColor(new Color(25,191,224)); // cor de fundo
		
		g.fillRect(0, 0, LARG, ALT);
		g.setColor(new Color(97,38,176));		
		Font newFont = Font.createFont(Font.TRUETYPE_FONT, Jogo.class.getResourceAsStream("/bits.ttf")).deriveFont(50f);
		g.setFont(newFont);
		g.drawString("UniGame", 470, 100);	
		//g2d.translate(cam.getX()+150,cam.getY()); // inicio da camera
		g.setColor(Color.yellow);
		g.fillOval(1100, 20, 40, 40);
		controlador.render(g);	
		//g2d.translate(-cam.getX()+100,-cam.getY()); // fim da camera
		JButton btnOk = new JButton("Ok");		
		btnOk.setPreferredSize(new Dimension(30, 50));
		btnOk.setVisible(true);		
		g.dispose();
		bs.show();
	}

	private void LoadImageLevel(BufferedImage image){
		int w =image.getWidth();
		int h = image.getHeight();
		//System.out.println("largura, altura: "+w+", "+h);
		
		for (int xx = 0; xx < h; xx++) {
			for (int yy = 0; yy < w; yy++) {
				int pixel = image.getRGB(xx, yy);
				int red = (pixel>>16)&0xff;
				int green = (pixel>>8)&0xff;
				int blue = (pixel)&0xff;
				if(red==255 && green ==255 && blue ==255){
					controlador.addObject(new Bloco(xx*32,yy*32,0, ObjectId.Bloco));
				}
				if(red==50 && green ==50 && blue ==50){
					//System.out.println("cria Objeto");
					controlador.addObject(new Jogador(xx*32,yy*32,controlador, ObjectId.Player));
				}if(red==200 && green ==200 && blue ==200){
					//System.out.println("cria Objeto");
				//	controlador.addObject(new Jogador(xx*32,yy*32,controlador, ObjectId.Player));
				}				
			}			
		}
	}
	
	public static Texturas getInstance(){
		return tex;
	}
	
	public static void main(String args[]) {
		new Janela(1200,650, "Game Unifil - Grupo UniGame", new Jogo());
	}
}
