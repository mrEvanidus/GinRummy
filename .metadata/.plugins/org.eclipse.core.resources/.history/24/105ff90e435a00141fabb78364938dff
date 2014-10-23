package com.example.ginrummy;

import java.util.ArrayList;
import gameObjects.*;
import Animation.*;
import android.graphics.*;
import android.view.MotionEvent;

public class CardAnimator implements Animation.Animator {

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
	
	@Override
	public int interval() {
		return FRAME_INTERVAL;
	}

	@Override
	public int backgroundColor() {
		return BACKGROUND_COLOR;
	}

	@Override
	public boolean doPause() {
		return false;
	}

	@Override
	public boolean doQuit() {
		return false;
	}

	@Override
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

	@Override
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
