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
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
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
	public boolean canKnock(GRState statecopy, Deck hand, int pidx){
		//Resolve any problem cards in more than one meld
		removeDuplicates(statecopy, hand, pidx);

		//If the player's deadwood is less than 10, the player may knock
		if(countDeadwood(statecopy.getHand(pidx)) <= 10){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Resolves any cards that are in both a run and a set at once
	 * 
	 * @param statecopy the state to perform the changes on
	 * @param hand the hand to resolve
	 * @param pidx the index of the player to check
	 */
	public void removeDuplicates(GRState statecopy, Deck hand, int pidx){
		//Problem cards are those that are in both a set and a run
		int numProblemCards = 0;

		//Get a list of all problem cards 
		for(Card c : hand.cards){
			//if the given card is a problem card...
			if(c.getRL() >=3 && c.getSL() >= 3){
				numProblemCards++;
			}
		}

		int minScore = 101; //Highest possible deadwood count is 100
		Deck bestHand = new Deck(hand,1);
		Deck handCopy = new Deck(hand,1);

		//Make copies of the melds and hand so we can make hypothetical changes
		Hashtable<Integer,Meld> melds = statecopy.getMeldsForPlayer(pidx);
		Hashtable<Integer,Meld> meldsCopy = htcopy(melds);
		Hashtable<Integer,Meld> bestMelds = new Hashtable<Integer,Meld>(melds);

		//Generate all possible permutations
		for(int i = 0; i < Math.pow(2,numProblemCards); i++){
			//Generates a hand based on the current permutation
			int score = genHand(i,handCopy,meldsCopy, statecopy, pidx);
			//If the deadwood count of the permutation is less than the current min,
			//update the min
			if (score < minScore && score != -1){
				minScore = score;
				bestHand = new Deck(handCopy,1);
				bestMelds = htcopy(meldsCopy);
			}
			//Reset hypothetical hand and melds
			handCopy = new Deck(hand,1);
			meldsCopy = htcopy(melds);
		}
		//Set the hand and melds to the optimal combinations
		hand = bestHand;
		statecopy.setHand(pidx, bestHand);
		statecopy.setMelds(pidx, bestMelds);
	}

	/**
	 * Just another consequence of putting attributes in the Card. Since by default the Hashtable
	 * just keeps references to the original melds instead of making copies, we have to make our 
	 * own function that copies a hashtable the way we want it to... :(
	 * 
	 * @param h the hashtable we want to copy
	 * @return a copy of the hashtable
	 */
	public Hashtable<Integer, Meld> htcopy(Hashtable<Integer,Meld> h){
		Hashtable<Integer,Meld> newHash = new Hashtable<Integer, Meld>();

		//Makes actual copies of the melds and puts them in a new hashtable
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
	public int genHand(int idx, Deck handcopy, Hashtable<Integer,Meld> pMelds, GRState opState, int pidx){

		//WARNING: This method is pretty hairy. Proceed at your own risk. 

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
							//Decrement the length of the set by one
							for(Card c1 : pMelds.get(c.setID).cards){
								for(Card c2 : handcopy.cards){
									if(c1.equals(c2)){
										c2.setSL(c2.getSL() - 1);
									}
								}
							}
							//If the length of a set is less than 3, dissolve the set
							if( c.getSL() < 3){
								int a = c.setID;
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
								pMelds.remove(a);
							}else {
								//Otherwise if the length of the set is still greater than
								//or equal to 3, a set still exists so we keep it
								pMelds.get(c.setID).remove(c);
								c.setID = 0;
								c.setSL(0);
							}

							if(!reversed.equals("")){
								reversed = reversed.substring(1);
							}
						}
					}
					//If the current digit is 1...
					else{
						//Problem card is removed from its run
						if(pMelds.get(c.runID) != null){
							//Decrement the run length
							c.setRL(c.getRL() -1);
							int a = c.runID;

							//If the run length becomes less than 3, dissolve the run
							if(c.getRL() < 3){
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
								pMelds.remove(a);
							}
							else{

								//OTHERWISE, this is where it gets a bit messy:
									//We have to account for the possibility that the run is now split.
								//Essentially, we have to take the what's left of the run and see if 
								//any runs can be made from it. Not pretty.

								//Remove the problem card from the run
								pMelds.get(c.runID).remove(c);
								int useThis = c.runID;
								c.runID = 0;
								c.setRL(0);

								//Reset all run lengths and IDs
								for(Card card : pMelds.get(useThis).cards){
									for(Card card1 : handcopy.cards){
										if(card.equals(card1)){
											card1.setRL(0);
											card1.runID = 0;
											card.setRL(0);
											card.runID = 0;
										}
									}
								}

								//Create a list of all possible ranks in the suit
								ArrayList<Card> thisMeld = new ArrayList<Card>();
								for(int j = 0; j < 14; j++){
									thisMeld.add(null);
								}
								//Add each leftover card in the meld to the list
								for(Card c1 : pMelds.get(useThis).cards){
									thisMeld.set(c1.getRank().value(1) - 1, c1);
								}
								//Remove the old meld from the list of melds
								pMelds.remove(useThis);

								int runCount = 0;
								ArrayList<Card> temp = new ArrayList<Card>();
								for (Card c1 : thisMeld){
									if(c1 != null){
										//add the card to the array of the current run
										temp.add(c1);
									}else {
										//If the current card is null, add the length
										//of the run to each card's runLength
										int tempLength = temp.size();
										for (Card c2 : temp){
											for(Card c3 : handcopy.cards){
												if(c2.equals(c3)){
													c3.setRL(tempLength);
												}
											}
										}
										if(tempLength >= 3){
											//For cards in a meld, store info about the melds they are in
											//and add them to a new meld
											for (Card c2 : temp){
												runCount += c2.getRank().value(1);
												for(Card c3 : handcopy.cards){
													if(c2.equals(c3)){
														c3.runID = opState.ID;
													}
												}
											}
											pMelds.put(opState.ID, new Meld(temp, false, runCount, opState.ID));
											opState.ID++;
											opState.meldCount++;
											runCount = 0;
										}

										temp.clear();
									}
								}

								if(!reversed.equals("")){
									reversed = reversed.substring(1);
								}
							}
						}
					}
				}
			}
		}

		//Sorry you had to see that^^^
		opState.setMelds(pidx, pMelds);
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
				(opState.getMeldsForPlayer(pidx)).put(opState.ID, new Meld(a, true, val*a.size(), opState.ID));
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

						(opState.getMeldsForPlayer(pidx)).put(opState.ID, new Meld(temp, false, runCount, opState.ID));
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

	/**
	 * TODO Comment
	 * @param knocker
	 * @param defender
	 */
	public void layoff(int knocker, int defender){
		playerWhoLaidOff = defender;

		//Reset layoff cards
		for(Card c: state.getHand(knocker).cards){
			c.layoffCard = false;
		}
		for(Card c: state.getHand(defender).cards){
			c.layoffCard = false;
		}

		//This loop ensures that all possible deadwood is laid off
		//If a player has a partial run of two card, both of which 
		//can be added to the knocker's hand, the first iteration will
		//lay off the first card and the second one will lay off the second
		for(int i = 0; i < 2; i++){
			ArrayList<Card> tempToRemove = new ArrayList<Card>();

			for(Card c : state.getHand(defender).cards){
				if(c.runID == 0 && c.setID == 0){

					//For each meld in the knocker's hand, see if a deadwood card 
					//from the defender's hand can be added to a meld
					for(Meld m : state.getMeldsForPlayer(knocker).values()){
						if(addCardToMeld(c, m)){
							tempToRemove.add(c);
						}
					}
				}
			}

			//Remove deadwood cards from defender's hand
			for(Card c : tempToRemove){
				state.getHand(defender).remove(c);
			}
		}
	}

	/**
	 * Helper function for layoff. Allows us to add a specified card to a 
	 * specified meld.
	 * 
	 * @param c the card to add
	 * @param m the meld to add to
	 * @return
	 */
	public boolean addCardToMeld(Card c, Meld m){

		//If the meld is a set and the card is of the same rank as those cards,
		//add it to the meld and flag it as being laid off
		if(m.isSet){
			if(m.getMeldCards().get(0).getRank() == c.getRank()){
				c.layoffCard = true;
				m.cards.add(new Card(c));
				layoffCards.add(c);
				return true;
			}
		}
		//If the meld is a run...
		else{
			//Determine the min and max ranks for the meld
			int minrank = 14;
			int maxrank = 0;
			for(Card card : m.cards){
				if( card.getRank().value(1) < minrank){
					minrank = card.getRank().value(1);
				}

				if(card.getRank().value(1) >maxrank){
					maxrank = card.getRank().value(1);
				}
			}

			//If the card is of the same suit as the run and it is adjacent,
			//add it to the meld and flag it as being laid off
			if((c.getSuit() == m.getMeldCards().get(0).getSuit()) 
					&& (c.getRank().value(1) == minrank - 1 
					|| c.getRank().value(1) == maxrank + 1)){
				c.layoffCard = true;
				m.cards.add(new Card(c));
				layoffCards.add(c);
				return true;
			}

		}
		return false;
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
			//Begin a new round when a player is ready
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
				state.setPhase(GRState.DISCARD_PHASE);
			}
			//DISCARD PHASE
			else if (grma.isDiscard() && state.getPhase() == GRState.DISCARD_PHASE) {

				GRDiscardAction da = (GRDiscardAction) action;
				if(!(state.isFromDiscard() && state.getLastPicked().equals(da.discardCard()))){
					// Remove the requested card from the player's hand and place it atop the discard pile
					state.discard(da.discardCard(), thisPlayerIdx);

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

				//see if removing the desired card from the player's hand will allow
				//them to knock
				GRState copy = new GRState(state, 1);
				GRKnockAction copy_grma = (GRKnockAction)grma;
				Card theCard = copy_grma.knockCard();
				copy.getHand(thisPlayerIdx).remove(theCard);
				assessMelds(copy, thisPlayerIdx);

				//If indeed they CAN knock...
				if(canKnock(copy, copy.getHand(thisPlayerIdx), thisPlayerIdx)){

					//Player who knocked gets to go first
					state.toGoFirst = thisPlayerIdx;

					state.isEndOfRound = true;
					state.setPhase(GRState.DRAW_PHASE);

					//At the end of, round we must set to human player's turn or else computer player
					//will continually make moves
					if(grma.getPlayer() instanceof GRHumanPlayer && thisPlayerIdx == 0){
						state.setWhoseTurn(0);
					}else if(grma.getPlayer() instanceof GRHumanPlayer && thisPlayerIdx == 1){
						state.setWhoseTurn(1);
					}else if(!(grma.getPlayer() instanceof GRHumanPlayer) && thisPlayerIdx == 0){
						state.setWhoseTurn(1);
					}else if(!(grma.getPlayer() instanceof GRHumanPlayer) && thisPlayerIdx == 1){
						state.setWhoseTurn(0);
					}

					sendAllUpdatedState();
					//state.setPhase(state.DRAW_PHASE);
					state.getHand(thisPlayerIdx).remove(theCard);

					//Get the hands in a state with optimal melds and duplicates removed
					normalizeHands();

					//Get the deadwood
					int p0dw = countDeadwood(state.getHand(0));
					int p1dw = countDeadwood(state.getHand(1));

					//Scoring
					if(p0dw < p1dw){
						if(p0dw == 0){
							//player 0 goes gin
							state.setScore(0,20);
							scoresForRound[0] += 20;
						}
						state.setScore(0,p1dw - p0dw);
						scoresForRound[0] += (p1dw - p0dw);
						if(thisPlayerIdx == 1){
							//Give undercut points
							state.setScore(0,10);
							scoresForRound[0] += 10;
						}
					}else if (p0dw > p1dw){
						if(p1dw == 0){
							//player 1 goes gin
							state.setScore(1,20);
							scoresForRound[1] += 20;
						}
						state.setScore(1,p0dw - p1dw);	
						scoresForRound[1] += (p0dw - p1dw);
						if(thisPlayerIdx == 0){
							//Give undercut points
							state.setScore(1,10);
							scoresForRound[1] += 10;
						}
					}else{
						//If both players scores are equal
						if(p0dw == 0 && p1dw == 0){
							//If both players have gin
							//Give the player who knocks 20 pts
							state.setScore(thisPlayerIdx, 20);
							scoresForRound[thisPlayerIdx] += 20;
						}
						else{
							if(thisPlayerIdx == 0){
								state.setScore(1,10);
								scoresForRound[1] += 10;
							}else{
								state.setScore(0,10);
								scoresForRound[1] -= 10;
							}
						}
					}

					if(thisPlayerIdx == PLAYER_1){
						playerWhoLaidOff = PLAYER_1;
						layoff(PLAYER_1,PLAYER_2);
					}else{
						playerWhoLaidOff = PLAYER_2;
						layoff(PLAYER_2,PLAYER_1);
					}

					//TODO: Here you go, Jaimiey
					state.gameMessage = this.playerNames[0] + " score for round: " + state.getp1score() 
							+ "\n" + this.playerNames[1]+ " score for round: "+ state.getp2score() + "\n";

					String layoffPlayer = "";
					if(playerWhoLaidOff == 0){
						layoffPlayer = this.playerNames[0];
					}else {
						layoffPlayer = this.playerNames[1];
					}

					if(layoffCards.size() > 0){
						state.gameMessage = state.gameMessage + layoffPlayer + 
								" laid off:\n";
						for(Card c : layoffCards){
							state.gameMessage = state.gameMessage + c.toString()+"\n";
						}
					}

					//Reset layoff cards for next round
					layoffCards.clear();
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
