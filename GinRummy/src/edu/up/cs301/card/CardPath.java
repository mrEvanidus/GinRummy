package edu.up.cs301.card;

import android.graphics.*;

/**
 * 
 * @version December 2014
 * 
 * @author John Allen
 * @author Matthew Wellnitz
 * @author Eric Tsai
 * @author Jaimiey Sears
 *
 */
public class CardPath{

	private float animationDuration = 50; //how long the animation will take, default 50 ticks

	//instance variables
	private Card card;
	private PointF origin;
	private PointF destination;
	private PointF location;
	private int progress; //the number of ticks that have elapsed

	/**
	 * ctor
	 * @param card
	 * 			the card which belongs to this path
	 * @param origin
	 * 			where the card starts
	 * @param destination
	 * 			where the card should end
	 */
	public CardPath(Card card, PointF origin, PointF destination) {
		this.card = card;
		this.origin = origin;
		this.destination = destination;
		this.location = origin;
		progress = 0;
	}


	/**
	 * @return the card which belongs to this path
	 */
	public Card getCard() {
		return card;
	}

	/**
	 * @param card
	 * 		the card you would like to set
	 */
	public void setCard(Card card) {
		this.card = card;
	}

	/**
	 * @return the start position of the card (PointF)
	 */
	public PointF getOrigin() {
		return origin;
	}

	/**
	 * @param speed
	 * 			how long you would like the animation to take, in ticks 
	 */
	public void setAnimationSpeed(float speed) {
		animationDuration = speed;
	}

	/**
	 * @return how long the animation will take
	 */
	public float getAnimationSpeed() {
		return animationDuration;
	}

	/**
	 * @return the PointF destination of the card
	 */
	public PointF getDestination() {
		return destination;
	}

	/**
	 * @param desination
	 * 			set a new PointF destination for the card
	 */
	public void setDestination(PointF desination) {
		this.destination = desination;
	}

	/**
	 * @return the current location of hte card
	 */
	public PointF getPosition() {
		return location;
	}

	/**
	 * @param position
	 * 			the position the card should teleport to
	 */
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
		float dx = 0;
		float dy = 0;
		try{
			dx = origin.x + (destination.x - origin.x)/animationDuration*(float)progress;
			dy = origin.y + (destination.y - origin.y)/animationDuration*progress;
		}
		catch(NullPointerException e){

		}

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
}
