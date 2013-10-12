package com.example.marslandingsimulation;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MarsLandingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Adding a custom action bar with useful items for users.
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.cust_action_bar);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		
		
		// Setting the content view to the main layout.
		setContentView(R.layout.activity_mars_landing);
				
		// Asking window manager to display full screen.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// Setting up Items in the custom action bar EXIT. With new onTouchListener.
				ImageView btnExit = (ImageView) findViewById(R.id.exit);
				btnExit.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						ImageView btnExit = (ImageView) findViewById(R.id.exit);
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: btnExit.setAlpha(100); break;
						case MotionEvent.ACTION_UP: btnExit.setAlpha(255);
						askOnExit();
							break;
						}
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mars_landing, menu);
		return true;
	}
	
	/**
	 * Exit the Finger paint if user select Exit.
	 */
	private void askOnExit() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder( MarsLandingActivity.this);
		alertDialog.setPositiveButton("Exit", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				System.exit(0);
			}
		});
		alertDialog.setNegativeButton("Cancel", null);
		alertDialog.setMessage("Do you want to Quit Mars Landing?");
		alertDialog.setTitle("Quit Mars Landing");
		alertDialog.show();
	}
}
