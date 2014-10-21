//JOHN WAS HERE
//Matt was here

package edu.up.cs301.slapjack;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.*;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Slapjack game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */
public class SJState extends GameState
{
	private static final long serialVersionUID = -8269749892027578792L;

	private static final int DRAW_PHASE = 0;
	private static final int DISCARD_PHASE = 1;
    ///////////////////////////////////////////////////
    // ************** instance variables ************
    ///////////////////////////////////////////////////

	// the three piles of cards:
    //  - 0: pile for player 0
    //  - 1: pile for player 1
    //  - 2: the "up" pile, where the top card
	// Note that when players receive the state, all but the top card in all piles
	// are passed as null.
    //private Deck[] piles;
	private Deck p1hand;
	private Deck p2hand;
	private Deck stockpile;
	private Deck discard;
    
    // whose turn is it to turn a card?
    private int toPlay;
    
    // which part of the turn is it?
    // 0 = draw
    // 1 = discard
    private int phase;
    
    //The number of round that have passed
    private int rounds;
    
    // The players' scores
    private int p1score;
    private int p2score;

    /**
     * Constructor for objects of class SJState. Initializes for the beginning of the
     * game, with a random player as the first to turn card
     *  
     */
    public SJState() {
    	// randomly pick the player who starts
    	toPlay = (int)(2*Math.random());
    		
    	p1hand = new Deck();
    	p2hand = new Deck();
    	stockpile = new Deck();
    	discard = new Deck();
    	
    	stockpile.add52();
    	
    }
    
    /**
     * Copy constructor for objects of class SJState. Makes a copy of the given state
     *  
     * @param orig  the state to be copied
     */
    public SJState(SJState orig) {
    	// set index of player whose turn it is
    	toPlay = orig.toPlay;
    	p1hand = orig.p1hand;
    	p2hand = orig.p2hand;
    	stockpile = orig.stockpile;
    	discard = orig.discard;
    	phase = orig.phase;
    	rounds = orig.rounds;
    	p1score = orig.p1score;
    	p2score = orig.p2score;
        
    }
    
    /**
     * Gives the given deck.
     * 
     * @return  the deck for the given player, or the middle deck if the
     *   index is 2
     */
//    public Deck getDeck(int num) {
//        if (num < 0 || num > 2) return null;
//        return piles[num];
//    }
    
    /**
     * Tells which player's turn it is.
     * 
     * @return the index (0 or 1) of the player whose turn it is.
     */
    public int toPlay() {
        return toPlay;
    }
    
    /**
     * change whose move it is
     * 
     * @param idx
     * 		the index of the player whose move it now is
     */
    public void setToPlay(int idx) {
    	toPlay = idx;
    }
 
    public int getp1score(){
    	return p1score;
    }
    
    public int getp2score(){
    	return p2score;
    }
//    /**
//     * Replaces all cards with null, except for the top card of deck 2
//     */
//    public void nullAllButTopOf2() {
//    	// see if the middle deck is empty; remove top card from middle deck
//    	boolean empty2 = piles[2].size() == 0;
//    	Card c = piles[2].removeTopCard();
//    	
//    	// set all cards in deck to null
//    	for (Deck d : piles) {
//    		d.nullifyDeck();
//    	}
//    	
//    	// if middle deck had not been empty, add back the top (non-null) card
//    	if (!empty2) {
//    		piles[2].add(c);
//    	}
//    }
    
    public void nullAppropriateCards(int playeridx){
    	if(playeridx == 0){
    		p2hand.nullifyDeck();
    		stockpile.nullifyDeck();
    		// TODO nullify all but top of discard
    	}
    	
    }
}
