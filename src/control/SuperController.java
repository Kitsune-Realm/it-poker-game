package control;

public class SuperController
{
	private LogController logC;
	private DeckController deckC;
	private PlayerController playC;
	
	public SuperController()
	{
		logC = new LogController(this);
		deckC = new DeckController(this);
		playC = new PlayerController(this);
	}

	public LogController getLogC()
	{
		return logC;
	}

	public DeckController getDeckC()
	{
		return deckC;
	}

	public PlayerController getPlayC()
	{
		return playC;
	}
	
}
