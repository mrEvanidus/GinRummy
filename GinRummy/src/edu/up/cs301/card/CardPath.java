package edu.up.cs301.card;

import android.graphics.*;

public class CardPath /*implements Runnable*/{
	
	private float animationDuration = 50; //how long the animation will take, default 50 ticks
	
	//instance variables
	private Card card;
	private PointF origin;
	private PointF destination;
	private PointF location;
	private int progress; //the number of ticks that have elapsed
	
	public CardPath(Card card, PointF origin, PointF destination) {
		this.card = card;
		this.origin = origin;
		this.destination = destination;
		this.location = origin;
		progress = 0;
	}


	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	public PointF getOrigin() {
		return origin;
	}
	
	public void setAnimationSpeed(float speed) {
		animationDuration = speed;
	}
	
	public float getAnimationSpeed() {
		return animationDuration;
	}

	public PointF getDestination() {
		return destination;
	}

	public void setDestination(PointF desination) {
		this.destination = desination;
	}
	
	public PointF getPosition() {
		return location;
	}
	
	public void setPosition(PointF position) {
		this.location = position; 
	}

	/**
	 * advances the animation by one tick
	 * @return the updated location of the card
	 */
	public PointF advance() {
		if (isComplete()) return null;
		//advance the card linearly along the path
		//find the change amount
		float dx = origin.x + (destination.x - origin.x)/animationDuration*(float)progress;
		float dy = origin.y + (destination.y - origin.y)/animationDuration*progress;
		
		progress++;
		
		//set the new location of the card
		location = new PointF(dx,dy);
		
		return location;
	}
	
	
	/**
	 * indicates whether the animation is complete
	 * @return TRUE if the path is complete
	 */
	public boolean isComplete() {
		return progress >= animationDuration;
	}

	/**
	 * TODO
	 */
//	public void run() {
//		while(!isComplete()) {
//			//advance the animation
//			this.location = this.advance();
//			
//			//wait a bit
//			try {
//				Thread.sleep((int)getAnimationSpeed());
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
