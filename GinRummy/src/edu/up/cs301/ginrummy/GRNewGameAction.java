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
public class GRNewGameAction extends GRNextRoundAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2892486299415640179L;

	public GRNewGameAction(GamePlayer player) {
		super(player);
	}

	@Override
	public boolean isNewGame(){
		return true;
	}
}
