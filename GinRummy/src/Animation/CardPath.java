package Animation;

import gameObjects.Card;
import android.graphics.*;

public class CardPath {
	
	private static float animationSpeed = 10; //the speed of animations, default 100 ticks
	
	//instance variables
	private Card card;
	private PointF origin;
	private PointF destination;
	private int progress; //the number of ticks that have elapsed
	
	public CardPath(Card card, PointF origin, PointF destination) {
		this.card = card;
		this.origin = origin;
		this.destination = destination;
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
		this.animationSpeed = speed;
	}
	
	public float getAnimationSpeed() {
		return animationSpeed;
	}

	public PointF getDestination() {
		return destination;
	}

	public void setDestination(PointF desination) {
		this.destination = desination;
	}

	public PointF advance() {
		// TODO Auto-generated method stub
		//advance the card linearly along the path
		if (progress == animationSpeed) return new PointF(destination.x, destination.y);
		
		//find the change amount, in pixels
		float dx = origin.x + (destination.x - origin.x)/animationSpeed*progress;
		float dy = origin.y + (destination.y - origin.y)/animationSpeed*progress;
		
		progress++;
		return new PointF(dx,dy);
	}
}
