package edu.up.cs301.ginrummy;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

public class GRDiscardAction extends GRMoveAction {

	private Card disCard;
	/**
	 * 
	 */
	private static final long serialVersionUID = 8689450112547589420L;

	public GRDiscardAction(GamePlayer player, Card dc) {
		super(player);
		disCard = dc;
	}
	
	@Override
	public boolean isDiscard() {
		return true;
	}
	
	public Card discardCard() {
		return disCard;
	}

}
