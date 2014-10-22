package com.example.ginrummy;

import gameObjects.Card;
import android.support.v7.app.ActionBarActivity;
import android.animation.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.LinearLayout;

public class GinRummy extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gin_rummy);
		
		//link the animation surface
		Animation.AnimationSurface gameBoard = (Animation.AnimationSurface)this.findViewById(R.id.animationSurface);
		CardAnimator animator = new CardAnimator();
		gameBoard.setAnimator(animator);
		
		//link the side bar containing the buttons and such
		LinearLayout sideBar = (LinearLayout)findViewById(R.id.sideBar);
		sideBar.setBackgroundColor(animator.backgroundColor());
		
		//initiate the card images
		Card.initImages(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gin_rummy, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
