package view;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.SuperController;

public class TopPanel extends JPanel
{
	private SuperController superC;
	
	private int round;
	private int pot;
	private int increase;
	private int scoreP1;
	private int scoreP2;
	
	private JLabel lRound;
	private JLabel lPot;
	private JLabel lScore;
	
	public TopPanel(SuperController superC)
	{
		this.superC = superC;
		this.round = superC.getPlayC().getRound();
		this.pot = superC.getPlayC().getPot();
		this.increase = superC.getPlayC().getIncrease();
		this.scoreP1 = superC.getPlayC().getScoreP1();
		this.scoreP2 = superC.getPlayC().getScoreP2();
		
		
		setLayout(new GridLayout(1,3));
		this.lRound = new JLabel(" Round: " + round);
		add(lRound);
		
		this.lPot = new JLabel("Pot: " + pot + " + ("+increase+")");
		add(lPot);
		
		this.lScore = new JLabel("P1: " + scoreP1 + " / P2: " + scoreP2);		
		add(lScore);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
	}
	
	public void updateTopPanel()
	{
		lRound.setText(" Round: " + superC.getPlayC().getRound());
		lPot.setText("Pot: " + superC.getPlayC().getPot() + " + ("+superC.getPlayC().getIncrease()+")");
		lScore.setText("P1: " + superC.getPlayC().getScoreP1() + " / P2: " + superC.getPlayC().getScoreP2());
		this.revalidate();
	}
}
