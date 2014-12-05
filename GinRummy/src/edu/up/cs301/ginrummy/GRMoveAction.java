package edu.up.cs301.ginrummy;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * A game-move object that a tic-tac-toe player sends to the game to make
 * a move.
 * 
 * @author Steven R. Vegdahl
 * @version 2 July 2001
 */
public abstract class GRMoveAction extends GameAction {
	
	private static final long serialVersionUID = -3107100271012188849L;

    /**
     * Constructor for SJMoveAction
     *
     * @param source the player making the move
     * @param row the row of the square selected (0-2)
     * @param col the column of the square selected
     */
    public GRMoveAction(GamePlayer player)
    {
        // invoke superclass constructor to set source
        super(player);
    }
    
    /**
     * @return
     * 		whether the move was a draw
     */
    public boolean isDraw() {
    	return false;
    }
    
    /**
     * @return
     * 		whether the move was a discard
     */
    public boolean isDiscard() {
    	return false;
    }
    
    /**
     * @return
     * 		whether the move was a knock
     */
    public boolean isKnock(){
    	return false;
    }
    
    public boolean isNextRound(){
    	return false;
    }
    public boolean isNewGame(){
    	return false;
    }

}
