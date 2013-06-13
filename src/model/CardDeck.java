package model;

import java.util.ArrayList;
import java.util.Collections;

public class CardDeck <E extends Comparable<? super E>>
{
	private ArrayList<E> list;	
	
	public CardDeck()
	{
		this.list = new ArrayList<E>();
	}
	
	public void sortDeckOnScore()
	{
		Collections.sort(list);
	}
	
	public E getFirstCard()
	{
		return list.get(0);		
	}
	
	public E getSecondCard()
	{
		return list.get(1);		
	}
	
	public E getCard(int index)
	{
		return list.get(index);		
	}
	
	public void burnFirstCard()
	{
		list.remove(0);
	}
	
	public void add(E object)
	{
		list.add(object);
	}
	
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	public int size()
	{
		return list.size();
	}
	
	public String toString()
	{
		return "CardDeck: " + list.toString();
	}
	
	public void shuffle()
	{
		Collections.shuffle(list);
	}
	
}
