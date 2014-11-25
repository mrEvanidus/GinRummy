package edu.up.cs301.game.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.widget.TextView;
import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.ginrummy.GRHumanPlayer;
import edu.up.cs301.ginrummy.GRMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.*;

public class GUIValidation extends
		
		ActivityInstrumentationTestCase2<GRMainActivity> {
	
	private GRMainActivity testClass;
	private TextView theResult;
	private float playerCardXPosReference = 0.05f;
	private float playerCardYPosReference = 0.75f;
	private float discardPosXReference =  0.03f;
	private float discardPosYReference = 0.25f;
	private AnimationSurface surface;
	private SurfaceHolder holder;
	
	/*
	 * Constructor
	 * 
	 * Invoke superclass' constructor to tell JUnit what class to test
	 */
	public GUIValidation() {		
		
		super(GRMainActivity.class);		
	}
	
	@Override
	/*
	 * This method is called before every test to initialize 
	 * variables and clean up side effects from previous tests
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	protected void setUp() throws Exception {		
		super.setUp();
		
		testClass = getActivity();
		theResult = (TextView) testClass.findViewById(R.id.messagePane);
		setActivityInitialTouchMode(false);
		
		
		surface = (AnimationSurface) testClass
				.findViewById(R.id.animationSurface);
		
		
	}	
	
	/*
	 * Get past the Main Menu to play Gin Rummy!!!!! 
	 */
	public void StartupScreen() {
		sendKeys(KeyEvent.KEYCODE_ENTER);
		sendKeys(KeyEvent.KEYCODE_ENTER);		
		sendKeys(KeyEvent.KEYCODE_ENTER);
		sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		sendKeys(KeyEvent.KEYCODE_ENTER);		
	}
	
	
	
//	/*
//	 * Test message pane displays "It's Your Turn Draw a card."
//	 * 
//	 */
//	public void testDrawCardMessage() {
//		
//		return;
//	}
	
	
	/*
	 * 
	 * 
	 */
	public void testDiscardMessage() {
		StartupScreen();
//		holder = surface.getHolder();
//		holder.lockCanvas();
//		TouchUtils.drag(this, playerCardXPosReference*surface.getWidth(), discardPosXReference*surface.getWidth(),
//				playerCardYPosReference*surface.getHeight(), discardPosYReference*surface.getHeight(), 10);
//		assertEquals("Message should indicate it's now the opponent's turn", ""
//				+ "Your opponent is taking their turn.", "Your opponent is taking their turn.");
		
		//sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		//sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		//sendKeys(KeyEvent.KEYCODE_DPAD_UP);
		//sendKeys(KeyEvent.KEYCODE_DPAD_LEFT);
		//sendKeys(KeyEvent.KEYCODE_DPAD_RIGHT);
		
	}
	
	
	

}
