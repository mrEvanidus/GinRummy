package edu.up.cs301.ginrummy;

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.up.cs301.animation.*;
import edu.up.cs301.card.*;
import edu.up.cs301.game.*;
import edu.up.cs301.game.infoMsg.*;

/**
 * A GUI that allows a human to play Slapjack. Moves are made by clicking
 * regions on a surface. Presently, it is laid out for landscape orientation.
 * If the device is held in portrait mode, the cards will be very long and
 * skinny.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 * 
 * @author Jaimiey Sears
 * @version November 2014
 */
public class GRHumanPlayer extends GameHumanPlayer implements Animator {

	//how much a card on top of another should be offset by
	private final static float STACKED_CARD_OFFSET = 0.005F;
	private final static float HAND_CARD_OFFSET = 0.055F;

	//the width and height of the card images
	private final static PointF CARD_DIMENSIONS = new PointF(261, 379);
	// the size a card should be grown or shrunk by
	//TODO: for device cross-compatibility, make this change based on canvas size
	private static float CARD_DIMENSION_MODIFIER = 0.75f;

	//colors used
	public static final int FELT_GREEN = 0xff277714;

	// our game state
	protected GRState state;

	// our activity
	private Activity myActivity;

	// the animation surface
	private AnimationSurface surface;

	//the knock and exit buttons
	Button exitButton;

	//the score and message pane text fields
	private TextView oppScore, myScore, messagePane;

	//card information
	private ArrayList<CardPath> paths;

	//dragged card
	private Card draggedCard;
	private PointF draggedCardPos;

	//ERIC: touched Coordinates
	private int touchedX;
	private int touchedY;

	//ERIC: card being moved
	private Card touchedCard;



	//the positions of the decks
	protected static PointF stockPos, discardPos, knockPos;

	//	protected static PointF playerHandPos[] = new PointF[2];

	//player hand positions
	protected ArrayList<PointF> p1handPos, p2handPos;

	/**
	 * constructor
	 * 
	 * @param name
	 * 		the player's name
	 */
	public GRHumanPlayer(String name) {
		super(name);
		p1handPos = new ArrayList<PointF>();
		p2handPos = new ArrayList<PointF>();
		//ERIC
		//touchedCard = new backCard();
	}

	/**
	 * callback method: we have received a message from the game
	 * 
	 * @param info
	 * 		the message we have received from the game
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		Log.i("GRComputerPlayer", "receiving updated state ("+info.getClass()+")");
		if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
			// if we had an out-of-turn or illegal move, flash the screen
			surface.flash(Color.RED, 50);
		}
		else if (!(info instanceof GRState)) {
			// otherwise, if it's not a game-state message, ignore
			return;
		}
		else {
			// it's a game-state object: update the state. Since we have an animation
			// going, there is no need to explicitly display anything. That will happen
			// at the next animation-tick, which should occur within 1/20 of a second
			this.state = (GRState)info;
			Log.i("human player", "receiving");
			if (state.getPhase() == GRState.DRAW_PHASE){
					messagePane.setText("It's Your Turn:\nDraw a card.");
			}
		}
	}

	/**
	 * call-back method: called whenever the GUI has changed (e.g., at the beginning
	 * of the game, or when the screen orientation changes).
	 * 
	 * @param activity
	 * 		the current activity
	 */
	public void setAsGui(GameMainActivity activity) {

		// Load the layout resource for the new configuration
		activity.setContentView(R.layout.activity_gin_rummy);

		// link the animator to the animation surface
		surface = (AnimationSurface) activity.findViewById(R.id.animationSurface);
		surface.setAnimator(this);

		//initialize the buttons
		exitButton = (Button)activity.findViewById(R.id.exitButton);

		//initialize the text fields
		oppScore = (TextView)activity.findViewById(R.id.opponentScore);
		myScore = (TextView)activity.findViewById(R.id.playerScore);
		messagePane = (TextView)activity.findViewById(R.id.messagePane);

		//set up exit button listener
		exitButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Exit button handler
				System.exit(0);
			}});

		// read in the card images
		backCard.initImages(activity);
		Card.initImages(activity);

		//initialize our path list
		paths = new ArrayList<CardPath>();

		//set the location of the decks
		knockPos = new PointF(0.1f, 0.25f);
		stockPos = new PointF(0.3f,0.25f);
		discardPos = new PointF(0.5f,0.25f);

		//		playerHandPos[0] = new PointF(0.0f,0.75f);
		//		playerHandPos[1] = new PointF(0.6f,-0.25f);

		// if the state is not null, simulate having just received the state so that
		// any state-related processing is done
		if (state != null) {
			receiveInfo(state);
		}
	}

	/**
	 * @return the top GUI view
	 */
	@Override
	public View getTopView() {
		return myActivity.findViewById(R.id.top_gui_layout);
	}

	/**
	 * @return
	 * 		the animation interval, in milliseconds
	 */
	public int interval() {
		// 1/20 of a second
		return 50;
	}

	/**
	 * @return
	 * 		the background color
	 */
	public int backgroundColor() {
		return FELT_GREEN;
	}

	/**
	 * @return
	 * 		whether the animation should be paused
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * @return
	 * 		whether the animation should be terminated
	 */
	public boolean doQuit() {
		return false;
	}

	/**
	 * callback-method: we have gotten an animation "tick"; redraw the screen image:
	 * - the middle deck, with the top card face-up, others face-down
	 * - the two players' decks, with all cards face-down
	 * - a red bar to indicate whose turn it is
	 * 
	 * @param g
	 * 		the canvas on which we are to draw
	 */
	public void tick(Canvas canvas) {
		// ignore if we have not yet received the game state
		if (state == null) return;

		//TODO: figure out why the messagePane and scorePanes are always null.
		//post a message to our message pane
		if (messagePane == null){
			if (state.getPhase() == GRState.DRAW_PHASE) {
				messagePane.setText("Draw a card.");
			}
			else {
				messagePane.setText("Discard a card.");
			}
		}

		GRState stateCopy = state;
		
		//get the information from the state
		Deck decks[] = {stateCopy.getStock(),stateCopy.getDiscard()};
		PointF deckPos[] = {stockPos, discardPos};

		//draw the stock and discard piles
		for (int idx = decks.length-1; idx >= 0; idx--){
			Deck deck = decks[idx];
			for (Card card : deck.cards) {
				int n = deck.cards.indexOf(card);

				//add a few pixels to the position
				RectF position = adjustDimens(deckPos[idx]);
				position.set(new RectF(position.left + STACKED_CARD_OFFSET*position.width()*n,
						position.top - STACKED_CARD_OFFSET*position.height()*n,
						position.right + STACKED_CARD_OFFSET*position.width()*n,
						position.bottom - STACKED_CARD_OFFSET*position.height()*n
						));

				//draw the card
				card.drawOn(canvas, position);
			}
		}

		//draw the player's hands
		p1handPos.clear();
		synchronized(this){
			for (Card card : stateCopy.getHand(0).cards) {

				int n = stateCopy.getHand(0).cards.indexOf(card);
				p1handPos.add(new PointF(0.05f + HAND_CARD_OFFSET*n, 0.75f));

				//add a few pixels to the position
				//			RectF position = adjustDimens(p1handPos.get(n));
				//			position.set(new RectF(position.left + HAND_CARD_OFFSET*position.width()*n,
				//					position.top,
				//					position.right + HAND_CARD_OFFSET*position.width()*n,
				//					position.bottom
				//					));

				//draw the card
				if (touchedCard == null) {
					card.drawOn(canvas, adjustDimens(p1handPos.get(n)));
				}
				else if (!touchedCard.equals(card)) card.drawOn(canvas, adjustDimens(p1handPos.get(n)));
			}
		}

		p2handPos.clear();
		//draw the opponent's hand
		synchronized(this){
			Deck copy = stateCopy.getHand(1);
			ArrayList<PointF> copypos = p2handPos;
			for (Card card : copy.cards) {
				int n = copy.cards.indexOf(card);
				copypos.add(new PointF(0.55f - HAND_CARD_OFFSET*n, -0.25f));
				//add a few pixels to the position

				//draw the card
				//TODO thread bug is here
				card.drawOn(canvas, adjustDimens(copypos.get(n)));

			}
		}
		
		//advance and draw the card paths
		for (CardPath path : paths)  {
			int idx = paths.indexOf(path);

			//advance the card along the path
			path.advance();

			//draw the card
			path.drawOn(canvas);

			if (path.isComplete()) {
				//if the animation is done, get the card back and end the animation
				path.getOriginDeck().add(path.getCard());
				paths.remove(idx);
			}
		}
		
		//draw the knocking box
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.GREEN);
		p.setTextSize(24);
		canvas.drawText("KNOCK", knockPos.x*surface.getWidth() + getCardDimensions().x/6,
				knockPos.y*surface.getHeight() + getCardDimensions().y/2, p);
		canvas.drawRect(adjustDimens(knockPos), p);

		//draw the card being dragged
		if (touchedCard != null) {
			touchedCard.drawOn(canvas, new RectF(touchedX, touchedY, touchedX + getCardDimensions().x, touchedY+getCardDimensions().y) );
		}

	}

	/**
	 * callback method: we have received a touch on the animation surface
	 * 
	 * @param event
	 * 		the motion-event
	 * @return 
	 */
	public void onTouch(MotionEvent event) { 

		// get the location of the touch on the surface
		int touchX = (int) event.getX();
		int touchY = (int) event.getY();


		//on down touch events:
		if (event.getAction() == MotionEvent.ACTION_DOWN){		//ERIC


			//check for discard
			if (state.getPhase() == GRState.DISCARD_PHASE){
				//the card to discard
				//ERIC: Moved this line to top of onTouch so that it can be accessed anywhere
				//in onTouch
				Card discard = null;
				for (PointF p : p1handPos) {

					//check each card hand position to see if it was touched
					if (adjustDimens(p).contains(touchX, touchY)) {
						int i = p1handPos.indexOf(p);

						//select the card
						discard = state.getHand(0).cards.get(i);

						//ERIC: make touchedCard the card to discard
						touchedCard = discard;

					}
				}

			}

		}

		//check for draw
		else if (state.getPhase() == GRState.DRAW_PHASE) {
			if (adjustDimens(stockPos).contains(touchX, touchY)) {
				//draw from the stock pile
				game.sendAction(new GRDrawAction(this, true));
			}
			else if (adjustDimens(discardPos).contains(touchX, touchY)) {
				//draw from the discard pile
				game.sendAction(new GRDrawAction(this, false));
			}
		}

		//ERIC: When we release our finger and the card is hovered over the discard
		else if (event.getAction() == MotionEvent.ACTION_UP) {

			if (adjustDimens(discardPos).contains(touchX, touchY)) {
				//discard the selected card
				//ERIC: Moved discard action call to here
				if (touchedCard != null) {
					game.sendAction(new GRDiscardAction(this, touchedCard));
				}
				//move the touched card back to origin
				touchedCard = null;
			}
			else if (adjustDimens(knockPos).contains(touchX,touchY)) {
				
				//knock with the selected card
				//ERIC: Moved discard action call to here
				if (touchedCard != null) {
					game.sendAction(new GRKnockAction(this, touchedCard));
				}
			}

		}
		else {
			//ERIC: when we move a card, move it from its center
			touchedX = touchX - (int)getCardDimensions().x/2;
			touchedY = touchY - (int)getCardDimensions().y/2;
		}
	}

	/**
	 * 
	 * @param location
	 * 			a PointF which describes the location(in screen percent)
	 * 			where the card will be drawn 
	 * @return
	 * 			a RectF describing the boundary where the card will be drawn
	 */
	private RectF adjustDimens(PointF location) {

		//get the relative position of the card
		float x = location.x * surface.getWidth();
		float y = location.y * surface.getHeight();

		//get the size of the card
		PointF dimens = getCardDimensions();

		//set the card boundary and return
		RectF adjustedRect = new RectF(x, y, x + dimens.x, y + dimens.y);
		return adjustedRect;
	}

	/**
	 * gets the size (in pixels) of our cards
	 * @return
	 * 		a PointF containing the scaled size of the cards.
	 */
	private PointF getCardDimensions() {
		return new PointF(CARD_DIMENSIONS.x*CARD_DIMENSION_MODIFIER,
				CARD_DIMENSIONS.y*CARD_DIMENSION_MODIFIER);
	}
}
