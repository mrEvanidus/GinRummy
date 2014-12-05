package edu.up.cs301.ginrummy;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.*;
import android.os.Handler;
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
import edu.up.cs301.game.util.MessageBox;

/**
 * A GUI that allows a human to play Gin Rummy. Moves are made by clicking
 * regions on a surface. Presently, it is laid out for landscape orientation. If
 * the device is held in portrait mode, the cards will be very long and skinny.
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 * 
 * @author Jaimiey Sears
 * @author Eric Tsai
 * @version November 2014
 */
public class GRHumanPlayer extends GameHumanPlayer implements Animator {

	// how much a card on top of another should be offset by
	private final static float STACKED_CARD_OFFSET = 0.005F;  	
	private final static float HAND_CARD_OFFSET = 0.06F;	
	private final static float MELD_OFFSET = 0.05F;

	// the width and height of the card images
	private final static PointF CARD_DIMENSIONS = new PointF(500, 726);
	// the size a card should be grown or shrunk by
	// TODO: for better device cross-compatibility,
	//	make this change based on canvas size
	private static float CARD_DIMENSION_MODIFIER = 0.4f;

	// colors used
	public static final int FELT_GREEN = 0xff277714;
	public static final int LAKE_ERIE = 0xff6183A6;

	// our game state
	protected GRState state;

	// our activity
	private Activity myActivity;

	// the animation surface
	private AnimationSurface surface;

	// the buttons. 
	//	TODO: figure out a way to implemet "new game"
	Button exitButton, newGame;

	// the score and message pane text fields
	private TextView oppScore, myScore, messagePane; 

	// moving card information for mine and my opponent's cardss
	CardPath path, opponentPath;

	// card being moved
	private Card touchedCard;

	// Coordinates of card being moved
	private PointF touchedPos, originPos;

	// the positions of the decks
	protected static PointF stockPos, discardPos, knockPos;

	//positions of the players' hands
	protected static ArrayList<ArrayList<PointF>> playerHandPos;

	//card order in this player's hand
	private ArrayList<Card> handOrder;

	//what are the player indices
	private int myIdx, otherIdx;

	//whether the GUI is locked or not
	private boolean lockGUI;

	//keeps track of what action we just performed
	private boolean drewFromStock, justDrew;

	/**
	 * constructor
	 * 
	 * @param name
	 *            the player's name
	 */
	public GRHumanPlayer(String name) {
		super(name);

		//initialize the hand-positions of the players
		playerHandPos = new ArrayList<ArrayList<PointF>>();
		playerHandPos.add(new ArrayList<PointF>());
		playerHandPos.add(new ArrayList<PointF>());

		//initialize the card order
		handOrder = new ArrayList<Card>();
	}

	/**
	 * callback method: we have received a message from the game
	 * 
	 * @param info
	 *            the message we have received from the game
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		Log.i("GRComputerPlayer", "receiving updated state (" + info.getClass()
				+ ")");
		if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
			// if we had an out-of-turn or illegal move, flash the screen
			surface.flash(Color.RED, 10);
		} else if (!(info instanceof GRState)) {
			// otherwise, if it's not a game-state message, ignore
			return;
		} else {
			// it's a game-state object: update the state. Since we have an
			// animation
			// going, there is no need to explicitly display anything. That will
			// happen
			// at the next animation-tick, which should occur within 1/20 of a
			// second
			this.state = (GRState) info;
			Log.i("human player", "receiving");

			//score messages
			int score1 = 
					(myIdx == 0 ? state.getp1score() : state.getp2score());
			int score2 = 
					(otherIdx == 0 ? state.getp1score() : state.getp2score());
			oppScore.setText("Opponent Score: " + Integer.toString(score2));
			myScore.setText("Your Score: " + Integer.toString(score1));

			//make the new game button invisible
			newGame.setVisibility(View.INVISIBLE);

			//if hand is over show an appropriate message
			if (state.isEndOfRound) {
				//lock the gui so cards cannot be moved
				lockGUI = true;
				messagePane.setText("Round over.\nTouch anywhere to see scores!");
				//return;
			}
			else{
				lockGUI = false;

				// my turn messages 
				if (state.whoseTurn() == myIdx){
					if (state.getPhase() == GRState.DRAW_PHASE) {
						messagePane.setText("It's Your Turn:\nDraw a card.");

						if (playerHandPos.get(otherIdx).size() == 0) return;

						//animate opponent's discard
						//from hand to discard pile
						PointF dst = discardPos;
						PointF org = playerHandPos.get(otherIdx).get(0);

						// start moving the card
						CardPath newPath = new CardPath(new backCard(), org, dst);
						newPath.setAnimationSpeed(5);
						opponentPath = newPath;
					}
					else if (state.getPhase() == GRState.DISCARD_PHASE) {
						messagePane.setText("It's Your Turn:\nDiscard a Card.");
					}
				}

				//opponent turn messages
				else{
					messagePane.setText("Your opponent is taking their turn.");

					//					PointF org = null;
					//					PointF dst = null;

					if (playerHandPos.get(otherIdx).size() == 0) return;

					// animate opponent's moves as they happen
					if (state.getPhase() == GRState.DRAW_PHASE) {
						//from stockpile to hand
						//TODO change this to discard pile when the opponent
						//								draws from discard
						PointF org = stockPos;
						PointF dst = playerHandPos.get(otherIdx).get(0);

						// start moving the card
						CardPath newPath = new CardPath(new backCard(), org, dst);
						newPath.setAnimationSpeed(5);
						opponentPath = newPath;
					}
				}
			}
		}
	}

	/**
	 * call-back method: called whenever the GUI has changed (e.g., at the
	 * beginning of the game, or when the screen orientation changes).
	 * 
	 * @param activity
	 *            the current activity
	 */
	public void setAsGui(GameMainActivity activity) {

		this.myActivity = activity;

		// Load the layout resource for the new configuration
		activity.setContentView(R.layout.activity_gin_rummy);

		// link the animator to the animation surface
		surface = (AnimationSurface) activity
				.findViewById(R.id.animationSurface);
		surface.setAnimator(this);

		// initialize the buttons
		exitButton = (Button) activity.findViewById(R.id.exitButton);
		newGame = (Button) activity.findViewById(R.id.newGame);

		// initialize the text fields
		oppScore = (TextView) activity.findViewById(R.id.opponentScore);
		myScore = (TextView) activity.findViewById(R.id.playerScore);
		messagePane = (TextView) activity.findViewById(R.id.messagePane);

		// set up exit button listener
		exitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBox.popUpChoice("Are you sure?", 
						"Yes, I admit that I am a poor sport and still want to exit.", 
						"No, keep playing the awesome game!",

						//listener for "yes"
						new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						// quit the game
						System.exit(0);
					}},

					//listener for "no"
					new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							//do nothing, return to game
						}},
						myActivity); //pop-up choice
			}
		});

		//set up new game button listener
		newGame.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				newGame(); 
			}
		});

		// read in the card images
		backCard.initImages(activity);
		Card.initImages(activity);

		// set the location of the decks
		knockPos = new PointF(0.1f, 0.25f);
		stockPos = new PointF(0.3f, 0.25f);
		discardPos = new PointF(stockPos.x + 0.2f, 0.25f);

		//initially unlock the GUI
		lockGUI = false;

		// if the state is not null, simulate having just received the state so
		// that any state-related processing is done
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
	 * @return the animation interval, in milliseconds
	 */
	public int interval() {
		// 1/1000 of a second
		return 10;
	}

	/**
	 * @return the background color
	 */
	public int backgroundColor() {
		return FELT_GREEN;
	}

	/**
	 * @return whether the animation should be paused
	 */
	public boolean doPause() {
		return false;
	}

	/**
	 * @return whether the animation should be terminated
	 */
	public boolean doQuit() {
		return false;
	}



	/**
	 * callback-method: we have gotten an animation "tick"; redraw the screen
	 * image: - the middle deck, with the top card face-up, others face-down -
	 * the two players' decks, with all cards face-down - a red bar to indicate
	 * whose turn it is
	 * 
	 * @param g
	 *            the canvas on which we are to draw
	 */
	public synchronized void tick(Canvas canvas) {
		// ignore if we have not yet received the game state
		if (state == null) return;

		GRState stateCopy = new GRState(state);

		//find out my id
		myIdx = state.yourId;
		otherIdx = (myIdx == 0 ? 1 : 0);

		//display players' melds if it's the end of a round
		if (state.isEndOfRound) {
			displayMelds(0,canvas);
			displayMelds(1,canvas);
			return;
		}

		//if card is not in hand Order, but is supposed to be, add it
		//this should happen at the beginning of a round and after a draw action
		ArrayList<Card> myHand = state.getHand(myIdx).cards;
		for (Card c : myHand) { 
			if (!handOrder.contains(c)){
				handOrder.add(c);

				//if we've just drawn a card, we can drag it
				if(stateCopy.getPhase() == GRState.DISCARD_PHASE) {
					touchedCard = c;
					touchedPos = (drewFromStock ? stockPos: discardPos);
					originPos = playerHandPos.get(myIdx).get(
							playerHandPos.get(myIdx).size()-1);
				}
			}
		}

		//if card is in hand order which is not supposed to be, remove it
		//this should happen after a discard or knock action.
		for (Card c : new ArrayList<Card>(handOrder)) {
			if (!myHand.contains(c)) handOrder.remove(c);
		}

		//empty the hand positions for repopulation
		playerHandPos.get(myIdx).clear();
		playerHandPos.get(otherIdx).clear();

		//set up the position of all the cards in the hands
		for (int i = 0; i < handOrder.size(); i++) {
			playerHandPos.get(myIdx).add(new PointF(0.05f + HAND_CARD_OFFSET*i, 0.75f));
		}
		for (int i = 0; i < stateCopy.getHand(1).cards.size(); i++) {
			playerHandPos.get(otherIdx).add(new PointF(0.55f - HAND_CARD_OFFSET*i, -0.25f));
		}

		//draw the hands
		drawHand(canvas, new ArrayList<Card>(handOrder), playerHandPos.get(myIdx));
		drawHand(canvas, stateCopy.getHand(otherIdx).cards, playerHandPos.get(otherIdx));

		// draw the knocking box and discard box
		drawBoundBox(canvas, "KNOCK", knockPos);
		drawBoundBox(canvas, "DISCARD", discardPos);

		//draw the stock and discard piles
		drawDeck(canvas, stateCopy.getStock(), stockPos);
		drawDeck(canvas, stateCopy.getDiscard(), discardPos);

		if (path != null) {
			// advance the card along the path
			PointF newPos = path.advance();

			// draw the moving cards
			if (newPos != null){
				path.getCard().drawOn(canvas, adjustDimens(newPos));
			}

			// if the animation is done, remove the animation
			if (path != null && path.isComplete()) path = null;

		}
		if (opponentPath != null) {
			// advance the card along the path
			PointF newPos = opponentPath.advance();

			// draw the moving cards
			if (newPos != null){
				//			PointF newPos = opponentPath.getPosition();
				opponentPath.getCard().drawOn(canvas, adjustDimens(newPos));
			}

			// if the animation is done, remove the animation
			if (opponentPath != null && opponentPath.isComplete()) opponentPath = null;
		}

		// draw the card being dragged
		if (touchedCard != null && touchedPos != null) {
			touchedCard.drawOn(canvas, adjustDimens(touchedPos));
		}

	}//tick

	/**
	 * Draw the knocking box onto the canvas
	 * @param canvas
	 * 			the Canvas to paint on
	 * @param col
	 * 			the integer color the box should be
	 */
	private void drawBoundBox(Canvas canvas, String name, PointF where, Paint p) {

		canvas.drawText(name, where.x * surface.getWidth()
				+ getCardDimensions().x / 6, where.y * surface.getHeight()
				+ getCardDimensions().y / 2, p);

		canvas.drawRoundRect(adjustDimens(where), 10F, 10F, p);		
	}

	private void drawBoundBox(Canvas canvas, String name, PointF where) {
		//set up a default paint
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		p.setColor(Color.GREEN);
		p.setTextSize(24);

		drawBoundBox(canvas, name, where, p);
	}

	/**
	 * Draw the specified deck as a stack of cards
	 * @param canvas
	 * 			the canvas to paint on
	 * @param deck
	 * 			the Deck of cards to draw
	 * @param pos
	 * 			a PointF of the location to draw the deck
	 */
	synchronized private void drawDeck(Canvas canvas, Deck deck, PointF pos) {
		// draw the stack of cards
		for (Card card : deck.cards) {
			int n = deck.cards.indexOf(card);

			// add a few pixels to the position
			RectF position = adjustDimens(pos);
			position.set(new RectF(position.left + STACKED_CARD_OFFSET
					* position.width() * n, position.top
					- STACKED_CARD_OFFSET * position.height() * n,
					position.right + STACKED_CARD_OFFSET * position.width()
					* n, position.bottom - STACKED_CARD_OFFSET
					* position.height() * n));

			// draw the card
			card.drawOn(canvas, position);
		}

	}

	/**
	 * Draws the specified arrayList of Cards onto the specified arrayList of positions
	 * @param canvas
	 * 			The canvas to draw on
	 * @param hand
	 * 			The hand to draw
	 * @param pos
	 * 			Where to draw the hand
	 */
	synchronized private void drawHand(Canvas canvas, ArrayList<Card> hand, ArrayList<PointF> pos) {
		for (PointF p : pos) {
			int n = pos.indexOf(p);
			if(n >= hand.size()){
				break;
			}
			Card card = hand.get(n);

			// draw the card, if it is not being dragged or animated
			if (card != null && (touchedCard != null && touchedCard.equals(card))
					|| (path != null && path.getCard().equals(card))) {
				// don't draw the card
			} else {
				// draw the card
				card.drawOn(canvas, adjustDimens(p));
			}
		}
	}

	/**
	 * Display the melds after a knock
	 * 
	 * @param playerIndex
	 * 			The index of the player
	 */
	synchronized private void displayMelds(int playerIndex, Canvas canvas) {
		ArrayList<Meld> playerMelds = state.getMeldsForPlayer(playerIndex);	
		ArrayList<PointF> playerHandPos = new ArrayList<PointF>();
		float cardsY;
		float cardSpacer = 0;
		float yPosOfCard;
		float startOfDeadwoodX;

		//decide which player hand position we need
		if (playerIndex == 0) cardsY = 0.55f;		
		else if (playerIndex == 1) cardsY = 0f;
		else {
			Log.v("Error", "Invalid Player Specified for displayMelds()");
			return;
		}

		yPosOfCard = cardsY;
		playerHandPos.clear();		
		//Iterate through each group of melds. 
		for (Meld meld : playerMelds) {
			int indexOfMeld = playerMelds.indexOf(meld);
			//Iterate through each card in a meld
			//"meldCard" is a card in "melds"
			for (Card meldCard : meld.getMeldCards()) {
				yPosOfCard = cardsY + MELD_OFFSET*indexOfMeld;
				playerHandPos.add(new PointF(0.02f + HAND_CARD_OFFSET*cardSpacer, yPosOfCard));

				//the last index of playerHandPos is the current meldCard
				int lastIndex = playerHandPos.size() - 1;
				meldCard.drawOn(canvas, adjustDimens(playerHandPos.get(lastIndex)));
				cardSpacer++;
			}					
		}	


		startOfDeadwoodX = 0.02f + HAND_CARD_OFFSET*cardSpacer;		
		displayDeadwood(playerIndex, startOfDeadwoodX, yPosOfCard + MELD_OFFSET, canvas);

	}

	/**
	 * TODO
	 * displays deadwood of respective player
	 * 
	 * @param playerIdx
	 * 			The index of the player
	 * @param startPosX
	 * 			the X-coordinate of the place to start drawing the deadwood
	 * @param startPosY
	 * 			the Y-coordinate of the place to start drawing the deadwood
	 * @param canvas
	 * 			the canvas to draw on
	 */
	synchronized private void displayDeadwood(int playerIdx, float startPosX, 
			float startPosY, Canvas canvas) {
		ArrayList<Card> playerDeadwood = state.getDeadwoodForPlayer(playerIdx);	
		ArrayList<PointF> playerHandPos = new ArrayList<PointF>();
		
		//set up the paint with which we will cover the deadwood cards 
		Paint grayShade = new Paint();
		grayShade.setColor(0xccd3d3d3);

		//TODO: streamline this. playerHandPos is also used on the top GRHuman level
		playerHandPos.clear();
		//Iterate through each group of melds. 
		for (Card c : playerDeadwood) {
			int indexOfCard = playerDeadwood.indexOf(c);
			playerHandPos.add(new PointF(startPosX + HAND_CARD_OFFSET*indexOfCard, startPosY));

			//the last index of playerHandPos is the current meldCard
			int lastIndex = playerHandPos.size() - 1;
			PointF cardPos = playerHandPos.get(lastIndex);
			c.drawOn(canvas, adjustDimens(cardPos));

			drawBoundBox(canvas, "", cardPos, grayShade);
			//			canvas.drawRoundRect(adjustDimens(cardPos), 10, 10, grayShade);
		}	
	}

	/**
	 * callback method: we have received a touch on the animation surface
	 * 
	 * @param event
	 *            the motion-event
	 */
	public void onTouchEvent(MotionEvent event) {
		if (state == null) return;

		//if the GUI is locked, it means we are at the end of the round
		//and touching the board anywhere should show the round end dialog
		if (lockGUI) {
			if (event.getAction() != MotionEvent.ACTION_DOWN) return;

			//pop up the message
			//TODO only the local human player should
			//be able to advance the round
			showEndRoundChoice(state.gameMessage);

			//don't allow other interaction with the game
			return;
		}

		// get the location of the touch on the surface
		int touchX = (int) event.getX();
		int touchY = (int) event.getY();

		// on down touch events:
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			poke(touchX, touchY);
		} // ACTION_DOWN

		// on finger-lift events
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (touchedCard != null) drop(touchedCard, touchX, touchY);
		} // ACTION_UP

		//on dragging events
		else if (event.getAction() == MotionEvent.ACTION_MOVE){
			// when we move a card, move it (from its center)
			// set screen relative position of the dragged card
			touchedPos = new PointF(
					((float) touchX - getCardDimensions().x / 2)
					/ (float) surface.getWidth(),
					((float) touchY - getCardDimensions().y / 2)
					/ (float) surface.getHeight());
		}// ACTION_MOVE
	}// onTouch

	/**
	 * Handles dropping a card onto the specified location
	 * @param card
	 * 			the Card object to drop
	 * @param x
	 * 			the integer x-coordinate of the drop 
	 * @param touchY
	 * 			the integer y-coordinate of the drop
	 */
	synchronized private void drop(Card card, int x, int y) {
		//null check
		if (card == null) return;

		//dropped on discard pile
		if (adjustDimens(discardPos).contains(x, y) && (!justDrew || drewFromStock)) {
			//discard the card
			game.sendAction(new GRDiscardAction(this, card));
		}

		//dropped on knock box
		else if (adjustDimens(knockPos).contains(x, y)) {
			game.sendAction(new GRKnockAction(this, card));
		}

		//dropped on hand
		else if (handContains(playerHandPos.get(myIdx), x, y)){
			//rearrange cards in hand
			int dest = playerHandPos.get(myIdx).indexOf(card);
			for (PointF p : playerHandPos.get(myIdx)) {
				//if this card was touched
				if (adjustDimens(p).contains(x, y))
					dest = playerHandPos.get(myIdx).indexOf(p);
			}

			//move the card
			handOrder.remove(card);
			handOrder.add(dest, card);
		}

		//dropped somewhere else
		else{
			// have the card move back to its origin
			CardPath newPath = new CardPath(card, touchedPos, originPos);
			newPath.setAnimationSpeed(5);
			path = newPath;
		}

		//nullify the touched card so we don't draw it
		touchedCard = null;
		touchedPos = null;

		//we did not just draw
		justDrew = false;
	}

	/**
	 * The checks to perform on the Me
	 * @param touchX
	 * @param touchY
	 */
	synchronized private void poke(int x, int y) {

		//stock poked
		if (adjustDimens(stockPos).contains(x, y)) {
			if (state.getPhase() == GRState.DRAW_PHASE){
				drewFromStock = true;
				justDrew = true;
				game.sendAction(new GRDrawAction(this, drewFromStock));
			}
		}

		//discard poked: send draw from discard action
		else if (adjustDimens(discardPos).contains(x, y)){
			if (state.getPhase() == GRState.DRAW_PHASE){
				drewFromStock = false;
				justDrew = true;
				game.sendAction(new GRDrawAction(this, drewFromStock));
			}
		}

		//hand poked
		else if (handContains(playerHandPos.get(myIdx), x, y)) {
			int origin = 0;
			for (PointF p : playerHandPos.get(myIdx)) {
				//if this card was touched
				if (adjustDimens(p).contains(x, y)) {
					origin = playerHandPos.get(myIdx).indexOf(p);
					originPos = p;
				}
			}

			//pick up the card
			touchedCard = handOrder.get(origin);
		}
	}//poke

	/**
	 * Shows a pop-up message asking the player if they want
	 * to continue to a new round or return to look at their cards. 
	 * @param msg
	 * 			the message to display in the pop-up window
	 */
	private void showEndRoundChoice(String msg) {
		//message box to show at the end of the round
		MessageBox.popUpChoice(msg, "Next Round", "Back to Melds",

				//listener for when the "next round" button is pressed
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				// start a new round
				nextRound();
				handOrder.clear();
			}},

			//listener for when the "Back to Melds" button is pressed 
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					//do nothing, return to game
				}},
				myActivity); //pop-up choice
	}

	/**
	 * Shows a pop-up message letting the player know that the round is over. 
	 * @param msg
	 * 			the message to display in the pop-up window
	 */
	private void showEndRoundMessage(String msg) {
		//message box to show at the end of the round
		MessageBox.popUpMessage(msg 
				+ "\n, Waiting for host to begin next round", 
				myActivity); //pop-up choice
	}

	/**
	 * Checks if a hand contains the given point
	 * @param posList
	 * 			the list of positions to check inside
	 * @param x
	 * 			the x-coordnate of the point
	 * @param y
	 * 			the y-coordinate of the point
	 * @return
	 * 		TRUE: if the position list contains the point
	 */
	synchronized private boolean handContains(ArrayList<PointF> posList, int x, int y) {
		for (PointF p : posList) {
			if (adjustDimens(p).contains(x, y)) return true;
		}

		return false;
	}

	/**
	 * gets the size (in pixels) of our cards
	 * 
	 * @return a PointF containing the scaled size of the cards.
	 */
	public static PointF getCardDimensions() {
		return new PointF(CARD_DIMENSIONS.x * CARD_DIMENSION_MODIFIER,
				CARD_DIMENSIONS.y * CARD_DIMENSION_MODIFIER);
	}

	/**
	 * 
	 * @param location
	 *            a PointF which describes the location(in screen percent) where
	 *            the card will be drawn
	 * @return a RectF describing the boundary where the card will be drawn
	 */
	private RectF adjustDimens(PointF location) {

		// get the relative position of the card
		float x = location.x * surface.getWidth();
		float y = location.y * surface.getHeight();

		// get the size of the card
		PointF dimens = getCardDimensions();

		// set the card boundary and return
		RectF adjustedRect = new RectF(x, y, x + dimens.x, y + dimens.y);
		return adjustedRect;
	}

	/**
	 * requests to move to the next round
	 * so that we can send the action from inside an onclick method
	 */
	private void nextRound() {
		game.sendAction(new GRNextRoundAction(this));
	}

	/**
	 * requests to start a new game
	 * so that we can send the action from inside an onclick method
	 */
	private void newGame() {
		//TODO: implement NEW GAME action
		game.sendAction(new GRNewGameAction(this));
	}
}
