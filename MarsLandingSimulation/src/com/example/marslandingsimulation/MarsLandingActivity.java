package com.example.marslandingsimulation;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Main Activity.
 * Setting the content view to the main layout.
 * Calculating height and width of the Surface View.
 */
public class MarsLandingActivity extends Activity {
	private SView newView;
	boolean fuelUses = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		newView = (SView)findViewById(R.id.newView);
		
		// Setting the content view to the main layout.
		setContentView(R.layout.activity_mars_landing);
		// Asking window manager to display full screen.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display display = getWindowManager().getDefaultDisplay();

		Point viewSize = new Point();
		display.getSize(viewSize);
		
		// Calculating height and width of the paintView.
		int width = viewSize.x;
		int height = viewSize.y;
		newView = new SView(this.getApplicationContext(), width, height);
		LinearLayout v = (LinearLayout) findViewById(R.id.linearLayout);
		v.addView(newView);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mars_landing, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		newView.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
		newView.resume();
	}
	
	@Override
	public void onBackPressed() {
		if(newView.atMenu)
		{
			finish();
			System.exit(0);
			newView.pause();
		}
		else
		{
			newView.atMenu = true;
		}
	}
	/**
	 * get selected items from the menu.
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()){
		case R.id.exit:
			finish();
			System.exit(0);
			newView.pause();
			break;
		case R.id.reset:
			newView.reset();
			break;
		}
		return true;
	}
}
