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
	 */
	@Override
	public void drawOn(Canvas g, RectF where) {		
		// create the paint object
		Paint p = new Paint();
		p.setColor(Color.BLACK);

		// create the source rectangle
		Rect r = new Rect(0,0,cardImage.getWidth(),cardImage.getHeight());

		// draw the bitmap into the target rectangle
		g.drawBitmap(cardImage, r, where, p);

	}

	private static Bitmap cardImage = null;

	public static void initImages(Activity activity) {

		// if it's already initialized, then ignore
		//TODO: Do we need this? Took it from Card.java to be safe
		if (cardImage != null) return;

		int cardBackImage = R.drawable.vegdahlback;
		cardImage = BitmapFactory.decodeResource(activity.getResources(),cardBackImage);

	}

}
