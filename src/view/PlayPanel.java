package view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import model.Card;

import control.SuperController;

public class PlayPanel extends JPanel implements ActionListener
{
	private SuperController superC;
	private Timer timer;
	private boolean drawCards;
	private boolean smallScreen;
	
	public PlayPanel(SuperController superC)
	{
		this.superC = superC;
		this.drawCards = superC.getDeckC().isDrawCards();
		
		
		this.timer = new Timer(500, this);		
		timer.start();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;		
		
		drawStatistics(g2);		
		drawHand(g2);
		drawBank(g2);
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		this.drawCards = superC.getDeckC().isDrawCards();
		smallScreen = (this.getHeight() < (Card.CARD_HEIGHT+10));
		repaint();
	}
	
	public void drawHand(Graphics2D g2)
	{
		if (drawCards) {
			for (int i=0; i<superC.getDeckC().getHand().size(); i++)
			{
				if (smallScreen) {					
					BufferedImage img = (BufferedImage)superC.getDeckC().getHand().getCard(i).getImage();				
					g2.drawImage(resizeImage(img, BufferedImage.TYPE_INT_RGB, 0.5, Card.CARD_WIDTH, Card.CARD_HEIGHT), 
								(i*50)+10, (int)(this.getHeight()-(Card.CARD_HEIGHT*0.5)), this);
				}
				else
					g2.drawImage(superC.getDeckC().getHand().getCard(i).getImage(), (i*100)+10, (int)(this.getHeight()-(Card.CARD_HEIGHT+10)), this);
			}
		}
	}
	
	public void drawBank(Graphics2D g2)
	{
		if (drawCards) {
			for (int i=0; i<superC.getDeckC().getBank().size(); i++)
			{
				if (smallScreen) {
					BufferedImage img = (BufferedImage)superC.getDeckC().getBank().getCard(i).getImage();				
					g2.drawImage(resizeImage(img, BufferedImage.TYPE_INT_RGB, 0.25, Card.CARD_WIDTH, Card.CARD_HEIGHT), 
								(i*25)+(this.getWidth()-125), 5, this);
				}
				else {
					BufferedImage img = (BufferedImage)superC.getDeckC().getBank().getCard(i).getImage();				
					g2.drawImage(resizeImage(img, BufferedImage.TYPE_INT_RGB, 0.5, Card.CARD_WIDTH, Card.CARD_HEIGHT), 
							(i*50)+(this.getWidth()-250), 10, this);
				}
			}
		}
	}
	
	public void drawStatistics(Graphics2D g2)
	{
		g2.drawString("Your hand:", 10, 25);
		g2.drawString("Other Player hand:", 150, 25);
		g2.drawString("Bank:", 300, 25);

		int y = 40;
		if (drawCards) {
			for (int i=0; i<superC.getDeckC().getHand().size(); i++) {		
				g2.drawString(superC.getDeckC().getHand().getCard(i).getCardStats(), 10, y);
				y+=15;
			}
			// TODO Remove this later
			y = 40;
			for (int i=0; i<superC.getDeckC().getHandOther().size(); i++) {		
				g2.drawString(superC.getDeckC().getHandOther().getCard(i).getCardStats(), 150, y);
				y+=15;
			}
			
			y = 40;
			for (int i=0; i<superC.getDeckC().getBank().size(); i++) {		
				g2.drawString(superC.getDeckC().getBank().getCard(i).getCardStats(), 300, y);
				y+=15;
			}
		}
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int type, double resize, int originalWidth, int originalHeight)
	{
		int imgWidth = (int)(originalWidth*resize);
		int imgHeight = (int)(originalHeight*resize);
		
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();
		
		return resizedImage;
	}
}
