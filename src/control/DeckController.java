package control;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import model.Card;
import model.CardDeck;

public class DeckController
{
	private SuperController superC;
	private CardDeck<Card> deck;
	private CardDeck<Card> hand;
	private CardDeck<Card> handOther;
	private CardDeck<Card> bank;
	private boolean drawCards;
	
	public DeckController(SuperController superC)
	{
		this.superC = superC;
		this.deck = new CardDeck<Card>();
		this.hand = new CardDeck<Card>();
		this.handOther = new CardDeck<Card>();
		this.bank = new CardDeck<Card>();
		this.drawCards = false;
	}	
	
	public void resetDeck()
	{
		this.deck = new CardDeck<Card>();
		this.hand = new CardDeck<Card>();
		this.handOther = new CardDeck<Card>();
		this.bank = new CardDeck<Card>();
		this.drawCards = false;
	}
	
	public CardDeck<Card> getDeck()
	{
		return deck;
	}

	public void setDeck(CardDeck<Card> deck)
	{
		this.deck = deck;
	}
	
	public String CheckCombo()
	{
		String combo = "no combo found";
		int pairs = 0;
		int bankpairs = 0;
		
		//Check hand cards among eachother
		if (hand.getFirstCard().getScore() == hand.getSecondCard().getScore())
			pairs++;
		
		//Check bank cards among eachother
		bankpairs = checkBankPairs(bank.size(), 0, 0);
		pairs += checkBankPairs(bank.size(), 0, 0);
		
		//Check hand cards with bank cards
		for (int h=0; h<hand.size(); h++) {
			for(int b=0; b<bank.size(); b++) {
				if((hand.getCard(h).getScore() == bank.getCard(b).getScore()) &&
				   (hand.getCard(h).getSymbol() != bank.getCard(b).getSymbol()) ) {
					pairs++;
				}
			}		

		}		
		
		if (pairs == 1)
			combo = "One Pair";
		else if (pairs == 2)
			combo = "Two Pair";
		else if (pairs == 3)
			combo = "Three of a Kind";
		else if (pairs == 4)
			combo = "Four of a Kind";
		
		combo += "  (Pairs: " + pairs + " bankpairs: " + bankpairs + ")";
		
		return combo;
	}
	
	public int checkBankPairs(int amount, int index, int pairs) 
	{
		int startIndex = index;
		while(index < amount) {			
			int score1 = bank.getCard(startIndex).getScore();
			int score2 = bank.getCard(index).getScore();
			int symbol1 = bank.getCard(startIndex).getSymbol();
			int symbol2 = bank.getCard(index).getSymbol();
			index++;
			
			if ((score1 == score2) && (symbol1 != symbol2)) {
				pairs++;
			}			
		}
		startIndex++;
		
		if(startIndex >= amount)
			return pairs;
		else
			return checkBankPairs(amount, startIndex, pairs);
	}
	
	public Card getFirstCard()
	{
		if(deck.size() > 0) {
			Card givenCard = deck.getFirstCard();
			deck.burnFirstCard();
			superC.getLogC().appendTop("You have drawn: " + givenCard.getCardStats());
			return givenCard;
		}
		else
			return null;
	}
	
	public Card giveOtherPlayerFirstCard()
	{
		if(deck.size() > 0) {
			Card givenCard = deck.getFirstCard();
			deck.burnFirstCard();
			return givenCard;
		}
		else
			return null;
	}
	
	public void giveBank1Card()
	{
		burnFirstCard();
		Card givenCard = deck.getFirstCard();
		bank.add(givenCard);
		deck.burnFirstCard();
		superC.getLogC().appendTop("Bank has drawn: " + givenCard.getCardStats());
	}
	
	public void giveBank3Cards()
	{
		for(int i=0; i<3; i++) {
			giveBank1Card();
		}
	}	
	
	public void burnFirstCard()
	{
		if(deck.size() > 0) {
			deck.burnFirstCard();
		}
		else
			superC.getLogC().appendTop("Deck is empty!");
	}

	public CardDeck<Card> getHand()
	{
		return hand;
	}

	public void setHand(CardDeck<Card> hand)
	{
		this.hand = hand;
	}

	public boolean isDrawCards()
	{
		return drawCards;
	}

	public void setDrawCards(boolean drawCards)
	{
		this.drawCards = drawCards;
	}

	public CardDeck<Card> getBank()
	{
		return bank;
	}

	public void setBank(CardDeck<Card> bank)
	{
		this.bank = bank;
	}

	public CardDeck<Card> getHandOther()
	{
		return handOther;
	}

	public void setHandOther(CardDeck<Card> handOther)
	{
		this.handOther = handOther;
	}
	
}
