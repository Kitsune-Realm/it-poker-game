package control;
public interface PokerConstants
{
	public static int PLAYER1 = 1;
	public static int PLAYER2 = 2;
	public static int CONTINUE = 4;
	
	public static int PLAYER1_WON = 1;
	public static int PLAYER2_WON = 2;
	
	public static int FRAME_WIDTH = 900;
	public static int FRAME_HEIGHT = 520;
	
	public static int ACTION_CALL = 1;
	public static int ACTION_FOLD = 2;
	public static int ACTION_RAISE = 3;
	public static int ACTION_CALLED = 8;
	public static int ACTION_ENDGAME = 9;
	
	public static int STATUS_PLAYER1_WIN = 1;
	public static int STATUS_PLAYER2_WIN = 2;
	public static int STATUS_DRAW = 3;
	public static int STATUS_CONTINUE = 4;
	
	public static final int CARD_WIDTH = 286;
	public static final int CARD_HEIGHT = 400;
	public static final int CARD_MARGIN = 80;
	
	public static int COMBO_HIGHCARD = 1;
	public static int COMBO_ONEPAIR = 2;
	public static int COMBO_TWOPAIR = 3;
	public static int COMBO_THREEKIND = 4;
	public static int COMBO_STRAIGHT = 5;
	public static int COMBO_FLUSH = 6;
	public static int COMBO_FULLHOUSE = 7;
	public static int COMBO_FOURKIND = 8;
	public static int COMBO_STRAIGHTFLUSH = 9;
	public static int COMBO_ROYALFLUSH = 10;
}
