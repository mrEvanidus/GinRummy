package edu.up.cs301.ginrummy;

import edu.up.cs301.game.GamePlayer;

public class GRNewGameAction extends GRNextRoundAction {

	public GRNewGameAction(GamePlayer player) {
		super(player);
	}
	
	@Override
	public boolean isNewGame(){
		return true;
	}
}
