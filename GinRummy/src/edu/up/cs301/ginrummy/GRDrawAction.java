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
 */
public class GRDrawAction extends GRMoveAction {

	private boolean fromStock;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4687694013306201965L;

	public GRDrawAction(GamePlayer player, boolean fs) {
		super(player);
		fromStock = fs;
	}

	@Override
	public boolean isDraw(){
		return true;
	}

	public boolean fromStock(){
		return fromStock;
	}
}
