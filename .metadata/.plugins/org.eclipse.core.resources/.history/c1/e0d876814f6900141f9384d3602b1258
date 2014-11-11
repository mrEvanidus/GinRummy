package edu.up.cs301.ginrummy;

import java.util.ArrayList;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import android.graphics.Color;

/**
 * this is the primary activity for Gin Rummy
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class GRMainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a gin rummy game for two players. The default is human vs. computer */
	@Override
	public GameConfig createDefaultConfig() {

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		playerTypes.add(new GamePlayerType("human player") {
			public GamePlayer createPlayer(String name) {
				return new GRHumanPlayer(name);
			}});
		playerTypes.add(new GamePlayerType("computer player (normal)") {
			public GamePlayer createPlayer(String name) {
				return new GRComputerPlayer(name);
			}
		});

		// Create a game configuration class for SlapJack
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 2, "Gin Rummy", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("Computer", 2);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame() {
		return new GRLocalGame();
	}
}
