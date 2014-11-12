package edu.up.cs301.slapjack;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GamePlayer;

public class GRKnockAction extends GRMoveAction {

	private Card knockCard;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9121428630987648723L;

	public GRKnockAction(GamePlayer player, Card kc) {
		super(player);
		knockCard = kc;
	}
	
	@Override
	public boolean isKnock() {
		return true;
	}
	
	public Card knockCard() {
		return knockCard;
	}

}
