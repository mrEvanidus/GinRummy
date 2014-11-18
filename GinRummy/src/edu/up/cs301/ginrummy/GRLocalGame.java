package edu.up.cs301.ginrummy;

import java.util.ArrayList;

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

    /**
     * Constructor for the SJLocalGame.
     */
    public GRLocalGame() {
        Log.i("SJLocalGame", "creating game");
        // create the state for the beginning of the game
        state = new GRState();
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
	 * makes a move on behalf of a player
	 * 
	 * @param action
	 * 		the action denoting the move to be made
	 * @return
	 * 		true if the move was legal; false otherwise
	 */
	@Override
	protected boolean makeMove(GameAction action) {
		
		// check that we have slap-jack action; if so cast it
		if (!(action instanceof GRMoveAction)) {
			return false;
		} 
		GRMoveAction grma = (GRMoveAction) action;
		
		// get the index of the player making the move; return false
		int thisPlayerIdx = getPlayerIdx(grma.getPlayer());
		
		if (thisPlayerIdx < 0) { // illegal player
			return false;
		}
		
		if(canMove(thisPlayerIdx)){
			//DRAW PHASE
			if (grma.isDraw() && state.getPhase() == GRState.DRAW_PHASE) {
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
					if(state.whoseTurn() == 0){
						state.setWhoseTurn(1);
					}
					else if (state.whoseTurn() == 1){
						state.setWhoseTurn(0);
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
				copy.getHand(thisPlayerIdx).cards.remove(theCard);
				
				copy.assessMelds(thisPlayerIdx);
				if(copy.canKnock(copy.getHand(thisPlayerIdx),copy.getMeldsForPlayer(thisPlayerIdx))){
					state.isEndOfRound = true;
					state.getHand(thisPlayerIdx).cards.remove(theCard);
					
					state.assessMelds(0);
					state.assessMelds(1);
					state.canKnock(state.getHand(0), state.getMeldsForPlayer(0));
					state.canKnock(state.getHand(1), state.getMeldsForPlayer(1));
					
					//Lay off cards
					if(thisPlayerIdx == 1){
						for(int i = 0; i<2;i++){
							ArrayList<Card> tempToAdd = new ArrayList<Card>();
							ArrayList<Card> tempToRemove = new ArrayList<Card>();
							for(Card c : state.getHand(0).cards){
								if(c.runID == 0 && c.setID == 0){
									int dw = state.genHand(-1, state.getHand(1));
									GRState s = new GRState(state);
									s.getHand(1).cards.add(c);
									s.assessMelds(1);
									s.canKnock(s.getHand(1), s.getMeldsForPlayer(1));
									int dw2 = s.genHand(-1, s.getHand(1));

									if(dw == dw2){
										tempToAdd.add(c);
										tempToRemove.remove(c);
									}
								}
							}
							for(Card c : tempToAdd){
								state.getHand(1).cards.add(c);
							}
							for(Card c : tempToRemove){
								state.getHand(0).cards.remove(c);
							}
						}
					}else{
						for(int i = 0; i<2;i++){
							ArrayList<Card> tempToAdd = new ArrayList<Card>();
							ArrayList<Card> tempToRemove = new ArrayList<Card>();
							for(Card c : state.getHand(1).cards){
								if(c.runID == 0 && c.setID == 0){
									int dw = state.genHand(-1, state.getHand(0));
									GRState s = new GRState(state);
									s.getHand(0).cards.add(c);
									s.assessMelds(0);
									s.canKnock(s.getHand(0), s.getMeldsForPlayer(0));
									int dw2 = s.genHand(-1, s.getHand(0));

									if(dw == dw2){
										tempToAdd.add(c);
										tempToRemove.remove(c);
									}
								}
								
							}
							for(Card c : tempToAdd){
								state.getHand(0).cards.add(c);
							}
							for(Card c : tempToRemove){
								state.getHand(1).cards.remove(c);
							}
						}
					}
					
					state.assessMelds(0);
					state.assessMelds(1);
					state.canKnock(state.getHand(0), state.getMeldsForPlayer(0));
					state.canKnock(state.getHand(1), state.getMeldsForPlayer(1));
					
					//Get the deadwood
					int p0dw = state.genHand(-1,state.getHand(0));
					int p1dw = state.genHand(-1,state.getHand(1));
					
					if(p0dw < p1dw){
						if(p0dw == 0){
							state.setScore(0,20);
						}
						state.setScore(0,p1dw - p0dw);	
						if(thisPlayerIdx != 0){
							//Give undercut points
							state.setScore(thisPlayerIdx,10);
						}
					}else if (p0dw > p1dw){
						if(p1dw == 0){
							state.setScore(1,20);
						}
						state.setScore(1,p0dw - p1dw);	
						if(thisPlayerIdx != 1){
							//Give undercut points
							state.setScore(thisPlayerIdx,10);
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
					
					//Whichever player knocked goes first
					if(thisPlayerIdx == 0){
						state.setWhoseTurn(0);
					}else{
						state.setWhoseTurn(1);
					}
					
					sendAllUpdatedState();
					//state.initNewRound();
					
				}
				else{
					return false;
				}
			}else if(grma.isNextRound() && state.isEndOfRound){
				state.initNewRound();
			}
			else {
				return false;
			}
		}
		
		sendAllUpdatedState();
		// return true, because the move was successful if we get here
		return true;
	}
}
