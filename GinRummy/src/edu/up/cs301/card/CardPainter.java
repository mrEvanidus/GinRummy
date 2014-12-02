package edu.up.cs301.card;

import java.util.ArrayList;
import java.util.Timer;
import edu.up.cs301.ginrummy.GRHumanPlayer;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class CardPainter implements Runnable{

	//the canvas we want to draw on
	private Canvas canvas;

	//the moving card
	private ArrayList<CardPath> paths;

	//the player which owns this painter
	private GRHumanPlayer player;

	private Card draggedCard;
	private PointF draggedPos, draggedOrigin;

	public PointF getDraggedOrigin() {
		return draggedOrigin;
	}

	public void setDraggedOrigin(PointF draggedOrigin) {
		this.draggedOrigin = draggedOrigin;
	}

	/**
	 * ctor
	 * @param p
	 * @param c
	 */
	public CardPainter(GRHumanPlayer p, Canvas c) {
		this.canvas = c;
		this.player = p;

		paths = new ArrayList<CardPath>();
	}

	/**
	 * TODO
	 */
	public void setDragged(Card card){
		draggedCard = card;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public Card getDragged() {
		return draggedCard;
	}
	
	/**
	 * TODO
	 * @return
	 */
	public PointF getDraggedPos() {
		return draggedPos;
	}
	
	/**
	 * TODO
	 * @param pos
	 */
	public void setDraggedPos(PointF pos) {
		draggedPos = pos;
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

		//draw the card being dragged
		if (draggedCard != null && draggedPos != null){
			draggedCard.drawOn(canvas, player.adjustDimens(draggedPos));
		}
		
		
		ArrayList<CardPath> pathsCopy = new ArrayList<CardPath>(paths);
		for (CardPath path : pathsCopy) {
			//advance the animation
			path.setPosition(path.advance());

			if (path.getLast() != null) {
				Paint bg = new Paint();
				bg.setColor(GRHumanPlayer.FELT_GREEN);
				canvas.drawRect(player.adjustDimens(path.getLast()), bg);
			}

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

		//check the dragged
		if (draggedCard != null && draggedCard.equals(card)) return true;

		//otherwise return false
		return false;
	}

	/**
	 * TODO
	 * @return
	 */
	public Card getCard() {
		return draggedCard;
	}

}
