package edu.up.cs301.ginrummy;

import edu.up.cs301.game.GamePlayer;
/**
 * 
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
 *
 */
public class GRNextRoundAction extends GRMoveAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -225823968468498776L;

	public GRNextRoundAction(GamePlayer player){
		super(player);
	}

	@Override
	public boolean isNextRound(){
		return true;
	}

}
