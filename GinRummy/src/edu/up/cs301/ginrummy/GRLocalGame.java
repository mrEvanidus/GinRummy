package edu.up.cs301.ginrummy;

import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.card.Rank;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.config.GameConfig;

/**
 * The LocalGame class for a slapjack game.  Defines and enforces
 * the game rules; handles interactions between players.
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */

public class GRLocalGame extends LocalGame implements GRGame {

	// the game's state
	GRState state;
	
	ArrayList<Card> layoffCards;
	int playerWhoLaidOff;
	int scoresForRound[];
	public static final int PLAYER_1 = 0;
	public static final int PLAYER_2 = 1;

	/**
	 * Constructor for the SJLocalGame.
	 */
	public GRLocalGame() {
		Log.i("SJLocalGame", "creating game");
		// create the state for the beginning of the game
		state = new GRState();
		layoffCards = new ArrayList<Card>();
		playerWhoLaidOff = 0;
		scoresForRound = new int[2];
	}


	/**
	 * checks whether the game is over; if so, returns a string giving the result
	 * 
	 * @result
	 * 		the end-of-game message, or null if the game is not over
	 */
	@Override
	protected String checkIfGameOver() {
		if( state.getp1score() >= 100){
			// TODO If display wrong player name, your bug is here
			return this.playerNames[0] + " is the winner";
		}

		if( state.getp2score() >= 100){
			return this.playerNames[1] + " is the winner";
		}
		return null;
	}

	/**
	 * sends the updated state to the given player. In our case, we need to
	 * make a copy of the Deck, and null out all the cards except the top card
	 * in the middle deck, since that's the only one they can "see"
	 * 
	 * @param p
	 * 		the player to which the state is to be sent
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		// if there is no state to send, ignore
		if (state == null) {
			return;
		}

		// make a copy of the state; null out all cards except for the
		// top card in the middle deck
		GRState stateForPlayer = new GRState(state); // copy of state
		stateForPlayer.nullCardsFor(getPlayerIdx(p)); // put nulls except for visible card

		// send the modified copy of the state to the player
		p.sendInfo(stateForPlayer);
		//p.sendInfo(state);
	}

	/**
	 * whether a player is allowed to move
	 * 
	 * @param playerIdx
	 * 		the player-number of the player in question
	 */
	protected boolean canMove(int playerIdx) {
		if (playerIdx < 0 || playerIdx > 1) {
			// if our player-number is out of range, return false
			return false;
		}
		else {
			// player can move if it's their turn, or if the middle deck is non-empty
			// so they can slap
			return state.whoseTurn() == playerIdx;
		}
	}

    /**
     * Determines whether or not a player can knock
     * 
     * @param hand the player's hand
     * @param melds the player's melds
     * 
     * @return Whether or not the hand is allowed to knock
     */
    public boolean canKnock(GRState statecopy, Deck hand,int pidx){
    	//Resolve any problem cards in more than one meld
    	removeDuplicates(statecopy, hand, pidx);
    	
    	//If the player's deadwood is less than 10, the player may knock
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
    public void removeDuplicates(GRState statecopy, Deck hand, int pidx){
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
    	//int minCombo = -1;  //Lowest deadwood count among permutations. if -1, hand is problem card free
    	Deck bestHand = new Deck(hand,1);
    	Deck handCopy = new Deck(hand,1);
    	Hashtable<Integer,Meld> melds = statecopy.getMeldsForPlayer(pidx);
    	Hashtable<Integer,Meld> meldsCopy = htcopy(melds);
    	Hashtable<Integer,Meld> bestMelds = new Hashtable<Integer,Meld>(melds);
    	//Generate all possible permutations
    	for(int i = 0; i < Math.pow(2,problemCards.size()); i++){
    		//Generates a hand based on the current permutation
    		int score = genHand(i,handCopy,meldsCopy,pidx);
    		//If the deadwood count of the permutation is less than the current min,
    		//update the min
    		if (score < minScore && score != -1){
    			minScore = score;
    			bestHand = new Deck(handCopy,1);
    			bestMelds = new Hashtable<Integer, Meld>(meldsCopy);
    		}
    		handCopy = new Deck(hand,1);
    		meldsCopy = htcopy(melds);
    	}
    	
    	statecopy.setHand(pidx, bestHand);
    	statecopy.setMelds(pidx, bestMelds);
    }

    public Hashtable<Integer, Meld> htcopy(Hashtable<Integer,Meld> h){
    	Hashtable<Integer,Meld> newHash = new Hashtable<Integer, Meld>();
    	
    	for(Meld m : h.values()){
    		Meld meld = new Meld(m);
    		newHash.put(m.id,meld);
    	}
    	
    	return newHash;
    	
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
    public int genHand(int idx, Deck handcopy, Hashtable<Integer,Meld> pMelds, int pidx){
    	
    	//Hashtable<Integer, Meld> pMelds = state.getMeldsForPlayer(pidx);
    	
    	// If the hand has problem cards...
    	if(idx != -1){
    		//Convert the given permutation index to a binary string
    		String comb = Integer.toBinaryString(idx);
    		//Reverse it to make it easier to pop digits off
    		String reversed = new StringBuilder(comb).reverse().toString();
    		
    		for(Card c : handcopy.cards){
    			//If the card is a problem card...
    			if(c.getRL() >= 3 && c.getSL() >= 3){
    				//If the current digit is 0...
    				if(reversed.equals("") || reversed.charAt(0) == '0'){
    					//Problem card is removed from its set
    					if(pMelds.get(c.setID) != null){
    						for(Card card : pMelds.get(c.setID).cards){
    							for(Card card1 : handcopy.cards){
    								if(card.equals(card1)){
    									card1.setSL(0);
    	    							card1.setID = 0;
    	    							card.setSL(0);
    	    							card.setID = 0;
    								}
    							}
    						}
    						pMelds.remove(c.setID);

    						if(!reversed.equals("")){
    							reversed = reversed.substring(1);
    						}
    					}
    				}
    				//If the current digit is 1...
    				else{
    					//Problem card is removed from its run
    					if(pMelds.get(c.runID) != null){
	    					for(Card card : pMelds.get(c.runID).cards){
	    						for(Card card1 : handcopy.cards){
    								if(card.equals(card1)){
    									card1.setRL(0);
    	    							card1.runID = 0;
    	    							card.setRL(0);
    	    							card.runID = 0;
    								}
    							}
	    					}
	    					pMelds.remove(c.runID);
	    		
	    					if(!reversed.equals("")){
	    						reversed = reversed.substring(1);
	    					}
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
    		if(c.runID != 0 && c.setID != 0){
    			return -1;
    		}
    		if(c.runID != 0 || c.setID != 0){
    			
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
    			(opState.getMeldsForPlayer(pidx)).put(state.ID, new Meld(a, true, val*a.size(), opState.ID));
    			opState.meldCount++;
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
    					(opState.getMeldsForPlayer(pidx)).put(state.ID, new Meld(temp, false, runCount, opState.ID));
	    				opState.ID++;
	    				opState.meldCount++;
	    				runCount = 0;
					}
    				
    				temp.clear();
    			}
    		}
    	}	
    }
    
    /**
     * Put the players hands into a normalized form
     * (sans duplicates, deadwood counted)
     */
    public void normalizeHands(){
    	assessMelds(state, 0);
		assessMelds(state, 1);
		removeDuplicates(state, state.getHand(0),0);
		removeDuplicates(state, state.getHand(1),1);
    }
    
    public void layoff(int knocker, int defender){
    	playerWhoLaidOff = defender;
    	
    	//This loop ensures that all possible deadwood is laid off
    	//If a player has a partial run of two card, both of which 
    	//can be added to the knocker's hand, the first iteration will
    	//lay off the first card and the second one will lay off the second
		for(int i = 0; i < 2; i++){
			
			ArrayList<Card> tempToAdd = new ArrayList<Card>();
			ArrayList<Card> tempToRemove = new ArrayList<Card>();
			
			for(Card c : state.getHand(defender).cards){
				if(c.runID == 0 && c.setID == 0){
					//Get the deadwood for the knocking player
					int dw = countDeadwood(state.getHand(knocker));
					int meldcount1 = 0;
					for(Meld m: state.getMeldsForPlayer(knocker).values()){
						meldcount1++;
					}
					//Create a temporary state before attempting to lay off
					GRState s = new GRState(state,1);

					//Hypothetically add the card to the hand, calculate deadwood
					s.getHand(knocker).cards.add(c);
					assessMelds(s, knocker);
					removeDuplicates(s, s.getHand(knocker),knocker);
					int dw2 = countDeadwood(s.getHand(knocker));

					int meldcount2 = 0;
					for(Meld m: s.getMeldsForPlayer(knocker).values()){
						meldcount2++;
					}
					//If card doesn't add any additional deadwood, the card may be 
					//added to a list of cards to be laid off
					if(dw == dw2 && (dw != -1 || dw2 != -1)){
						layoffCards.add(c);
						tempToAdd.add(c);
						tempToRemove.remove(c);
					}
				}
			}

			//Add deadwood cards to knocker's hand
			for(Card c : tempToAdd){
				state.getHand(knocker).cards.add(c);
			}
			//Remove deadwood cards from defender's hand
			for(Card c : tempToRemove){
				state.getHand(defender).remove(c);
			}
		}
    }
    
	/**
	 * makes a move on behalf of a player
	 * 
	 * @param action
	 * 		the action denoting the move to be made
	 * @return
	 * 		true if the move was legal; false otherwise
	 */
	@Override
	protected boolean makeMove(GameAction action) {

		// check that we have gin rummy action; if so cast it
		if (!(action instanceof GRMoveAction)) {
			return false;
		} 
		GRMoveAction grma = (GRMoveAction) action;

		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(grma.getPlayer());

		if (thisPlayerIdx < 0) { // illegal player
			return false;
		}

		if(grma.isNextRound() && state.isEndOfRound){
			if (grma instanceof GRNewGameAction) {
				state.setScore(0, 0);
				state.setScore(1, 0);
			}
			state.initNewRound();
			sendAllUpdatedState();
			return true;
		}
		
		if(canMove(thisPlayerIdx)){
			//DRAW PHASE
			if (grma.isDraw() && state.getPhase() == GRState.DRAW_PHASE) {
				//If there are 2 two cards in the stockpile, begin a new round
				if(state.getStock().size() <= 3){
					state.initNewRound();
				}

				GRDrawAction da = (GRDrawAction) action;
				state.drawFrom(da.fromStock(), thisPlayerIdx);
				state.setPhase(state.DISCARD_PHASE);
			}
			//DISCARD PHASE
			else if (grma.isDiscard() && state.getPhase() == GRState.DISCARD_PHASE) {

				GRDiscardAction da = (GRDiscardAction) action;
				if(!(state.isFromDiscard() && state.getLastPicked().equals(da.discardCard()))){
					// Remove the requested card from the player's hand and place it atop the discard pile
					state.discard(da.discardCard(), thisPlayerIdx);

					//state.assessMelds(thisPlayerIdx);
					//state.canKnock(state.getHand(thisPlayerIdx),state.getMeldsForPlayer(thisPlayerIdx));


					//Set the turn to the other player and set phase to draw phase
					state.setPhase(state.DRAW_PHASE);
					if(state.whoseTurn() == PLAYER_1){
						state.setWhoseTurn(PLAYER_2);
					}
					else if (state.whoseTurn() == PLAYER_2){
						state.setWhoseTurn(PLAYER_1);
					} else {
						// never get here
					}
				}else {
					return false;
				}
			}
			//KNOCK PHASE 
			else if (grma.isKnock() && state.getPhase() == GRState.DISCARD_PHASE){

				GRState copy = new GRState(state);
				//(GRKnockAction)grma.knockCard();
				GRKnockAction copy_grma = (GRKnockAction)grma;
				Card theCard = copy_grma.knockCard();
				copy.getHand(thisPlayerIdx).remove(theCard);

				assessMelds(copy, thisPlayerIdx);
				if(canKnock(copy, copy.getHand(thisPlayerIdx), thisPlayerIdx)){

					state.toGoFirst = thisPlayerIdx;
					state.isEndOfRound = true;
					state.setPhase(state.DRAW_PHASE);
					state.setWhoseTurn(0);
					sendAllUpdatedState();
					//state.setPhase(state.DRAW_PHASE);
					state.getHand(thisPlayerIdx).remove(theCard);

					normalizeHands();

					//ArrayList<Card> layoffCards = new ArrayList<Card>();
					//int playerWhoLaidOff = 0;
					
					if(thisPlayerIdx == PLAYER_1){
						playerWhoLaidOff = PLAYER_1;
						layoff(PLAYER_1,PLAYER_2);
					}else{
						playerWhoLaidOff = PLAYER_2;
						layoff(PLAYER_2,PLAYER_1);
					}

					normalizeHands();
					//					
					//Get the deadwood
					int p0dw = countDeadwood(state.getHand(0));
					int p1dw = countDeadwood(state.getHand(1));

					//TODO: MAKE SEPARATE FUNCTION
					if(p0dw < p1dw){
						if(p0dw == 0){
							state.setScore(0,20);
						}
						state.setScore(0,p1dw - p0dw);	
						if(thisPlayerIdx == 1){
							//Give undercut points
							state.setScore(0,10);
						}
					}else if (p0dw > p1dw){
						if(p1dw == 0){
							state.setScore(1,20);
						}
						state.setScore(1,p0dw - p1dw);	
						if(thisPlayerIdx == 0){
							//Give undercut points
							state.setScore(1,10);
						}
					}else{
						//If both players scores are equal
						if(p0dw == 0 && p1dw == 0){
							//If both players have gin
							//Give the player who knocks 20 pts
							state.setScore(thisPlayerIdx, 20);
						}
						else{
							if(thisPlayerIdx == 0){
								state.setScore(1,10);
							}else{
								state.setScore(0,10);
							}
						}
					}
					
					//TODO: Here you go, Jaimiey
					state.gameMessage = "Player 1 score: " + state.getp1score() 
							+ "\nPlayer 2 score: "+ state.getp2score() + "\n";
					
					String layoffPlayer = "";
					if(playerWhoLaidOff == 0){
						layoffPlayer = "Player 1";
					}else {
						layoffPlayer = "Player 2";
					}
					
					if(layoffCards.size() > 0){
						state.gameMessage = state.gameMessage + layoffPlayer + 
								" laid off:\n";
						for(Card c : layoffCards){
							state.gameMessage = state.gameMessage + c.toString()+"\n";
						}
					}
					
					sendAllUpdatedState();
				}
				else{
					return false;
				}
			}else if(grma.isNewGame()){
				return true;
			}
			else {
				return false;
			}
		}

		//Team Up the River was here 
		sendAllUpdatedState();
		// return true, because the move was successful if we get here
		return true;
	}
}
