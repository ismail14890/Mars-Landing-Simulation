package com.example.marslandingsimulation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MarsLandingActivity extends Activity {
	private SView newView;

	Bitmap mainBM;
	ProgressBar progressHorizontal = null;
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
        float progressBar = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

		Point viewSize = new Point();
		display.getSize(viewSize);
		
		// Calculating height and width of the paintView.
		int width = viewSize.x;
		int height = (int) (viewSize.y - progressBar);
		newView = new SView(this.getApplicationContext(), width, height);
		LinearLayout v = (LinearLayout) findViewById(R.id.linearLayout);
		v.addView(newView);
		progressHorizontal = (ProgressBar) findViewById(R.id.progressBar1);
		progressHorizontal.setMax(100);
		progressHorizontal.setProgress(100);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mars_landing, menu);
		return true;
	}
	
	/**
	 * Call AskOnEixt () when user pressed back.
	 */
	@Override
	public void onBackPressed() {
//		askOnExit();
		finish();
		System.exit(0);
	}
	
	public void fuel() {
	}
	
	/**
	 * get selected items from the menu.
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()){
		case R.id.exit:
			askOnExit();
			break;
		}
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
