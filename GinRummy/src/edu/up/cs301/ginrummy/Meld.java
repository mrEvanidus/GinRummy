package edu.up.cs301.ginrummy;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.card.Card;

public class Meld implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8590155092857173829L;
	
	public ArrayList<Card> cards;

	public boolean isSet;
	private int val;
	public int id;
	
	public Meld(ArrayList<Card> meldCards, boolean set, int meldVal, int meldID){
		cards = new ArrayList<Card>(meldCards);
		isSet = set;
		val   = meldVal;
		id = meldID;
	}
	
	public Meld(Meld orig){
		cards = new ArrayList<Card>();
		for(Card c : orig.cards){
			cards.add(new Card(c));
		}
		isSet = orig.isSet;
		val = orig.val;
		id = orig.id;
	}
	//ERIC: Need to retrieve the melds
	public ArrayList<Card> getMeldCards() {
		return cards;
		
	}
	
	
}
