package com.example.marslandingsimulation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class SView extends SurfaceView implements Runnable,
		SurfaceHolder.Callback, OnTouchListener, SensorEventListener {
	
	static final int REFRESH_RATE = 5;
	static final int GRAVITY = 4;
	double t = 0.1;
	Thread main;
	Paint paint = new Paint();
	Bitmap background;
	Bitmap ship1, sMain, sLeft, sRight, sGround, sStars; 
	BitmapShader fillBMPshaderGround, fillBMPshaderStars;
	int DW, DH; // Display width and height
	private static final int MOVEMENT = 4;
	SensorManager mgr = null;
	float xAxis = 0;
	float yAxis = 0;
	int width = 0;
	int height = 0;

	Canvas offscreen;
	Bitmap buffer;
	Boolean gameover = false;
	float x, y;
	int Changedwidth = 0;
	Path path;

	public SView(Context context, int width, int height) {
		super(context);
		mgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mgr.registerListener(this, mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		this.width = width;
		this.height = height;
		init();
//		fuelG = (ImageView) findViewById(R.id.fuelG);

		Bitmap temShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
		Bitmap temSMain = BitmapFactory.decodeResource(getResources(), R.drawable.main);
		Bitmap temSLeft = BitmapFactory.decodeResource(getResources(), R.drawable.left);
		Bitmap temSRight = BitmapFactory.decodeResource(getResources(), R.drawable.right);
		Bitmap temSground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
		Bitmap temSStars = BitmapFactory.decodeResource(getResources(), R.drawable.stars);
		
		ship1 = Bitmap.createScaledBitmap(temShip, 120, 60, false);
		sMain = Bitmap.createScaledBitmap(temSMain, 50, 50, false);
		sLeft = Bitmap.createScaledBitmap(temSLeft, 30, 30, false);
		sRight = Bitmap.createScaledBitmap(temSRight, 30, 30, false);
		sGround = Bitmap.createScaledBitmap(temSground , 200, 200, false);
		sStars = Bitmap.createScaledBitmap(temSStars, 200, 200, false);
		fillBMPshaderGround = new BitmapShader(sGround, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		fillBMPshaderStars = new BitmapShader(sStars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
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
				// Draw path
				int xcor[] = { 0, 200, 200, 400, 400, width, width, 0, 0 };
				int ycor[] = { 700, 700, 750, 750, 600, 700, height, height, 700 };
				path = new Path();
				for (int i = 0; i < xcor.length; i++) {
					path.lineTo(xcor[i], ycor[i]);
				}
				synchronized (holder) {
					canvas = holder.lockCanvas();
                    Paint paintstars = new Paint();
					
                    paintstars.setColor(Color.BLACK);  
                    paintstars.setStyle(Paint.Style.FILL);
                    paintstars.setShader(fillBMPshaderStars);
                    
					canvas.drawPaint(paintstars);
					paint.setColor(Color.WHITE);
					if (yAxis > 2 && yAxis < 8) {
						x = x + MOVEMENT;
						y = y - 1;
						t = 1;
						canvas.drawBitmap(sLeft, x - 65, y + 28, paint);
					} else if (yAxis < -2 && yAxis > -8) {
						x = x - MOVEMENT;
						y = y - 1;
						t = 1;
						canvas.drawBitmap(sRight, x + 35, y + 28, paint);
					} else if (xAxis < 8 && xAxis > 2) {
						y = y - 4;
						t = 0.5;
						canvas.drawBitmap(sMain, x - 25, y + 35, paint);
					} else {
						y = (int) y + (int) (t + (0.5 * (GRAVITY * t * t)));
						t = t + 0.01;
					}

					// If object hit the side
					if (x < 0) {
						x = width;
					}
					if (y < 30) {
						y = 30;
					}
					if (x > width) {
						x = 0;
					}
					if (y > height) {
						y = height;
					}
					canvas.drawBitmap(ship1, x - 60, y - 30, paint);
					Paint paint1 = new Paint();
					paint1.setColor(Color.GREEN);  
					paint1.setStyle(Paint.Style.FILL);
					paint1.setShader(fillBMPshaderGround);
					canvas.drawPath(path, paint1);
				}

				if (contains(xcor, ycor, x + 60, y + 30)) {
					paint.setColor(Color.RED);
					canvas.drawCircle(x, y + 30, 20, paint);
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

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		xAxis = event.values[0];
		yAxis = event.values[1];
//		float zAxis = event.values[2];

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			yAxis = (float) Math.round(yAxis);
			xAxis = (float) Math.round(xAxis);

//			Log.d("x", Float.toString(xAxis));
//			Log.d("y", Float.toString(yAxis));
//			Log.d("z", Float.toString(zAxis));
		}
	}

	public void reset() {
		gameover = false;
		x = Changedwidth / 2;
		y = 0;
		t = 0;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Changedwidth = w;
		x = Changedwidth / 2;
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
