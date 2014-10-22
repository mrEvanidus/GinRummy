package com.example.ginrummy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class CardAnimator implements Animation.Animator {

	private static final int FRAME_INTERVAL = 10;
	private static final int BACKGROUND_COLOR = 0xff278734;
	
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

	}

	@Override
	public void onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
	}

}