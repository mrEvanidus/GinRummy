package edu.up.cs301.ginrummy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.TimerInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013 
 */
public class GRComputerPlayerSmart extends GameComputerPlayer {	
	//public static final int THIS_PLAYER = 1;
	public static final int MELD = 0;
	public static final int ONE_HALF_MELD = 10;
	public static final int TWO_HALF_MELD = 5;
	public static final int SINGLETON = 20;
	
	// the most recent state of the game
	private GRState savedState;
	private Random randDeck;
	private Random randCard;
	
	private int thisPlayer;
	
	
    /**
     * Constructor for the SJComputerPlayer class; creates an "average"
     * player.
     * 
     * @param name
     * 		the player's name
     */
    
    /*
     * Constructor for the SJComputerPlayer class
     */
    public GRComputerPlayerSmart(String name) {
        // invoke superclass constructor
        super(name);
        randDeck = new Random();
        randCard = new Random();
        
    }
    
    public Card cardToDiscard(Deck hand){
    	for(Card c : hand.cards){
    		if(c.getRL() >= 3 || c.getSL() >= 3){
    			c.priority = MELD;
    		}else if(c.getRL() ==  1 && c.getSL() == 1){
    			c.priority = SINGLETON;
    		}else if(c.getRL() == 2 && c.getSL() == 2){
    			c.priority = TWO_HALF_MELD;
    		}else if(c.getRL() == 2 || c.getSL() == 2){
    			c.priority = ONE_HALF_MELD;
    		}else{
    			c.priority = 20;
    		}
    		
    		if(c.getRank().value(1) < 10){
    			c.priority *= c.getRank().value(1);
    		}else{
    			c.priority *= 10;
    		}
    	}
    	Card theCard = null;
    	int highest = 0;
    	for(Card c : hand.cards){
    		if(c.priority > highest){
    			highest = c.priority;
    			theCard = new Card(c);
    		}
    	}
    	
    	return theCard;
    }
    /**
     * Determines whether or not a player can knock
     * 
     * @param hand the player's hand
     * @param melds the player's melds
     * 
     * @return Whether or not the hand is allowed to knock
     */
    public boolean canKnock(Deck hand, Hashtable<Integer, Meld> melds, int pidx){
    	removeDuplicates(hand,pidx);
    	
    	if(countDeadwood(hand) <= 10){
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
     * 
     * @param hand
     */
    public void removeDuplicates(Deck hand, int pidx){
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
    }
    
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
    	
    	return countDeadwood(handcopy);
    }
    
    /**
     * countDeadwood
     * 
     */
    public int countDeadwood(Deck hand){
    	//Count the deadwood
    	int dc = 0;
    	for(Card c : hand.cards){
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
    public void assessMelds(GRState opState, int pidx){
    	(opState.getMeldsForPlayer(pidx)).clear();
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
    	for( Card c : (opState.getHand(pidx)).cards){
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
    				c.setID = opState.ID;
    				val = c.getRank().value(1);
        		}
    			(opState.getMeldsForPlayer(pidx)).put(savedState.ID, new Meld(a, true, val*a.size(), opState.ID));
    			savedState.meldCount++;
				opState.ID++;
			}
    		
    		
    	}
    	
    	// RUN DETECTION
    	
    	// Sort hand into suits
    	for( Card c : (opState.getHand(pidx)).cards){
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
        					c2.runID = opState.ID;
        				}
    					(opState.getMeldsForPlayer(pidx)).put(savedState.ID, new Meld(temp, false, runCount, opState.ID));
	    				opState.ID++;
	    				savedState.meldCount++;
	    				runCount = 0;
					}
    				
    				temp.clear();
    			}
    		}
    	}	
    }
    
    
    /**
     * callback method, called when we receive a message, typically from
     * the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
    	
    	// if we don't have a game-state, ignore
    	if (!(info instanceof GRState)) {
    		return;
    	}
    	
//    	if(savedState != null && savedState.isEndOfRound){
//    		return;
//    	}
    	
    	// update our state variable
    	savedState = (GRState)info;
    	
    	thisPlayer = savedState.yourId;
    	
    	//Check if it's this players turn
    	if(savedState.whoseTurn() == thisPlayer){
    		
    		
    		//DRAW PHASE
    		if(savedState.getPhase() == savedState.DRAW_PHASE){
    			//delay
        		try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			synchronized(this){
    				assessMelds(savedState, thisPlayer);
    				canKnock(savedState.getHand(thisPlayer), savedState.getMeldsForPlayer(thisPlayer),thisPlayer);

    				Card topOfDiscard = savedState.getDiscard().peekAtTopCard();
    				GRState copy = new GRState(savedState,1);

    				copy.getHand(thisPlayer).add(topOfDiscard);
    				assessMelds(copy, thisPlayer);
    				canKnock(copy.getHand(thisPlayer), copy.getMeldsForPlayer(thisPlayer),thisPlayer);

    				Card dc = cardToDiscard(copy.getHand(thisPlayer));

    				boolean drawFromStock;
    				if(dc.equals(topOfDiscard)){
    					drawFromStock = true;
    				}else{
    					drawFromStock = false;
    				}

    				game.sendAction(new GRDrawAction(this,drawFromStock));
    			}
    		} 
    		//DISCARD PHASE
    		else if (savedState.getPhase() == savedState.DISCARD_PHASE && !savedState.isEndOfRound){
    			//delay
        		try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			synchronized(this){
    				GRState s = new GRState(savedState,1);
    				Card c = cardToDiscard(s.getHand(thisPlayer));
    				s.getHand(thisPlayer).remove(c);
    				
    				assessMelds(s, thisPlayer);
    				canKnock(s.getHand(thisPlayer), s.getMeldsForPlayer(thisPlayer),thisPlayer);
    				
    				if(canKnock(s.getHand(thisPlayer), s.getMeldsForPlayer(thisPlayer), thisPlayer)){
    					game.sendAction(new GRKnockAction(this,c));
    				}else{
    					game.sendAction(new GRDiscardAction(this, c));
    				}
    			}
    		}
    	}
    }
}
