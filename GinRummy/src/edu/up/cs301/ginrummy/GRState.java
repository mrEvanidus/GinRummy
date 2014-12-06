package edu.up.cs301.ginrummy;

import java.util.ArrayList;
import java.util.Hashtable;

import edu.up.cs301.card.*;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * Contains the state of a Gin Rummy game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 * 
 * @author Steven R. Vegdahl
 * 
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
 */
public class GRState extends GameState
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3480922944715960270L;
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

	private Deck[] playerHands = new Deck[2];
	private int[] playerScores = new int[2];
	private ArrayList<Hashtable<Integer, Meld>> playerMelds;

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
	public int ID;

	public String gameMessage;
	public String hello;

	//mystery variable!!
	private boolean fromDiscard;

	private Card lastPicked;

	private boolean lastMoveWasDraw;

	public int toGoFirst;
	public int yourId;

	public boolean lockGUI;
	public int meldCount;
	/**
	 * Constructor for objects of class SJState. Initializes for the beginning of the
	 * game, with a random player as the first to turn card
	 *  
	 */
	public GRState() {

		//initialize the meld lists
		playerMelds = new ArrayList<Hashtable<Integer, Meld>>();
		playerMelds.add(new Hashtable<Integer, Meld>());
		playerMelds.add(new Hashtable<Integer, Meld>());

		isEndOfRound = false;

		// randomly pick the player who starts
		whoseTurn = (int)(2*Math.random());
		//whoseTurn = 1;
		turnPhase = DRAW_PHASE;


		playerHands[0] = new Deck();
		playerHands[1] = new Deck();

		playerScores[0] = 0;
		playerScores[1] = 0;

		rounds = 0;
		ID = 1;

		setLastPicked(null);
		setFromDiscard(true);
		gameMessage = null;
		stock = new Deck();
		discard = new Deck();

		stock.add52();
		stock.shuffle();

		for(int i = 0; i < 10; i++){
			drawFrom(true,0);
			drawFrom(true,1);
		}

		//TEST HAND
    	for(int i = 0; i < 10; i++){
    		switch (i){
    			case 0:
    				playerHands[0].cards.set(i, new Card(Rank.TEN, Suit.Club));
    				break;
    			case 1:
    				playerHands[0].cards.set(i, new Card(Rank.ACE, Suit.Heart));
    				break;
    			case 2:
    				playerHands[0].cards.set(i, new Card(Rank.KING, Suit.Heart));
    				break;
    			case 3:
    				playerHands[0].cards.set(i, new Card(Rank.JACK, Suit.Spade));
    				break;
    			case 4:
    				playerHands[0].cards.set(i, new Card(Rank.THREE, Suit.Diamond));
    				break;
    			case 5:
    				playerHands[0].cards.set(i, new Card(Rank.THREE, Suit.Club));
    				break;
    			case 6:
    				playerHands[0].cards.set(i, new Card(Rank.JACK, Suit.Diamond));
    				break;
    			case 7:
    				playerHands[0].cards.set(i, new Card(Rank.QUEEN, Suit.Diamond));
    				break;
    			case 8:
    				playerHands[0].cards.set(i, new Card(Rank.SEVEN, Suit.Heart));
    				break;
    			case 9:
    				playerHands[0].cards.set(i, new Card(Rank.FIVE, Suit.Heart));
    				break;
    			
    			
    		}
    	}
    	
    	for(int i = 0; i < 10; i++){
		switch (i){
			case 0:
				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Spade));
				break;
			case 1:
				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Club));
				break;
			case 2:
				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Club));
				break;
			case 3:
				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Heart));
				break;
			case 4:
				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Diamond));
				break;
			case 5:
				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Spade));
				break;
			case 6:
				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Club));
				break;
			case 7:
				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Diamond));
				break;
			case 8:
				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Spade));
				break;
		case 9:
			playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Club));
				break;
		}
    	}
		//    	//TEST HAND
//		    	for(int i = 0; i < 10; i++){
//		    		switch (i){
//		    			case 0:
//		    				playerHands[0].cards.set(i, new Card(Rank.EIGHT, Suit.Club));
//		    				break;
//		    			case 1:
//		    				playerHands[0].cards.set(i, new Card(Rank.ACE, Suit.Diamond));
//		    				break;
//		    			case 2:
//		    				playerHands[0].cards.set(i, new Card(Rank.ACE, Suit.Heart));
//		    				break;
//		    			case 3:
//		    				playerHands[0].cards.set(i, new Card(Rank.ACE, Suit.Spade));
//		    				break;
//		    			case 4:
//		    				playerHands[0].cards.set(i, new Card(Rank.SIX, Suit.Diamond));
//		    				break;
//		    			case 5:
//		    				playerHands[0].cards.set(i, new Card(Rank.SEVEN, Suit.Club));
//		    				break;
//		    			case 6:
//		    				playerHands[0].cards.set(i, new Card(Rank.NINE, Suit.Club));
//		    				break;
//		    			case 7:
//		    				playerHands[0].cards.set(i, new Card(Rank.TWO, Suit.Spade));
//		    				break;
//		    			case 8:
//		    				playerHands[0].cards.set(i, new Card(Rank.TWO, Suit.Club));
//		    				break;
//		    			case 9:
//		    				playerHands[0].cards.set(i, new Card(Rank.TWO, Suit.Diamond));
//		    				break;
//		    			
//		    		}
//		    	}

		//    	for(int i = 0; i < 10; i++){
		//		switch (i){
		//			case 0:
		//				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Club));
		//				break;
		//			case 1:
		//				playerHands[1].cards.set(i, new Card(Rank.TEN, Suit.Club));
		//				break;
		//			case 2:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Diamond));
		//				break;
		//			case 3:
		//				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Diamond));
		//				break;
		//			case 4:
		//				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Spade));
		//				break;
		//			case 5:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Spade));
		//				break;
		//			case 6:
		//				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Club));
		//				break;
		//			case 7:
		//				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Heart));
		//				break;
		//			case 8:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Heart));
		//				break;
		//			case 9:
		//				playerHands[1].cards.set(i, new Card(Rank.JACK, Suit.Club));
		//				break;
		//			
		//		}
		//	}

		//PENULTIMATE CASE
		//    	for(int i = 0; i < 10; i++){
		//    		switch (i){
		//    			case 0:
		//    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Heart));
		//    				break;
		//    			case 1:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Diamond));
		//    				break;
		//    			case 2:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Diamond));
		//    				break;
		//    			case 3:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Diamond));
		//    				break;
		//    			case 4:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Spade));
		//    				break;
		//    			case 5:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Spade));
		//    				break;
		//    			case 6:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Spade));
		//    				break;
		//    			case 7:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Heart));
		//    				break;
		//    			case 8:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Heart));
		//    				break;
		//    			case 9:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Heart));
		//    				break;
		//    			
		//    		}
		//    	}

		//ULTIMATE CASE
		//    	for(int i = 0; i < 10; i++){
		//    		switch (i){
		//    			case 0:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Heart));
		//    				break;
		//    			case 1:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Diamond));
		//    				break;
		//    			case 2:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Spade));
		//    				break;
		//    			case 3:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Club));
		//    				break;
		//    			case 4:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Spade));
		//    				break;
		//    			case 5:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Club));
		//    				break;
		//    			case 6:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Diamond));
		//    				break;
		//    			case 7:
		//    				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Spade));
		//    				break;
		//    			case 8:
		//    				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Club));
		//    				break;
		//    			case 9:
		//    				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Diamond));
		//    				break;
		//    			
		//    		}
		//    	}

		//    	//5 OVERLAPPING CASE
		//    	for(int i = 0; i < 10; i++){
		//    		switch (i){
		//    			case 0:
		//    				playerHands[1].cards.set(i, new Card(Rank.NINE, Suit.Heart));
		//    				break;
		//    			case 1:
		//    				playerHands[1].cards.set(i, new Card(Rank.NINE, Suit.Diamond));
		//    				break;
		//    			case 2:
		//    				playerHands[1].cards.set(i, new Card(Rank.NINE, Suit.Spade));
		//    				break;
		//    			case 3:
		//    				playerHands[1].cards.set(i, new Card(Rank.JACK, Suit.Heart));
		//    				break;
		//    			case 4:
		//    				playerHands[1].cards.set(i, new Card(Rank.JACK, Suit.Diamond));
		//    				break;
		//    			case 5:
		//    				playerHands[1].cards.set(i, new Card(Rank.JACK, Suit.Spade));
		//    				break;
		//    			case 6:
		//    				playerHands[1].cards.set(i, new Card(Rank.TEN, Suit.Heart));
		//    				break;
		//    			case 7:
		//    				playerHands[1].cards.set(i, new Card(Rank.TEN, Suit.Spade));
		//    				break;
		//    			case 8:
		//    				playerHands[1].cards.set(i, new Card(Rank.QUEEN, Suit.Diamond));
		//    				break;
		//    			case 9:
		//    				playerHands[1].cards.set(i, new Card(Rank.KING, Suit.Diamond));
		//    				break;
		//    			
		//    		}
		//    	}

		//SIX OVERLAPPING CARDS
		//    	for(int i = 0; i < 10; i++){
		//		switch (i){
		//			case 0:
		//				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Heart));
		//				break;
		//			case 1:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Heart));
		//				break;
		//			case 2:
		//				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Heart));
		//				break;
		//			case 3:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Diamond));
		//				break;
		//			case 4:
		//				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Diamond));
		//				break;
		//			case 5:
		//				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Diamond));
		//				break;
		//			case 6:
		//				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Spade));
		//				break;
		//			case 7:
		//				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Spade));
		//				break;
		//			case 8:
		//				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Spade));
		//				break;
		//			case 9:
		//				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Club));
		//				break;
		//			
		//		}
		//	}

		//OTHER RANDOM CASE
//		    	for(int i = 0; i < 10; i++){
//		    		switch (i){
//		    			case 0:
//		    				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Heart));
//		    				break;
//		    			case 1:
//		    				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Diamond));
//		    				break;
//		    			case 2:
//		    				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Club));
//		    				break;
//		    			case 3:
//		    				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Spade));
//		    				break;
//		    			case 4:
//		    				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Heart));
//		    				break;
//		    			case 5:
//		    				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Diamond));
//		    				break;
//		    			case 6:
//		    				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Spade));
//		    				break;
//		    			case 7:
//		    				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Club));
//		    				break;
//		    			case 8:
//		    				playerHands[1].cards.set(i, new Card(Rank.NINE, Suit.Spade));
//		    				break;
//		    			case 9:
//		    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Club));
//		    				break;
//		    			
//		    		}
//		    	}


		//    	//SPLIT
		//    	for(int i = 0; i < 10; i++){
		//    		switch (i){
		//    			case 0:
		//    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Heart));
		//    				break;
		//    			case 1:
		//    				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Heart));
		//    				break;
		//    			case 2:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Heart));
		//    				break;
		//    			case 3:
		//    				playerHands[1].cards.set(i, new Card(Rank.FOUR, Suit.Heart));
		//    				break;
		//    			case 4:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Spade));
		//    				break;
		//    			case 5:
		//    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Diamond));
		//    				break;
		//    			case 6:
		//    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Heart));
		//    				break;
		//    			case 7:
		//    				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Heart));
		//    				break;
		//    			case 8:
		//    				playerHands[1].cards.set(i, new Card(Rank.SEVEN, Suit.Heart));
		//    				break;
		//    			case 9:
		//    				playerHands[1].cards.set(i, new Card(Rank.EIGHT, Suit.Heart));
		//    				break;
		//    			
		//    		}
		//    	}
		stock.moveTopCardTo(discard);
		//discard.add(new Card(Rank.KING,Suit.Heart));
	}

	/**
	 * Copy constructor for objects of class SJState. Makes a copy of the given state
	 *  
	 * @param orig  the state to be copied
	 */
	public GRState(GRState orig) {
		if (orig == null) return;
		// set index of player whose turn it is
		whoseTurn = orig.whoseTurn;
		playerHands[0] = new Deck(orig.playerHands[0]);
		playerHands[1] = new Deck(orig.playerHands[1]);
		playerMelds = new ArrayList<Hashtable<Integer, Meld>>();

		playerMelds.add(new Hashtable<Integer, Meld>());
		playerMelds.add(new Hashtable<Integer, Meld>());
		//PROBLEM LIES HERE
		for (Meld m : orig.playerMelds.get(0).values()){
			playerMelds.get(0).put(m.id, new Meld(m));
		}
		for (Meld m :  orig.playerMelds.get(1).values()){
			playerMelds.get(1).put(m.id, new Meld(m));
		}

		gameMessage = orig.gameMessage;
		stock = new Deck(orig.stock);
		discard = new Deck(orig.discard);
		turnPhase = orig.turnPhase;
		rounds = orig.rounds;
		playerScores[0] = orig.playerScores[0];
		playerScores[1] = orig.playerScores[1];
		ID = orig.ID;
		isEndOfRound = orig.isEndOfRound;
		//TODO add new stuff to copy constructor
	}

	/**
	 * Copy constructor for objects of class SJState. Makes a copy of the given state
	 *  
	 * @param orig  the state to be copied
	 */
	public GRState(GRState orig, int blah) {
		// set index of player whose turn it is
		whoseTurn = orig.whoseTurn;
		playerHands[0] = new Deck(orig.playerHands[0],1);
		playerHands[1] = new Deck(orig.playerHands[1],1);
		playerMelds = new ArrayList<Hashtable<Integer, Meld>>();

		playerMelds.add(new Hashtable<Integer, Meld>());
		playerMelds.add(new Hashtable<Integer, Meld>());
		//PROBLEM LIES HERE
		for (Meld m : orig.playerMelds.get(0).values()){
			playerMelds.get(0).put(m.id, new Meld(m));
		}
		for (Meld m :  orig.playerMelds.get(1).values()){
			playerMelds.get(1).put(m.id, new Meld(m));
		}

		gameMessage = orig.gameMessage;
		stock = new Deck(orig.stock);
		discard = new Deck(orig.discard);
		turnPhase = orig.turnPhase;
		rounds = orig.rounds;
		playerScores[0] = orig.playerScores[0];
		playerScores[1] = orig.playerScores[1];
		ID = orig.ID;
		isEndOfRound = orig.isEndOfRound;
		//TODO add new stuff to copy constructor
	}

	/**
	 * Starts a new round
	 */
	public void initNewRound(){
		//Nuke all current cards on screen
		Deck d = new Deck();
		playerHands[0].moveAllCardsTo(d);
		playerHands[1].moveAllCardsTo(d);
		stock.moveAllCardsTo(d);
		discard.moveAllCardsTo(d);

		lockGUI = false;
		//reset end of round
		isEndOfRound = false;

		//Reset phase, shuffle deck
		turnPhase = DRAW_PHASE;
		whoseTurn = toGoFirst;

		stock.add52();
		stock.shuffle();

		//Deal cards
		for(int i = 0; i < 10; i++){
			drawFrom(true,0);
			drawFrom(true,1);
		}

		//Flip top of stock onto discard
		stock.moveTopCardTo(discard);
	}

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

	public void setHand(int pidx, Deck d){
		playerHands[pidx] = new Deck(d);
	}

	public void setMelds(int pidx,  Hashtable<Integer, Meld> m){
		playerMelds.set(pidx, new Hashtable<Integer, Meld>(m));
	}

	public int getp1score(){
		return playerScores[0];
	}

	public int getp2score(){
		return playerScores[1];
	}

	public void setScore(int pidx, int addedScore){
		playerScores[pidx] += addedScore;
	}
	public Hashtable<Integer, Meld> getMeldsForPlayer(int pidx){
		if(pidx == 0){
			return playerMelds.get(0);
		}else if(pidx == 1){
			return playerMelds.get(1);
		}
		return null;
	}

	public ArrayList<Card> getDeadwoodForPlayer(int pidx) {    	
		ArrayList<Card> deadwoodCards = new ArrayList<Card>();
		for (Card c : playerHands[pidx].cards) {
			if (c.getRL() < 3 && c.getSL() < 3) deadwoodCards.add(c);
		}
		return deadwoodCards;
	}

	public boolean drawFrom(boolean fromStock, int playeridx){
		if(fromStock){
			setLastPicked(stock.peekAtTopCard());
			stock.moveTopCardTo(playerHands[playeridx]);
			setFromDiscard(false);
		} else {
			setLastPicked(discard.peekAtTopCard());
			discard.moveTopCardTo(playerHands[playeridx]);
			setFromDiscard(true);
		}

		//TODO discuss case where all cards are drawn
		if(stock.size() == 0){
			return false;
		}
		return true;
	}

	/*
	 * ERIC: Discard no longer automatically discards. It just removes a card from playerHands
	 * Then, the player is free to drag around the removed card to the discard pile.    
	 */
	public boolean discard(Card disCard, int playeridx){

		//ERIC: don't add discard automatically to discard. We want to drag the card. 
		discard.add(disCard);
		playerHands[playeridx].remove(disCard);
		return true;
	}

	/*
	 * ERIC: New method that sends a card to the discard pile. This can be called when the player
	 * has been dragging a card to discard and hovers it over the discard pile. 
	 */
	public boolean takeFromPlayer(Card disCard, int playeridx){

		//discard.add(disCard);
		playerHands[playeridx].remove(disCard);
		return true;
	}


	public Card getTopDiscard(){
		return discard.peekAtTopCard();
	}

	public void nullCardsFor(int playeridx){
		yourId = playeridx;
		if(playeridx == 0){
			if(!isEndOfRound){
				playerHands[1].nullifyDeck();
			}
			stock.nullifyDeck();
			// TODO nullify all but top of discard
		}else if(playeridx == 1){
			if(!isEndOfRound){
				playerHands[0].nullifyDeck();
			}
			stock.nullifyDeck();
		}

	}

	public Deck getStock() {
		return stock;
	}

	public Deck getDiscard() {
		return discard;
	}

	public boolean isFromDiscard() {
		return fromDiscard;
	}

	public void setFromDiscard(boolean fromDiscard) {
		this.fromDiscard = fromDiscard;
	}

	public Card getLastPicked() {
		return lastPicked;
	}

	public void setLastPicked(Card lastPicked) {
		this.lastPicked = lastPicked;
	}
}
