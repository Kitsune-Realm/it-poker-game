package model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;

import control.Images;
import control.PokerConstants;

public class Card implements PokerConstants, Comparable<Card>, Serializable
{
	private int symbol;
	private int score;
	private Image image;
	private boolean highlighted;
	private boolean flipped;
	
	public Card(int symbol, int score)
	{
		this.symbol = symbol;
		this.score = score;
		try {
			this.image = Images.get("cards/"+getSymbolName()+"-"+score+".png");
		}
		catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2, int cardIndex)
	{
		g2.drawImage(getImage(), (FRAME_WIDTH/2)-(Card.CARD_WIDTH/2) + cardIndex * 100, FRAME_HEIGHT - CARD_HEIGHT - CARD_MARGIN, null);
	}
	
	public Image getImage()
	{
		return image;
	}
	
	public String getSymbolName()
	{
		switch(symbol) {
		case 0:
			return "hearts";
		case 1:
			return "clubs";
		case 2:
			return "spades";
		case 3:
			return "diamonds";
		default:
			return "no symbol";
		}
	}

	public String getCardStats()
	{
		return getSymbolName() + "-" + getScore();
	}
	
	public int getSymbol()
	{
		return symbol;
	}

	public void setSymbol(int symbol)
	{
		this.symbol = symbol;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	@Override
	public int compareTo(Card input)
	{
		if (this.getScore() == 1) // Ace
			return 1;
		if (this.getScore() < input.getScore())
			return -1;
		else if (this.getScore() > input.getScore())
			return 1;
		else
			return 0;
	}
	
}
