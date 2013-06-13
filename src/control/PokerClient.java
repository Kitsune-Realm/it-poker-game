package control;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import model.Card;
import model.CardDeck;
import view.PlayPanel;
import view.TopPanel;

/**
 * @author Raoul Theunis en Kevin van der Vleuten
 * This program was based upon the TicTacToe program of the Networking chapter in the
 * Liang book. Used to learn server/client communication technique.
 *
 */
public class PokerClient extends JFrame implements Runnable, PokerConstants
{
	private SuperController superC;
	private LogController logC;
	
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	
	private boolean myTurn = false;
	private boolean continueToPlay = true;
	private boolean waiting = true;
	private String host = "127.0.0.1";
	private int player;
	
	private TopPanel topPanel;
	private JPanel rightPanel;
	private JLabel lChips;
	private JLabel lBlind;
	private int selectedAction;
	private JButton btnFold;
	private JButton btnCall;
	private JButton btnRaise;
	private JButton btnCheck;
	
	private List<Card> deck;
	
	public PokerClient()
	{		
		superC = new SuperController();
		superC.getPlayC().setChipStack(0);
		superC.getPlayC().setPot(0);
		superC.getPlayC().setCalledChips(0);
		superC.getPlayC().setRound(1);
		
		
		
		topPanel = new TopPanel(superC);		
		add(topPanel, BorderLayout.NORTH);
		
		logC = superC.getLogC();
		JScrollPane scrollPane = new JScrollPane(logC.getLog());
		scrollPane.setEnabled(false);
		add(scrollPane, BorderLayout.SOUTH);
		
		createRightPanel();
		
		JPanel playPanel = new PlayPanel(superC);
		add(playPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("AnimeGirl PokerGame!");
		setVisible(true);
		setLocationRelativeTo(null);
		
		connectToServer(10);
	}
	
	public static void main(String[] args)
	{
		new PokerClient();
	}
	
	private void connectToServer(int attempts)
	{
		if (attempts <= 0) {
			JOptionPane.showMessageDialog(null, "Timeout: Er is geen Host gevonden! \nHost: " +  host + "\nPort: 8000", "geen host gevonden", JOptionPane.ERROR_MESSAGE);
			System.exit(0);			
		}	
		try {
			Thread.sleep(1000);
			Socket socket;
			socket = new Socket(host, 8000);
			fromServer = new DataInputStream(socket.getInputStream());
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch(Exception ex) {
			logC.appendTop("Attempting another connection (" + attempts + ")");			
			connectToServer(attempts - 1);			
		}
		
		Thread thread = new Thread(this);
		thread.start();		
	}
	
	@Override
	public void run()
	{
		logC.appendTop("starting thread");
		try {
			player = fromServer.readInt();
			logC.appendTop("init for player"+player);
			if (player == PLAYER1) {				
				setTitle("AnimeGirl PokerGame! - Player 1");
				superC.getPlayC().setOtherPlayer(PLAYER2);
				logC.appendTop("Waiting for player 2 to join");
				fromServer.readInt();
				
				initPlayers();
				lChips.setText("Chips: " + superC.getPlayC().getChipStack());
				constructDeck(1);
				superC.getPlayC().setBigBlind(fromServer.readBoolean());
				setBlinds(10);
				
				logC.appendTop("Other player is ready, you may make your move! ^w^");
				setMyTurn(true);
			}
			else if (player == PLAYER2) {				
				setTitle("AnimeGirl PokerGame! - Player 2");
				superC.getPlayC().setOtherPlayer(PLAYER1);
					
				initPlayers();
				lChips.setText("Chips: " + superC.getPlayC().getChipStack());
				constructDeck(2);
				superC.getPlayC().setBigBlind(fromServer.readBoolean());
				setBlinds(10);
				setMyTurn(false);
			}						
			
			while (continueToPlay) {
				if (player == PLAYER1) {
					waitForPlayerAction();
					sendMove();
					receiveMove();
				}
				else if (player == PLAYER2) {
					receiveMove();
					waitForPlayerAction();
					sendMove();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void waitForPlayerAction() throws InterruptedException
	{
		while (waiting) {
			Thread.sleep(100);
		}
		waiting = true;
	}
	
	private void sendMove() throws IOException
	{
		if (myTurn) {
			toServer.writeInt(selectedAction);
			if (selectedAction == ACTION_CALL) {
				superC.getPlayC().setChipStack(superC.getPlayC().getChipStack() - superC.getPlayC().getPot());
				toServer.writeInt(superC.getPlayC().getPot());				
			}
			setMyTurn(false);
		}
	}
	
	private void receiveMove() throws IOException
	{	
		logC.appendTop("Waiting for other player to make a move");
		int status = fromServer.readInt();	
		
		if (status == ACTION_FOLD) {			
			logC.appendTop("Player "+superC.getPlayC().getOtherPlayer()+" used - Fold!");
			JOptionPane.showMessageDialog(null, "you won this round! :)");
			
			toServer.writeInt(player);
			toServer.writeInt(superC.getPlayC().getPot());
			toServer.writeInt(superC.getPlayC().getScoreP1());
			toServer.writeInt(superC.getPlayC().getScoreP2());
			
			logC.appendTop("Recieving new data");
			superC.getPlayC().setChipStack(superC.getPlayC().getChipStack() + fromServer.readInt());
			lChips.setText("$: " + superC.getPlayC().getChipStack());
			superC.getPlayC().setRound(fromServer.readInt());
			superC.getPlayC().setScoreP1(fromServer.readInt());
			superC.getPlayC().setScoreP2(fromServer.readInt());	
			superC.getPlayC().setPot(0);
			logC.appendTop("Round: " + superC.getPlayC().getRound() + " Scores P1: " +superC.getPlayC().getScoreP1() + "P2: " +superC.getPlayC().getScoreP2());
			topPanel.updateTopPanel();
			topPanel.revalidate();
			
			superC.getDeckC().resetDeck();
			constructDeck(player);
			logC.appendTop("Setting blinds");
			superC.getPlayC().setBigBlind(!superC.getPlayC().isBigBlind());
			setBlinds(20);			

			setMyTurn(true);
			waiting = true;
		}
		else if (status == ACTION_CALL) {
			logC.appendTop("Player "+superC.getPlayC().getOtherPlayer()+" used - Call!");		
			
			superC.getPlayC().setPot(fromServer.readInt());			
			topPanel.updateTopPanel();
			topPanel.revalidate();
			setMyTurn(true);
		}
		else if (status == ACTION_RAISE) {
			logC.appendTop("Player "+superC.getPlayC().getOtherPlayer()+" used - Raise!");
			setMyTurn(true);
		}
		else if (status == ACTION_ENDGAME) {
			logC.appendTop("Game is over");
			logC.appendTop("recieving new data");
			superC.getPlayC().setChipStack(superC.getPlayC().getChipStack() + fromServer.readInt());
			lChips.setText("$: " + superC.getPlayC().getChipStack());
			superC.getPlayC().setRound(fromServer.readInt());
			superC.getPlayC().setScoreP1(fromServer.readInt());
			superC.getPlayC().setScoreP2(fromServer.readInt());			
			superC.getPlayC().setPot(0);
			logC.appendTop("Round: " + superC.getPlayC().getRound() + " Scores P1: " +superC.getPlayC().getScoreP1() + "P2: " +superC.getPlayC().getScoreP2());
			topPanel.updateTopPanel();
			topPanel.revalidate();
			
			logC.appendTop("Shuffeling deck");
			superC.getDeckC().resetDeck();
			constructDeck(player);
			logC.appendTop("Setting blinds");
			superC.getPlayC().setBigBlind(!superC.getPlayC().isBigBlind());
			setBlinds(20);			
			
			setMyTurn(false);
			waiting = false;
		}
		else if (status == ACTION_CALLED) {
			superC.getPlayC().setPot(fromServer.readInt());
			topPanel.updateTopPanel();
			topPanel.revalidate();
			lChips.setText("$: " + superC.getPlayC().getChipStack());
			setMyTurn(false);
		}
		else {
			logC.appendTop("ERROR");
			setMyTurn(false);
		}
		
		
	}
	
	public void initPlayers()
	{
		logC.appendTop("Initializing game data");
		try {
			superC.getPlayC().setChipStack(fromServer.readInt());
			superC.getPlayC().setRound(fromServer.readInt());
			superC.getPlayC().setScoreP1(fromServer.readInt());
			superC.getPlayC().setScoreP2(fromServer.readInt());
			superC.getPlayC().setPot(fromServer.readInt());
			topPanel.updateTopPanel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void constructDeck(int Player)
	{
		int amount = 52;
		logC.appendTop("Initializing new card deck");
		CardDeck<Card> newDeck = new CardDeck<Card>();
		
		for (int i=0; i<amount; i++) {
			try {
				int symbol = fromServer.readInt();
				int score = fromServer.readInt();
				newDeck.add(new Card(symbol, score));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		superC.getDeckC().setDeck(newDeck);	
		
		if (player == 1) {
			superC.getDeckC().getHand().add(superC.getDeckC().getFirstCard());
			superC.getDeckC().getHandOther().add(superC.getDeckC().giveOtherPlayerFirstCard());
			superC.getDeckC().getHand().add(superC.getDeckC().getFirstCard());
			superC.getDeckC().getHandOther().add(superC.getDeckC().giveOtherPlayerFirstCard());
			superC.getDeckC().giveBank3Cards();
			superC.getDeckC().getHand().sortDeckOnScore();
			superC.getDeckC().setDrawCards(true);
		}
		else if (player == 2) {
			superC.getDeckC().getHandOther().add(superC.getDeckC().giveOtherPlayerFirstCard());
			superC.getDeckC().getHand().add(superC.getDeckC().getFirstCard());
			superC.getDeckC().getHandOther().add(superC.getDeckC().giveOtherPlayerFirstCard());
			superC.getDeckC().getHand().add(superC.getDeckC().getFirstCard());
			superC.getDeckC().giveBank3Cards();
			superC.getDeckC().getHand().sortDeckOnScore();
			superC.getDeckC().setDrawCards(true);
		}		
	}

	private void setEnabledButtons(boolean status)
	{
		btnFold.setEnabled(status);
		btnCall.setEnabled(status);
		btnRaise.setEnabled(status);
		btnCheck.setEnabled(status);
	}
	
	private void createRightPanel()
	{
		rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(3,1));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.Y_AXIS));
		
		btnFold = new JButton("Fold");
		btnFold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){
				logC.appendTop("you used - Fold!");
				logC.appendTop("You folded, you lost round " + superC.getPlayC().getRound());
				JOptionPane.showMessageDialog(null, "you lost this round :(");
				selectedAction = ACTION_FOLD;
				waiting = false;
			}			
		});
		btnCall = new JButton("Call");
		btnCall.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){
				logC.appendTop("you used - Call!");
				selectedAction = ACTION_CALL;
				waiting = false;
			}			
		});
		btnRaise = new JButton("Raise");
		btnRaise.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){
				logC.appendTop("you used - Raise!");
				selectedAction = ACTION_RAISE;
				waiting = false;
			}			
		});
		btnCheck = new JButton("Check");
		btnCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){				
				logC.appendTop("You check for highest Combo: " + superC.getDeckC().CheckCombo());
			}			
		});
		
		buttonPanel.add(btnCall);
		buttonPanel.add(btnFold);
		buttonPanel.add(btnRaise);
		buttonPanel.add(btnCheck);
		setEnabledButtons(false);		
		
		rightPanel.add(buttonPanel);
		
		
		
		lBlind = new JLabel("");
		lChips = new JLabel("$: " + superC.getPlayC().getChipStack());
		
		rightPanel.add(lBlind);
		rightPanel.add(lChips);		
		
		rightPanel.setBorder(new LineBorder(Color.DARK_GRAY));
		add(rightPanel, BorderLayout.EAST);
	}
	
	public void setMyTurn(boolean myTurn)
	{
		this.myTurn = myTurn;
		setEnabledButtons(myTurn);
		if(myTurn)
			logC.appendTop("You may make your move");
	}
	
	private void setBlinds(int input)
	{
		int amount = input;
		String blind;
		if (superC.getPlayC().isBigBlind()) {
			blind = "Big Blind";
			superC.getPlayC().setChipStack(superC.getPlayC().getChipStack() - amount);
			updateChipsLabel();
			try {
				toServer.writeInt(amount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			blind = "Small Blind";
			superC.getPlayC().setChipStack(superC.getPlayC().getChipStack() - (amount/2));
			updateChipsLabel();
			try {
				toServer.writeInt(amount/2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		lBlind.setText(blind);
		
		try {
			superC.getPlayC().setPot(fromServer.readInt());
		} catch (IOException e) {
			e.printStackTrace();
		}
		topPanel.updateTopPanel();
		rightPanel.revalidate();
	}
	
	private void updateChipsLabel()
	{
		lChips.setText("$: " + superC.getPlayC().getChipStack());
	}
}
