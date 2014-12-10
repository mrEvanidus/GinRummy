package edu.up.cs301.ginrummy;

import java.io.Serializable;
import java.util.ArrayList;

import edu.up.cs301.card.Card;

/**
 * Represents a meld (run or set of at least three cards)
 * 
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
 *
 */
public class Meld implements Serializable{
	private static final long serialVersionUID = -8590155092857173829L;

	//The cards in the meld
	public ArrayList<Card> cards;

	public boolean isSet; //true if the meld is a set
	private int val;      //the score for the meld
	public int id;        //the ID of the meld

	/**
	 *  Constructor
	 * @param meldCards The cards in the meld
	 * @param set Whether or not the meld is a set
	 * @param meldVal The score of the meld
	 * @param meldID The ID of the meld
	 */
	public Meld(ArrayList<Card> meldCards, boolean set, int meldVal, int meldID){
		cards = new ArrayList<Card>(meldCards);
		isSet = set;
		val   = meldVal;
		id = meldID;
	}

	/**
	 * Copy Constructor
	 * 
	 * @param orig the Meld to be copied
	 */
	public Meld(Meld orig){
		//Create new cards that are equivalent to the original
		cards = new ArrayList<Card>();
		for(Card c : orig.cards){
			cards.add(new Card(c));
		}
		isSet = orig.isSet;
		val = orig.val;
		id = orig.id;
	}

	/**
	 * Removes the specified card from the meld
	 * 
	 * @param c Card to be removed
	 */
	public synchronized void remove(Card c){
		for(Card card : cards){
			if(card != null && card.getRank() == c.getRank() && card.getSuit() == c.getSuit()){
				cards.remove(card);
				break;
			}
		}
	}

	/**
	 * 
	 * @return The cards in the Meld
	 */
	public ArrayList<Card> getMeldCards() {
		return cards;
	}


}
