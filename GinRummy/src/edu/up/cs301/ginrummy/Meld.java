package edu.up.cs301.ginrummy;

import java.util.ArrayList;

import edu.up.cs301.card.Card;

public class Meld {
	
	private ArrayList<Card> cards;
	private boolean isSet;
	private int val;
	private int id;
	
	public Meld(ArrayList<Card> meldCards, boolean set, int meldVal, int meldID){
		cards = meldCards;
		isSet = set;
		val   = meldVal;
		id = meldID;
	}
	
	
}