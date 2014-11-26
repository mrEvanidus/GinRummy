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
    
	private Deck[] playerHands = new Deck[2];
	private int[] playerScores = new int[2];
	private ArrayList<ArrayList<Meld>> playerMelds;
	
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
    private int ID;
    
    private String gameMessage;
    
    //mystery variable!!
    private boolean fromDiscard;
    
    private Card lastPicked;
    
    private boolean lastMoveWasDraw;
    
    /**
     * Constructor for objects of class SJState. Initializes for the beginning of the
     * game, with a random player as the first to turn card
     *  
     */
    public GRState() {
    	
    	//initialize the meld lists
    	playerMelds = new ArrayList<ArrayList<Meld>>();
    	playerMelds.add(new ArrayList<Meld>());
    	playerMelds.add(new ArrayList<Meld>());
    	
    	isEndOfRound = false;
    	
    	// randomly pick the player who starts
    	//whoseTurn = (int)(2*Math.random());
    	whoseTurn = 0;
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
    				playerHands[0].cards.set(i, new Card(Rank.FOUR, Suit.Spade));
    				break;
    			case 1:
    				playerHands[0].cards.set(i, new Card(Rank.FOUR, Suit.Club));
    				break;
    			case 2:
    				playerHands[0].cards.set(i, new Card(Rank.SEVEN, Suit.Heart));
    				break;
    			case 3:
    				playerHands[0].cards.set(i, new Card(Rank.EIGHT, Suit.Heart));
    				break;
    			case 4:
    				playerHands[0].cards.set(i, new Card(Rank.NINE, Suit.Heart));
    				break;
    			case 5:
    				playerHands[0].cards.set(i, new Card(Rank.TEN, Suit.Heart));
    				break;
    			case 6:
    				playerHands[0].cards.set(i, new Card(Rank.JACK, Suit.Heart));
    				break;
    			case 7:
    				playerHands[0].cards.set(i, new Card(Rank.QUEEN, Suit.Heart));
    				break;
    			case 8:
    				playerHands[0].cards.set(i, new Card(Rank.ACE, Suit.Club));
    				break;
    			case 9:
    				playerHands[0].cards.set(i, new Card(Rank.FOUR, Suit.Diamond));
    				break;
    			
    		}
    	}
    	
    	for(int i = 0; i < 10; i++){
    		switch (i){
    			case 0:
    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Spade));
    				break;
    			case 1:
    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Heart));
    				break;
    			case 2:
    				playerHands[1].cards.set(i, new Card(Rank.ACE, Suit.Diamond));
    				break;
    			case 3:
    				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Heart));
    				break;
    			case 4:
    				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Spade));
    				break;
    			case 5:
    				playerHands[1].cards.set(i, new Card(Rank.TWO, Suit.Club));
    				break;
    			case 6:
    				playerHands[1].cards.set(i, new Card(Rank.KING, Suit.Heart));
    				break;
    			case 7:
    				playerHands[1].cards.set(i, new Card(Rank.FIVE, Suit.Heart));
    				break;
    			case 8:
    				playerHands[1].cards.set(i, new Card(Rank.SIX, Suit.Heart));
    				break;
    			case 9:
    				playerHands[1].cards.set(i, new Card(Rank.THREE, Suit.Diamond));
    				break;
    			
    		}
    	}
    	
    	stock.moveTopCardTo(discard);
    	//discard.add(new Card(Rank.KING,Suit.Heart));
    }
    
    /**
     * Copy constructor for objects of class SJState. Makes a copy of the given state
     *  
     * @param orig  the state to be copied
     */
    public GRState(GRState orig) {
    	// set index of player whose turn it is
    	whoseTurn = orig.whoseTurn;
    	playerHands[0] = new Deck(orig.playerHands[0]);
    	playerHands[1] = new Deck(orig.playerHands[1]);
    	playerMelds = new ArrayList<ArrayList<Meld>>();
    	playerMelds.add(new ArrayList<Meld>(orig.playerMelds.get(0)));
    	playerMelds.add(new ArrayList<Meld>(orig.playerMelds.get(1)));
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
     * Determines whether or not a player can knock
     * 
     * @param hand the player's hand
     * @param melds the player's melds
     * 
     * @return Whether or not the hand is allowed to knock
     */
    public boolean canKnock(Deck hand, ArrayList<Meld> melds){
    	//Problem cards are those that are in both a set and a run
    	ArrayList<Card> problemCards = new ArrayList<Card>();
    	Deck handcopy = hand;
    	
    	//Get a list of all problem cards 
    	for(Card c : handcopy.cards){
    		//if the given card is a problem card...
    		if(c.getRL() >=3 && c.getSL() >= 3){
    			c.isProblem = true;
    			problemCards.add(c);
    		}
    	}
  
    	ArrayList<Card> pcc = problemCards;
    	ArrayList<Meld> meldsCopy = melds;
    	int minScore = 101; //Highest possible deadwood count is 100
    	int minCombo = -1;  //Lowest deadwood count among permutations. if -1, hand is problem card free
    	
    	//Generate all possible permutations
    	for(int i = 0; i < Math.pow(2,problemCards.size()); i++){
    		//Generates a hand based on the current permutation
    		int score = genHand(i,hand);
    		//If the deadwood count of the permutation is less than the current min,
    		//update the min
    		if (score < minScore && score != -1){
    			minScore = score;
    		}
    	}
    	
    	//Get the deadwood count for the hand
    	int deadwoodCount = genHand(minCombo,hand);
    	
    	if(deadwoodCount <= 10){
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    //TODO: THIS IS NOT TOTALLY WORKING
    /**
     * Generates a permutation of the given hand based on the given index.
     * The index is converted to a binary string where each binary digit represents
     * one of the problem cards in the hand. If the digit is '0', the card stays in its run
     * and is removed from its set and vice versa for if the digit is '1';
     * 
     * @param idx The permutation to generate
     * @param hand The hand to generate the permutation of
     * 
     * @return the deadwood count of the hand
     */
    public int genHand(int idx, Deck hand){
    	
    	Deck handcopy = hand;
    	
    	// If the hand has problem cards...
    	if(idx != -1){
    		//Convert the given permutation index to a binarry string
    		String comb = Integer.toBinaryString(idx);
    		//Reverse it to make it easier to pop digits off
    		String reversed = new StringBuilder(comb).reverse().toString();
    		
    		for(Card c : handcopy.cards){
    			//If the card is a problem card...
    			if(c.getRL() >= 3 && c.getSL() >= 3){
    				//If the current digit is 0...
    				if(reversed.equals("") || reversed.charAt(0) == '0'){
    					//Problem card is removed from its set
    					c.setSL(0);
    					c.setID = 0;
    					if(!reversed.equals("")){
    						reversed = reversed.substring(1);
    					}
    				}
    				//If the current digit is 1...
    				else{
    					//Problem card is removed from its run
    					c.setRL(0);
    					c.runID = 0;
    					if(!reversed.equals("")){
    						reversed = reversed.substring(1);
    					}
    				}
    			}
    		}
    	}
    	//Count the deadwood
    	int dc = 0;
    	for(Card c : handcopy.cards){
    		if(c.getRL() >= 3 && c.getSL() >= 3){
    			return -1;
    		}
    		if(c.getRL() >= 3 || c.getSL() >= 3){
    			
    		}else{
    			if(c.getRank().value(1) <= 10){
    				dc += c.getRank().value(1);
    			}else{
    				dc += 10;
    			}
    		}
    	}
    	return dc;
    }
    
    /**
     * Look at a player's hand and determine the melds that it contains
     * 
     * @param pidx the player's hand to assess
     */
    public void assessMelds(int pidx){
    	//Store cards by rank
    	ArrayList<ArrayList<Card>> ranks = new ArrayList<ArrayList<Card>>();
    	for(int i = 0; i < 13; i++){
    		ranks.add(new ArrayList<Card>());
    	}
    	//Store cards by suit
    	ArrayList<ArrayList<Card>> suits = new ArrayList<ArrayList<Card>>();
    	for(int i = 0; i < 4; i++){
    		ArrayList<Card> temp= new ArrayList<Card>();
    		for(int j = 0; j < 14; j++){
    			temp.add(null);
    		}
    		suits.add(temp);
    	}
    	
    	// SET DETECTION
    	//put cards cards in arraylists of same rank
    	for( Card c : playerHands[pidx].cards){
    		ranks.get(c.getRank().value(1) - 1).add(c);
    	}
    	
    	//for each card of the same rank...
    	for(ArrayList<Card> a : ranks){
    		int val = 0;
    		for(Card c : a){
    			//for EVERY card, set the "set length" whether or not it's in a meld
    			c.setSL(a.size());
    		}
    		if(a.size() >= 3){
    			//If the rank is a meld, add a new meld with the cards of this rank
    			for(Card c : a){
    				c.setID = ID;
    				val = c.getRank().value(1);
        		}
				playerMelds.get(pidx).add(new Meld(a, true, val*a.size(), ID));
				ID++;
			}
    		
    		
    	}
    	
    	// RUN DETECTION
    	
    	// Sort hand into suits
    	for( Card c : playerHands[pidx].cards){
    		if(c.getSuit().shortName() == 'C'){
    			suits.get(0).set(c.getRank().value(1) - 1, c);;
    		}
    		else if(c.getSuit().shortName() == 'D'){
    			suits.get(1).set(c.getRank().value(1) - 1, c);
    		}
    		else if(c.getSuit().shortName() == 'H'){
    			suits.get(2).set(c.getRank().value(1) - 1, c);
    		}
    		else if(c.getSuit().shortName() == 'S'){
    			suits.get(3).set(c.getRank().value(1) - 1, c);
    		}
    		else {
    			//this is a bad
    		}
    	}
    	
    	//Check a suit to see if it has any runs
    	for(int i = 0; i < 4; i++){
    		int runCount = 0;
    		ArrayList<Card> suit = suits.get(i);
    		ArrayList<Card> temp = new ArrayList<Card>();
    		for (Card c : suit){
    			if(c != null){
    				//add the card to the array of the current run
    				temp.add(c);
    			}else {
    				//If the current card is null, add the length
    				//of the run to each card's runLength
    				int tempLength = temp.size();
    				for (Card c2 : temp){
    					c2.setRL(tempLength);
    				}
    				if(tempLength >= 3){
    					//For cards in a meld, store info about the melds they are in
    					//and add them to a new meld
    					for (Card c2 : temp){
    						runCount += c2.getRank().value(1);
        					c2.runID = ID;
        				}
	    				playerMelds.get(pidx).add(new Meld(temp, false, runCount, ID));
	    				ID++;
	    				runCount = 0;
					}
    				
    				temp.clear();
    			}
    		}
    	}	
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
    	
    	//Reset phase, shuffle deck
    	turnPhase = DRAW_PHASE;
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
    
    public void setScore(int pidx, int addedScore){
    	playerScores[pidx] += addedScore;
    }
    public ArrayList<Meld> getMeldsForPlayer(int pidx){
    	if(pidx == 0){
    		return playerMelds.get(0);
    	}else if(pidx == 1){
    		return playerMelds.get(1);
    	}
    	return null;
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
    	if(playeridx == 0){
    		playerHands[1].nullifyDeck();
    		stock.nullifyDeck();
    		// TODO nullify all but top of discard
    	}else if(playeridx == 1){
    		playerHands[0].nullifyDeck();
    		stock.nullifyDeck();
    	}
    	
    }

	public Deck getStock() {
		// TODO Auto-generated method stub
		return stock;
	}

	public Deck getDiscard() {
		// TODO Auto-generated method stub
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
