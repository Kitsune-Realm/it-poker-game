package control;
import java.awt.BorderLayout;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import model.Card;
import model.CardDeck;

/**
 * @author Raoul Theunis en Kevin van der Vleuten
 * This program was based upon the TicTacToe program of the Networking chapter in the
 * Liang book. Used to learn server/client communication technique.
 *
 */
public class PokerServer extends JFrame implements PokerConstants
{	
	private SuperController superC;
	
	public PokerServer()
	{
		superC = new SuperController();
		LogController logC = superC.getLogC();
		JScrollPane scrollPane = new JScrollPane(logC.getLog());		
		
		add(scrollPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setTitle("AnimeGirl PokerGame! - Server");
		setVisible(true);
		
		try {
			ServerSocket serverSocket = new ServerSocket(8000);
			logC.append("Server started at socket 8000");
			
			int sessionNo = 1;
			
			while(true) {
				logC.append("Waiting for other players to join session " + sessionNo);
				
				Socket player1 = serverSocket.accept();
				logC.append("Player 1 joined session " + sessionNo);
				logC.append("Player 1's IP address: " + player1.getInetAddress().getHostAddress());
				
				new DataOutputStream(player1.getOutputStream()).writeInt(PLAYER1);
				
				Socket player2 = serverSocket.accept();
				logC.append("Player 2 joined session " + sessionNo);
				logC.append("Player 2's IP address: " + player1.getInetAddress().getHostAddress());
				
				new DataOutputStream(player2.getOutputStream()).writeInt(PLAYER2);
								
				logC.append("Starting thread for session " + (sessionNo+1));
				HandleASession task = new HandleASession(player1, player2, superC);
				
				new Thread(task).start();				
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		PokerServer frame = new PokerServer();
	}
}

class HandleASession implements Runnable, PokerConstants
{
	private SuperController superC;
	private LogController logC;
	
	private Socket player1;
	private Socket player2;
	
	private DataInputStream fromPlayer1;
	private DataOutputStream toPlayer1;
	private DataInputStream fromPlayer2;
	private DataOutputStream toPlayer2;
	
	private CardDeck<Card> deck;
	private boolean bigBlindP1;
	
	public HandleASession(Socket player1, Socket player2, SuperController superC)
	{
		this.superC = superC;
		this.logC = superC.getLogC();
		this.player1 = player1;
		this.player2 = player2;
		deck = superC.getDeckC().getDeck();
	}

	@Override
	public void run()
	{
		try {
			fromPlayer1 = new DataInputStream(player1.getInputStream());
			toPlayer1 = new DataOutputStream(player1.getOutputStream());
			fromPlayer2 = new DataInputStream(player2.getInputStream()); 
			toPlayer2 = new DataOutputStream(player2.getOutputStream());
			
			toPlayer1.writeInt(1);
			
			logC.append("Creating card deck for players");	
			initPlayers(1000, 1, 0, 0, 0);
			superC.getPlayC().setRound(1);
			writeDeck();
			writeBlinds();
			
			
			for (int i=0; i<12; i++) {
				logC.append("Card "+i+" :" + deck.getCard(i).getCardStats());
			}
				
			
			while (true) {
				// P1
				logC.append("Awaiting action from P1");
				int status1 = fromPlayer1.readInt();
				logC.append("Recieved P1: " + status1);	
				
				if (status1 == ACTION_FOLD) {	
					foldAction(status1, fromPlayer1, toPlayer1, fromPlayer2, toPlayer2);
				}
				else if (status1 == ACTION_CALL) {
					callAction(status1, fromPlayer1, toPlayer1, fromPlayer2, toPlayer2);
				}				
				
				// P2
				logC.append("Awaiting action from P2");
				int status2 = fromPlayer2.readInt();
				logC.append("Recieved P2: " + status2);
				
				if (status2 == ACTION_FOLD) {
					foldAction(status2, fromPlayer2, toPlayer2, fromPlayer1, toPlayer1);
				}	
				else if (status2 == ACTION_CALL) {					
					callAction(status2, fromPlayer2, toPlayer2, fromPlayer1, toPlayer1);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void callAction(int status, DataInputStream fromThisPlayer, DataOutputStream toThisPlayer, DataInputStream fromOtherPlayer, DataOutputStream toOtherPlayer)
	{		
		try {
			toThisPlayer.writeInt(ACTION_CALLED);
			toOtherPlayer.writeInt(status);
		
			superC.getPlayC().setIncrease(superC.getPlayC().getIncrease() + fromThisPlayer.readInt());
			
			toOtherPlayer.writeInt(superC.getPlayC().getIncrease());
			toThisPlayer.writeInt(superC.getPlayC().getIncrease());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void foldAction(int status, DataInputStream fromThisPlayer, DataOutputStream toThisPlayer, DataInputStream fromOtherPlayer, DataOutputStream toOtherPlayer)
	{
		
		try {
			toOtherPlayer.writeInt(status);
			toThisPlayer.writeInt(ACTION_ENDGAME);
			
			int winner = fromOtherPlayer.readInt();
			logC.append("Player " + winner + " has won this round!");	
			int pot = fromOtherPlayer.readInt();
			superC.getPlayC().setScoreP1(fromOtherPlayer.readInt()+1);
			superC.getPlayC().setScoreP2(fromOtherPlayer.readInt());
			superC.getPlayC().setRound(superC.getPlayC().getRound()+1);
			
			logC.append("Writing new data");
			logC.append("Round: " + superC.getPlayC().getRound() + " Scores P1: " +superC.getPlayC().getScoreP1() + "P2: " +superC.getPlayC().getScoreP2());
			toOtherPlayer.writeInt(pot);
			toThisPlayer.writeInt(0);					
			toOtherPlayer.writeInt(superC.getPlayC().getRound());
			toThisPlayer.writeInt(superC.getPlayC().getRound());
			toOtherPlayer.writeInt(superC.getPlayC().getScoreP1());
			toThisPlayer.writeInt(superC.getPlayC().getScoreP1());
			toOtherPlayer.writeInt(superC.getPlayC().getScoreP2());
			toThisPlayer.writeInt(superC.getPlayC().getScoreP2());
			logC.append("Writing new deck");
			writeDeck();
			
			logC.append("Setting up a new Pot");
			superC.getPlayC().setPot(0);
			superC.getPlayC().setPot(superC.getPlayC().getPot() + fromOtherPlayer.readInt());
			superC.getPlayC().setPot(superC.getPlayC().getPot() + fromThisPlayer.readInt());
			toOtherPlayer.writeInt(superC.getPlayC().getPot());
			toThisPlayer.writeInt(superC.getPlayC().getPot());
			logC.append("Pot has been set! Pot: " + superC.getPlayC().getPot());		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void initPlayers(int chipStack, int round, int scoreP1, int scoreP2, int pot)
	{	
		logC.append("Initializing game data");
		try {
			toPlayer1.writeInt(chipStack);			
			toPlayer1.writeInt(round);			
			toPlayer1.writeInt(scoreP1);			
			toPlayer1.writeInt(scoreP2);			
			toPlayer1.writeInt(pot);
			
			toPlayer2.writeInt(chipStack);
			toPlayer2.writeInt(round);
			toPlayer2.writeInt(scoreP1);
			toPlayer2.writeInt(scoreP2);
			toPlayer2.writeInt(pot);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void writeDeck()
	{
		for(int i=1; i<=13; i++) {
			for(int s=0; s<4 ; s++) {				
				deck.add(new Card(s,i));
			}					
		}
		
		deck.shuffle();
				
		int[] symbols = new int[deck.size()];
		int[] scores = new int[deck.size()];
				
		for (int i=0; i<deck.size(); i++) {
			symbols[i] = deck.getCard(i).getSymbol();
			scores[i] = deck.getCard(i).getScore();
		}
		
		for (int i=0; i<52; i++) {
			try {
				toPlayer1.writeInt(symbols[i]);
				toPlayer1.writeInt(scores[i]);
				toPlayer2.writeInt(symbols[i]);
				toPlayer2.writeInt(scores[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeBlinds()
	{
		boolean bb = (Math.random() >= 0.5);
		try {
			toPlayer1.writeBoolean(bb);
			toPlayer2.writeBoolean(!bb);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		bigBlindP1 = bb;
		if (bb)
			logC.append("Player 1 is big blind, Player 2 is small blind");
		else
			logC.append("Player 1 is small blind, Player 2 is big blind");
		
		try {
			superC.getPlayC().setPot(superC.getPlayC().getPot() + fromPlayer1.readInt());			
			superC.getPlayC().setPot(superC.getPlayC().getPot() + fromPlayer2.readInt());
			logC.append("Writing pot: " + superC.getPlayC().getPot());
			toPlayer1.writeInt(superC.getPlayC().getPot());
			toPlayer2.writeInt(superC.getPlayC().getPot());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
