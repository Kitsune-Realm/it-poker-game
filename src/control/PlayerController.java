package control;

public class PlayerController
{
	private SuperController superC;
	
	private int round;
	private int pot;
	private int increase;
	private int chipStack;
	private boolean bigBlind;
	private int otherPlayer;
	private int scoreP1;
	private int scoreP2;
	
	public PlayerController(SuperController superC)
	{
		this.superC = superC;
	}

	public int getChipStack()
	{
		return chipStack;
	}

	public void setChipStack(int chipStack)
	{
		this.chipStack = chipStack;
	}

	public boolean isBigBlind()
	{
		return bigBlind;
	}

	public void setBigBlind(boolean bigBlind)
	{
		this.bigBlind = bigBlind;
	}

	public int getRound()
	{
		return round;
	}

	public void setRound(int round)
	{
		this.round = round;
	}

	public int getPot()
	{
		return pot;
	}

	public void setPot(int pot)
	{
		this.pot = pot;
	}

	public int getOtherPlayer()
	{
		return otherPlayer;
	}

	public void setOtherPlayer(int otherPlayer)
	{
		this.otherPlayer = otherPlayer;
	}

	public int getScoreP1()
	{
		return scoreP1;
	}

	public void setScoreP1(int scoreP1)
	{
		this.scoreP1 = scoreP1;
	}

	public int getScoreP2()
	{
		return scoreP2;
	}

	public void setScoreP2(int scoreP2)
	{
		this.scoreP2 = scoreP2;
	}

	public int getIncrease()
	{
		return increase;
	}

	public void setIncrease(int increase)
	{
		this.increase = increase;
	}	
		
}
