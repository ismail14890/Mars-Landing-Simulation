package com.example.marslandingsimulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class SView extends SurfaceView implements Runnable,
		SurfaceHolder.Callback, OnTouchListener {
	
	static final int REFRESH_RATE = 5;
	static final int GRAVITY = 10;
	Thread main;

	Paint paint = new Paint();

	Bitmap background;

	int xcor[] = { 0, 200, 190, 218, 260, 275, 298, 309, 327, 336, 368, 382,
			448, 462, 476, 498, 527, 600, 600, 0, 0 };
	int ycor[] = { 616, 540, 550, 605, 605, 594, 530, 520, 520, 527, 626, 636,
			636, 623, 535, 504, 481, 481, 750, 750, 616 };

	Canvas offscreen;
	Bitmap buffer;

	boolean downPressed = false;
	boolean leftPressed = false;
	boolean rightPressed = false;
	Boolean gameover = false;

	float x, y;
	int width = 0;

	double t = 1.5;

	Path path;

	public SView(Context context) {
		super(context);
		init();
	}

	public SView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		path = new Path();

		for (int i = 0; i < xcor.length; i++) {
			path.lineTo(xcor[i], ycor[i]);
		}

		setOnTouchListener(this);

		getHolder().addCallback(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		x = event.getX();
		y = event.getY();

		t = 0;

		gameover = false;

		return true;
	}

	@Override
	public void run() {
		while (true) {
			while (!gameover) {
				Canvas canvas = null;
				SurfaceHolder holder = getHolder();
				synchronized (holder) {
					canvas = holder.lockCanvas();

					canvas.drawColor(Color.BLACK);

					paint.setColor(Color.WHITE);

					if (rightPressed == true) {
						x = x + 20;
						rightPressed = false;
					}
					if (leftPressed == true) {
						x = x - 20;
						leftPressed = false;
					}

					canvas.drawCircle(x, y, 50, paint);

					canvas.drawPath(path, paint);
				}

				y = (int) y + (int) (t + (0.5 * (GRAVITY * t * t)));
				t = t + 0.01;

				if (contains(xcor, ycor, x, y + 50)) {
					paint.setColor(Color.RED);
					canvas.drawCircle(x, y, 40, paint);

					gameover = true;
				}

				try {
					Thread.sleep(REFRESH_RATE);
				} catch (Exception e) {
				}

				holder.unlockCanvasAndPost(canvas);
			}
		}

	}

	public boolean contains(int[] xcor, int[] ycor, double x0, double y0) {
		int crossings = 0;

		for (int i = 0; i < xcor.length - 1; i++) {
			int x1 = xcor[i];
			int x2 = xcor[i + 1];

			int y1 = ycor[i];
			int y2 = ycor[i + 1];

			int dy = y2 - y1;
			int dx = x2 - x1;

			double slope = 0;
			if (dx != 0) {
				slope = (double) dy / dx;
			}

			boolean cond1 = (x1 <= x0) && (x0 < x2); // is it in the range?
			boolean cond2 = (x2 <= x0) && (x0 < x1); // is it in the reverse
														// range?
			boolean above = (y0 < slope * (x0 - x1) + y1); // point slope y - y1

			if ((cond1 || cond2) && above) {
				crossings++;
			}
		}
		return (crossings % 2 != 0); // even or odd
	}
	
	public void reset()
	{	
		gameover = false;
		
		x = width /2;
		y = 0;
		t = 0;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		x = width / 2;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		main = new Thread(this);
		if (main != null)
			main.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}
