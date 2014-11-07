package edu.up.cs301.slapjack;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Gin Rummy game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */
public class GRState extends GameState
{
	public static final int DRAW_PHASE = 0;
	public static final int DISCARD_PHASE = 1;
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
	private Deck stock;
	private Deck discard;
    
	private Deck[] playerHands;
	private int[] playerScores;
	
    // whose turn is it to turn a card?
    private int whoseTurn;
    
    // which part of the turn is it?
    // 0 = draw
    // 1 = discard
    private int turnPhase;
    
    //The number of round that have passed
    private int rounds;

    // TODO make private and make getter/setters
    public boolean isEndOfRound;
    
    private String gameMessage;
    /**
     * Constructor for objects of class SJState. Initializes for the beginning of the
     * game, with a random player as the first to turn card
     *  
     */
    public GRState() {
    	// randomly pick the player who starts
    	whoseTurn = (int)(2*Math.random());
    	turnPhase = DRAW_PHASE;
    	
    	playerHands[0] = new Deck();
    	playerHands[1] = new Deck();
    	
    	playerScores[0] = 0;
    	playerScores[1] = 0;
    	
    	rounds = 0;
    	
    	gameMessage = null;
    	stock = new Deck();
    	discard = new Deck();
    	
    	stock.add52();
    	
    }
    
    /**
     * Copy constructor for objects of class SJState. Makes a copy of the given state
     *  
     * @param orig  the state to be copied
     */
    public GRState(GRState orig) {
    	// set index of player whose turn it is
    	whoseTurn = orig.whoseTurn;
    	playerHands[0] = orig.playerHands[0];
    	playerHands[1] = orig.playerHands[1];
    	stock = orig.stock;
    	discard = orig.discard;
    	turnPhase = orig.turnPhase;
    	rounds = orig.rounds;
    	playerScores[0] = orig.playerScores[0];
    	playerScores[1] = orig.playerScores[1];
        
    }
    
    public boolean canKnock(int pidx){
//    	ArrayList<ArrayList<Card>> ranks = new ArrayList<ArrayList<Card>>();
//    	ArrayList<ArrayList<Card>> suits = new ArrayList<ArrayList<Card>>();
//    	
//    	// SET DETECTION
//    	for( Card c : playerHands[pidx].cards){
//    		ranks.get(c.getRank().value(1) - 1).add(c);
//    	}
//    	
//    	for(ArrayList<Card> a : ranks){
//    		for(Card c : a){
//    			c.setSL(a.size());
//    		}
//    	}
//    	
//    	// RUN DETECTION
//    	
//    	// Sort hand into suits
//    	for( Card c : playerHands[pidx].cards){
//    		if(c.getSuit().shortName() == 'C'){
//    			suits.get(0).add(c);
//    		}
//    		else if(c.getSuit().shortName() == 'D'){
//    			suits.get(1).add(c);
//    		}
//    		else if(c.getSuit().shortName() == 'H'){
//    			suits.get(2).add(c);
//    		}
//    		else if(c.getSuit().shortName() == 'S'){
//    			suits.get(3).add(c);
//    		}
//    		else {
//    			//this is a bad
//    		}
//    	}
//    	
//    	// Sort suits by rank
//    	for(ArrayList<Card> a : suits){
//    		for(Card c : a){
//    			a.set(c.getRank().value(1) - 1, c);
//    		}
//    	}
//    	
//    	int runCount = 0;
//    	
//    	for(int i = 0; i < 4; i++){
//    		ArrayList<Card> a = suits.get(i);
//    		
//    		a.set(c.getRank().value(1) - 1, c);
//
//    	}
    	
    	return true;
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
    public int whoseTurn() {
        return whoseTurn;
    }
    
    public int getPhase() {
    	return turnPhase;
    }
    
    /**
     * change whose move it is
     * 
     * @param idx
     * 		the index of the player whose move it now is
     */
    public void setWhoseTurn(int idx) {
    	whoseTurn = idx;
    }
    
    public void setPhase(int phase){
    	turnPhase = phase;
    }
 
    public Deck getHand(int playeridx){
    	return playerHands[playeridx];
    }
    
    public int getp1score(){
    	return playerScores[0];
    }
    
    public int getp2score(){
    	return playerScores[1];
    }
    
    public boolean drawFrom(boolean fromStock, int playeridx){
    	if(fromStock){
    		stock.moveTopCardTo(playerHands[playeridx]);
    	} else {
    		discard.moveTopCardTo(playerHands[playeridx]);
    	}
    	
    	//TODO discuss case where all cards are drawn
    	if(stock.size() == 0){
    		return false;
    	}
    	return true;
    }
    
    public boolean discard(Card disCard, int playeridx){
    	
    	discard.add(disCard);
    	playerHands[playeridx].remove(disCard);
    	return true;
    }
    
    public Card getTopDiscard(){
    	return discard.peekAtTopCard();
    }
    
    public void nullCardsFor(int playeridx){
    	if(playeridx == 0){
    		playerHands[1].nullifyDeck();
    		stock.nullifyDeck();
    		// TODO nullify all but top of discard
    	}else if(playeridx == 1){
    		playerHands[0].nullifyDeck();
    		stock.nullifyDeck();
    	}
    	
    }
}
