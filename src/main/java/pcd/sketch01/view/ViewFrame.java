package pcd.sketch01.view;

import pcd.sketch01.controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import pcd.sketch01.model.PlayerId;

public class ViewFrame extends JFrame {

    private static final double KICK_STRENGTH = 1.5;
    private VisualiserPanel panel;
    private ViewModel model;
    private RenderSynch sync;
    private ActiveController controller;
    
    public ViewFrame(ViewModel model, ActiveController controller, int w, int h){
    	this.model = model;
    	this.sync = new RenderSynch();
        this.controller = controller;
    	setTitle("Sketch 03");
        setSize(w,h + 25);
        setResizable(false);
        panel = new VisualiserPanel(w,h);
        getContentPane().add(panel);
        addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
        });
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        controller.notifyNewCmd(new MoveUpCmd());
                        break;
                    case KeyEvent.VK_DOWN:
                        controller.notifyNewCmd(new MoveDownCmd());
                        break;
                    case KeyEvent.VK_RIGHT:
                        controller.notifyNewCmd(new MoveRightCmd());
                        break;
                    case KeyEvent.VK_LEFT:
                        controller.notifyNewCmd(new MoveLeftCmd());
                        break;
                }
            }
        });
    }


    public void render(){
		long nf = sync.nextFrameToRender();
        panel.repaint();
		try {
			sync.waitForFrameRendered(nf);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
    }

    public class VisualiserPanel extends JPanel {
        private int ox;
        private int oy;
        private int delta;
        
        public VisualiserPanel(int w, int h){
            setSize(w,h + 25);
            ox = w/2;
            oy = h/2;
            delta = Math.min(ox, oy);
        }

        public void paint(Graphics g){
    		Graphics2D g2 = (Graphics2D) g;
    		
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    		          RenderingHints.VALUE_ANTIALIAS_ON);
    		g2.setRenderingHint(RenderingHints.KEY_RENDERING,
    		          RenderingHints.VALUE_RENDER_QUALITY);
    		g2.clearRect(0,0,this.getWidth(),this.getHeight());
            
    		g2.setColor(Color.LIGHT_GRAY);
		    g2.setStroke(new BasicStroke(1));
    		g2.drawLine(ox,0,ox,oy*2);
    		g2.drawLine(0,oy,ox*2,oy);
    		g2.setColor(Color.BLACK);
    		
    		    g2.setStroke(new BasicStroke(1));
	    		for (var b: model.getBalls()) {
	    			var p = b.pos();
	            	int x0 = (int)(ox + p.x()*delta);
	                int y0 = (int)(oy - p.y()*delta);
	                int radiusX = (int)(b.radius()*delta);
	                int radiusY = (int)(b.radius()*delta);
	                g2.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
	    		}
	
    		    g2.setStroke(new BasicStroke(3));
	    		var pb = model.getPlayerBall();
	    		if (pb != null) {
					var p1 = pb.pos();
		        	int x0 = (int)(ox + p1.x()*delta);
		            int y0 = (int)(oy - p1.y()*delta);
	                int radiusX = (int)(pb.radius()*delta);
	                int radiusY = (int)(pb.radius()*delta);
	                g2.drawOval(x0 - radiusX,y0 - radiusY,radiusX*2,radiusY*2);
	    		}
                // Disegna le buche
                g2.setColor(Color.BLACK);
                for (var hole : model.getHoles()) {
                    var p = hole.pos();
                    int x0 = (int) (ox + p.x() * delta);
                    int y0 = (int) (oy - p.y() * delta);
                    int r = (int) (hole.radius() * delta);
                    g2.fillOval(x0 - r, y0 - r, r * 2, r * 2);
                }
                // disegno del punteggio
                g2.setColor(Color.BLUE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));

                g2.drawString("Human: " + model.getHumanScore(), 30, 600);
                g2.drawString("Bot: " + model.getBotScore(), getWidth() - 120, 600);

                g2.setStroke(new BasicStroke(1));
	    		g2.drawString("Num small balls: " + model.getBalls().size(), 550, 40);
	    		g2.drawString("Frame per sec: " + model.getFramePerSec(), 550, 60);

                // disegno fine partita
                if (model.isGameOver()) {
                    g2.setColor(new Color(0, 0, 0, 180)); // Overlay semi-trasparente
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 36));

                    String msg = "GAME OVER";
                    if (model.getWinner() == PlayerId.HUMAN) {
                        msg = "VICTORY! Human Wins!";
                        g2.setColor(Color.GREEN);
                    } else if (model.getWinner() == PlayerId.BOT) {
                        msg = "DEFEAT! Bot Wins!";
                        g2.setColor(Color.RED);
                    } else {
                        msg = "GAME OVER - DRAW!";
                    }

                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(msg)) / 2;
                    g2.drawString(msg, x, getHeight() / 2);
                }

	    		sync.notifyFrameRendered();
    		
        }
        
    }
}
