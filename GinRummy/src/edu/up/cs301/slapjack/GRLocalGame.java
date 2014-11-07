package edu.up.cs301.slapjack;

import android.util.Log;
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
				// Remove the requested card from the player's hand and place it atop the discard pile
				state.discard(da.discardCard(), thisPlayerIdx);
				
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
			}
			//KNOCK PHASE
			else if (grma.isKnock() && state.getPhase() == GRState.DISCARD_PHASE){
				//If the player can knock, flag in the state that it is the end of the round
				if(state.canKnock(state.whoseTurn())){
					state.isEndOfRound = true;
				}
			}
			else {
				return false;
			}
		}
		// return true, because the move was successful if we get her
		return true;
	}
}