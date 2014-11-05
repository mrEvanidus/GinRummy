package edu.up.cs301.slapjack;

import java.util.ArrayList;
import gameObjects.*;
import android.graphics.*;
import android.view.MotionEvent;

public class CardAnimator implements edu.up.cs301.animation.Animator {

	private static final int FRAME_INTERVAL = 10;
	private static final int BACKGROUND_COLOR = 0xff278734;
	
	private ArrayList<CardPath> paths;
	protected Card card;
	protected RectF cardPos;
	private PointF cardDimensions;
	
	//constructor
	public CardAnimator() {
		super();
		cardDimensions = new PointF(261, 379);
		paths = new ArrayList<CardPath>();
		
		//create a new card
		card = new Card(Rank.ACE, Suit.Spade);
		cardPos = new RectF(0,0,0,0);
	}
	
	public int interval() {
		return FRAME_INTERVAL;
	}

	public int backgroundColor() {
		return BACKGROUND_COLOR;
	}

	public boolean doPause() {
		return false;
	}

	public boolean doQuit() {
		return false;
	}

	public void tick(Canvas canvas) {
		// TODO Auto-generated method stub
		for (CardPath path : paths)  {
			PointF newPos = path.advance();
			
			int x = (int)newPos.x;
			int y = (int)newPos.y;
			
			cardPos = new RectF(x,y,x + cardDimensions.x, y + cardDimensions.y);
		}
		
		if (card != null && cardPos != null) card.drawOn(canvas, cardPos);
	}

	public void onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		
		float x = event.getX();
		float y = event.getY();
		
		//teleport the card to the coordinates
//		cardPos = new RectF(x,y,x + cardDimensions.x, y + cardDimensions.y);
		
		float origX = cardPos.left;
		float origY = cardPos.top;
		//move the card to the tapped location along a path
		paths.add(new CardPath(card, new PointF(origX, origY), new PointF(x, y)));
		
	}
}
