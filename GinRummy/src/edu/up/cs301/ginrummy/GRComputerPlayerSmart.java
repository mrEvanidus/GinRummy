package edu.up.cs301.ginrummy;

import java.util.ArrayList;
import java.util.Hashtable;
import edu.up.cs301.card.Card;
import edu.up.cs301.card.Deck;
import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * This is a computer player that slaps at an average rate given
 * by the constructor parameter.
 * 
 * @author Steven R. Vegdahl
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
 */
public class GRComputerPlayerSmart extends GameComputerPlayer {	
	//public static final int THIS_PLAYER = 1;
	public static final int MELD = 0;
	public static final int ONE_HALF_MELD = 10;
	public static final int TWO_HALF_MELD = 5;
	public static final int SINGLETON = 20;

	// the most recent state of the game
	private GRState savedState;

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
			if(c.priority > highest || theCard == null){
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
	 * callback method, called when we receive a message, typically from
	 * the game
	 */
	@Override
	protected void receiveInfo(GameInfo info) {

		// if we don't have a game-state, ignore
		if (!(info instanceof GRState)) {
			return;
		}

		// update our state variable
		savedState = (GRState)info;

		thisPlayer = savedState.yourId;

		//Check if it's this players turn
		if(savedState.whoseTurn() == thisPlayer){


			//DRAW PHASE
			if(savedState.getPhase() == GRState.DRAW_PHASE){
				//delay
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized(this){
					assessMelds(savedState, thisPlayer);
					canKnock(savedState, savedState.getHand(thisPlayer),thisPlayer);

					Card topOfDiscard = savedState.getDiscard().peekAtTopCard();
					GRState copy = new GRState(savedState,1);

					copy.getHand(thisPlayer).add(topOfDiscard);
					assessMelds(copy, thisPlayer);
					canKnock(copy, copy.getHand(thisPlayer),thisPlayer);

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
			else if (savedState.getPhase() == GRState.DISCARD_PHASE && !savedState.isEndOfRound){
				//delay
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				synchronized(this){
					GRState s = new GRState(savedState,1);
					Card c = cardToDiscard(s.getHand(thisPlayer));
					s.getHand(thisPlayer).remove(c);

					assessMelds(s, thisPlayer);
					canKnock(s, s.getHand(thisPlayer),thisPlayer);

					if(canKnock(s, s.getHand(thisPlayer), thisPlayer)){
						game.sendAction(new GRKnockAction(this,c));
					}else{
						game.sendAction(new GRDiscardAction(this, c));
					}
				}
			}
		}
	}
}
