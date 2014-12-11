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

	private Deck stock;   //The stockpile
	private Deck discard; //The discard pile

	private Deck[] playerHands = new Deck[2]; //The players' hands
	private int[] playerScores = new int[2];  //The players' score
	private ArrayList<Hashtable<Integer, Meld>> playerMelds; //The players' melds

	// whose turn is it to turn a card?
	private int whoseTurn;

	// which part of the turn is it?
	// 0 = draw
	// 1 = discard
	private int turnPhase;
	private int rounds; //The number of round that have passed

	public boolean isEndOfRound; //Is it the end of the round?
	public int ID; //The current ID that the state will assign to a Meld
	public String gameMessage; //A message to be sent to a player
	private boolean fromDiscard; //Was the last card drawn from the discard pile
	private Card lastPicked; //What was the last card that was drawn

	public int toGoFirst; //Who is the next person to go first
	public int yourId;    //Let's the GUI know which indexs a player is at

	//Keeps track of the number of melds currently in both players hands
	//This seems like a weird thing to keep, but it's helpful in determining
	//if a card may be laid off
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
		
		stock.moveTopCardTo(discard);
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
	}

	/**
	 * Copy constructor for objects of class SJState. Makes a copy of the given state
	 *  
	 * @param orig  the state to be copied
	 * @param noReferences Um, so we need a copy constructor that makes NEW cards rather 
	 * 					   than modifying references to the original card. Having an int
	 * 				       in the copy ctor lets us use this version of the copy ctor
	 */
	public GRState(GRState orig, int noReferences) {
		// set index of player whose turn it is
		whoseTurn = orig.whoseTurn;
		//Copy hands
		playerHands[0] = new Deck(orig.playerHands[0],1);
		playerHands[1] = new Deck(orig.playerHands[1],1);
		playerMelds = new ArrayList<Hashtable<Integer, Meld>>();
		
		//Copy melds
		playerMelds.add(new Hashtable<Integer, Meld>());
		playerMelds.add(new Hashtable<Integer, Meld>());

		for (Meld m : orig.playerMelds.get(0).values()){
			playerMelds.get(0).put(m.id, new Meld(m));
		}
		for (Meld m :  orig.playerMelds.get(1).values()){
			playerMelds.get(1).put(m.id, new Meld(m));
		}

		//Copy other various things
		gameMessage = orig.gameMessage;
		stock = new Deck(orig.stock);
		discard = new Deck(orig.discard);
		turnPhase = orig.turnPhase;
		rounds = orig.rounds;
		playerScores[0] = orig.playerScores[0];
		playerScores[1] = orig.playerScores[1];
		ID = orig.ID;
		isEndOfRound = orig.isEndOfRound;
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
	 * Nullifies appropriate cards when sending a copy of the state to a player
	 * 
	 * @param playeridx the player we are sending the state to
	 */
	public void nullCardsFor(int playeridx){
		yourId = playeridx;
		if(playeridx == 0){
			if(!isEndOfRound){
				playerHands[1].nullifyDeck();
			}
			stock.nullifyDeck();
		}else if(playeridx == 1){
			if(!isEndOfRound){
				playerHands[0].nullifyDeck();
			}
			stock.nullifyDeck();
		}

	}
	
	/**
	 * Gets the deadwood count for a specified player
	 * 
	 * @param pidx the player's index
	 * @return the player's deadwood count
	 */
	public ArrayList<Card> getDeadwoodForPlayer(int pidx) {    	
		ArrayList<Card> deadwoodCards = new ArrayList<Card>();
		for (Card c : playerHands[pidx].cards) {
			if (c.getRL() < 3 && c.getSL() < 3) deadwoodCards.add(c);
		}
		return deadwoodCards;
	}

	/**
	 * Handles a draw
	 * 
	 * @param fromStock Was the card drawn from the stockpile
	 * @param playeridx The player making the draw
	 * 
	 * @return true if the draw is possible, false otherwise
	 */
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

		//If all cards in the pile are drawn...
		if(stock.size() == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * Handles a discard
	 * 
	 * @param disCard the card to be discarded
	 * @param playeridx the player discarding
	 * @return true if the discard was successful
	 */
	public boolean discard(Card disCard, int playeridx){
		discard.add(disCard);
		playerHands[playeridx].remove(disCard);
		return true;
	}

	/**
	 * New method that sends a card to the discard pile. This can be called when the player
	 * has been dragging a card to discard and hovers it over the discard pile. 
	 * 
	 * @param disCard The card to be discarded
	 * @param playeridx The player who is discarding
	 * 
	 * @return true if the discard was successful
	 */
	public boolean takeFromPlayer(Card disCard, int playeridx){
		playerHands[playeridx].remove(disCard);
		return true;
	}
	
	
	/////////////////////////GETTER/SETTER METHODS////////////////////////////////////////////////
	public int whoseTurn() {
		return whoseTurn;
	}

	public int getPhase() {
		return turnPhase;
	}

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

	public Card getTopDiscard(){
		return discard.peekAtTopCard();
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
