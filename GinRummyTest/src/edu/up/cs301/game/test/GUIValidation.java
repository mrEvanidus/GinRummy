package edu.up.cs301.game.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
	
	private LinearLayout sidebar;
	private Button exitButton;
	private TextView score1,score2,messagePane;
	private RelativeLayout main;
	
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
	
	/**
	 * tests the width of the sidebar
	 */
	public void testSidebar() {
		StartupScreen();
		
		main = (RelativeLayout)testClass.findViewById(R.id.top_gui_layout);
		int width = main.getWidth();
		int height = main.getHeight();
		
		sidebar = (LinearLayout)testClass.findViewById(R.id.sidebar);
		int sbWidth = sidebar.getWidth();
		int sbHeight = sidebar.getHeight();
		
		int[] mainLoc = new int[2];
		int[] sbLoc = new int[2];
		
		main.getLocationOnScreen(mainLoc);
		sidebar.getLocationOnScreen(sbLoc);
		
		assertTrue("not on screen H", mainLoc[0] + width >= sbLoc[0] + sbWidth);
		assertTrue("On screen V", mainLoc[1] + height >= sbLoc[1] + sbHeight
				);
	}
	
	public void testMessagePane(){
		StartupScreen();
		
		main = (RelativeLayout)testClass.findViewById(R.id.top_gui_layout);
		int width = main.getWidth();
		int height = main.getHeight();
		
		sidebar = (LinearLayout)testClass.findViewById(R.id.sidebar);
		int sbWidth = sidebar.getWidth();
		int sbHeight = sidebar.getHeight();
		
		messagePane = (TextView)testClass.findViewById(R.id.messagePane);
		int mpWidth = sidebar.getWidth();
		int mpHeight = sidebar.getHeight();
		
		int[] mainLoc = new int[2];
		int[] sbLoc = new int[2];
		int[] mpLoc = new int[2];
		
		main.getLocationOnScreen(mainLoc);
		sidebar.getLocationOnScreen(sbLoc);
		messagePane.getLocationOnScreen(mpLoc);
		
		assertTrue("Message pane too wide", mpLoc[0] + mpWidth <= sbLoc[0] + sbWidth);
		assertTrue("Message pane too tall ", mpHeight <= sbHeight);
	}
	
	public void testMessageText(){
		StartupScreen();
		messagePane = (TextView)testClass.findViewById(R.id.messagePane);
		
		String msg = messagePane.getText().toString();
		assertEquals("message1 correct", "It's Your Turn:\nDraw a card.", msg);
	}
	

}
