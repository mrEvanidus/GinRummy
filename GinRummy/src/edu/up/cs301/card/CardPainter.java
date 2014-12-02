package edu.up.cs301.card;

import java.util.ArrayList;
import java.util.Timer;

import edu.up.cs301.ginrummy.GRHumanPlayer;
import android.graphics.Canvas;

public class CardPainter implements Runnable{

	//the canvas we want to draw on
	private Canvas canvas;

	//the moving card
	private ArrayList<CardPath> paths;

	//the player which owns this painter
	private GRHumanPlayer player;

	public CardPainter(GRHumanPlayer p, Canvas c) {
		this.canvas = c;
		this.player = p;
		
		paths = new ArrayList<CardPath>();
	}

	/**
	 * adds a path to our list
	 */
	public void addCardPath(CardPath path) {
		paths.add(path);
	}

	/**
	 * TODO
	 */
	public void run() {
		/*while(true) */{
			ArrayList<CardPath> pathsCopy = new ArrayList<CardPath>(paths);
			for (CardPath path : pathsCopy) {
				//advance the animation
				path.setPosition(path.advance());

				//draw the card
				path.getCard().drawOn(canvas, player.adjustDimens(path.getPosition()));

				//delete the path if complete
				if (path != null && path.isComplete()) paths.remove(path);

				//wait a bit
//				try {
//					Thread.sleep(10/*(int)path.getAnimationSpeed()*/);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		}//paths
	}//run

	/**
	 * indicates whether the cardPainter is painting the given card
	 * @param card
	 * 		the Card object we are looking for
	 * @return
	 * 		TRUE if the card is being painted
	 */
	public boolean isPainting(Card card) {
		//check all the paths
		for (CardPath path : paths) {
			if (path.getCard().equals(card)) return true;
		}

		//otherwise return false
		return false;
	}

}
