package com.example.marslandingsimulation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MarsLandingActivity extends Activity {
	private SView newView;

	Bitmap mainBM;
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
		
//		final Button btnRestart = (Button)findViewById(R.id.btnRestart);
//        btnRestart.setOnClickListener(new OnClickListener()
//        {	
//			@Override
//			public void onClick(View v)
//			{
//				newView.reset();
//				newView.invalidate();
//			}
//		});
//        
//        final Button btnRight = (Button)findViewById(R.id.btnRight);
//        btnRight.setOnClickListener(new OnClickListener()
//        {	
//			@Override
//			public void onClick(View v)
//			{
//				newView.rightPressed = true;
//			}
//		});
//        
//        final Button btnLeft = (Button)findViewById(R.id.btnLeft);
//        btnLeft.setOnClickListener(new OnClickListener()
//        {	
//			@Override
//			public void onClick(View v)
//			{
//				newView.leftPressed = true;
//			}
//		});
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
