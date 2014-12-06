package edu.up.cs301.ginrummy;

import edu.up.cs301.card.Card;
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
public class GRDiscardAction extends GRMoveAction {

	private Card disCard;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8689450112547589420L;

	/**
	 * ctor
	 * @param player
	 * 		the player who calls it
	 * @param dc
	 * 		the card we discarded
	 */
	public GRDiscardAction(GamePlayer player, Card dc) {
		super(player);
		disCard = dc;
	}

	@Override
	public boolean isDiscard() {
		return true;
	}

	/**
	 * @return the discarded card
	 */
	public Card discardCard() {
		return disCard;
	}

}
