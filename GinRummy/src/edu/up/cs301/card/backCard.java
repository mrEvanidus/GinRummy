package edu.up.cs301.card;

import edu.up.cs301.game.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

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
public class backCard extends Card{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1033148364866715766L;

	//private static final long serialVersionUID = 393532931190070332L;
	/**
	 * Constructor for class backCard
	 *
	 */
	public backCard() {
		super(null,null);
	}

	
	 /**
     * Draws the card on a Graphics object.  The card is drawn as the
     * image provided. 
     * @param g  the graphics object on which to draw
     * @param where  a rectangle that tells where the card should be drawn
     * @param cardType tells whether the card to draw is for the stock pile
     */
	@Override
	public void drawOn(Canvas g, RectF where, boolean cardType) {		
		// create the paint object

    	Paint p = new Paint();
    	p.setColor(Color.BLACK);
    	
    	//draw appropriate orientation for card based on specified player
    	Bitmap imageToDraw;
    	if (cardType) imageToDraw = cardImage1;
    	else imageToDraw = cardImage2;
    	
    	// create the source rectangle
		Rect r = new Rect(0,0,imageToDraw.getWidth(),imageToDraw.getHeight());
		
		// draw the bitmap into the target rectangle
		g.drawBitmap(imageToDraw, r, where, p);
		
	}
	
	private static Bitmap cardImage1 = null;
	private static Bitmap cardImage2 = null;
	
	/**
	 * initialize the back card images for second player and stock pile
	 * @param activity - the current activity
	 */
	public static void initImages(Activity activity) {

		// if it's already initialized, then ignore

    	if (cardImage1 != null) return;
		
		int stockBackImage = R.drawable.vegdahlback;
		int player2BackImage = R.drawable.vegdahlback2;
		
		cardImage1 = BitmapFactory.decodeResource(activity.getResources(),stockBackImage);
		cardImage2 = BitmapFactory.decodeResource(activity.getResources(),player2BackImage);
	}

}
