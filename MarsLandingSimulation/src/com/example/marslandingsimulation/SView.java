package com.example.marslandingsimulation;

import java.io.InputStream;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * SView Surface View.
 * Detect when the space craft intersects with the martian landscape.
 * Scale the Terrain, spaceship and movement that will work with other resolutions.
 * Setup Accelerometer to control the space craft’s main, left and right thrusters
 * Setup sounds effects.
 * setup an event handler to handle onTouch
 * handle the physics
 */
public class SView extends SurfaceView implements Runnable,
		SurfaceHolder.Callback, OnTouchListener, SensorEventListener {
	
	SoundPool sP;
	MediaPlayer mp;
	Movie explodeGif;
	int explosion1;
	
	static final int REFRESH_RATE = 5;
	static final int GRAVITY = 4;
	static final int MOVEMENT = 4;
	static final int fuelUsage = 2;
	double t = 0.1;
	static final int maxFuel = 800;
	int fuel = maxFuel;
	boolean fuelFinished = false;
	boolean pause = false;
	Boolean gameover = false;
	boolean atMenu = false;
	Thread main;
	Paint paint = new Paint();
	Bitmap ship,shipCrash, sMain, sLeft,sRight, sGround, sStars, sMessage, sMessage2, sMenu, landing; 
	int shipW, shipH, sMainSize, sLeftSize, sGroundSize, sStarsSize, sMainSizeH, sLeftSizeH, 
	sGroundSizeH, sStarsSizeH, sMessageSize, sMessageSizeH;
	BitmapShader fillBMPshaderGround, fillBMPshaderStars;
	float x, y;
	float sX, sY;
	float animStart = 0;
	SensorManager newSensor = null;
	float xAxis = 0;
	float yAxis = 0;
	int width = 0;
	int height = 0;
	ArrayList<Integer> xcorland = new ArrayList<Integer>();
	ArrayList<Integer> ycorland = new ArrayList<Integer>();
	ArrayList<Integer> xcor = new ArrayList<Integer>();
	ArrayList<Integer> ycor = new ArrayList<Integer>();
	Canvas offscreen;
	Bitmap buffer;
	
	int Changedwidth = 0;
	Path path;

	public SView(Context context, int width, int height) {
		super(context);
		newSensor = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		newSensor.registerListener(this, newSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		this.width = width;
		this.height = height;
		sX = 1920 / (float) width;
		sY = 1080 / (float) height;
		x = (float)(100/sX);
		y = (float)(40/sY);
		shipW = (int) (120 / sX);
		shipH = (int) (60 / sY);
		sMainSize = (int) (50 / sX);
		sMainSizeH = (int) (50 / sY);
        sLeftSize = (int) (30 / sX);
        sLeftSizeH = (int) (30 / sY);
        sGroundSize = (int) (300 / sX);
        sGroundSizeH = (int) (300 / sY);
        sStarsSize = (int) (300 / sX);
        sStarsSizeH = (int) (300 / sY);
        sMessageSize = (int) (1920 / sX);
        sMessageSizeH = (int) (1080 / sY);
        Bitmap temShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1);
        Bitmap temShipcrash = BitmapFactory.decodeResource(getResources(), R.drawable.shipcrash);
		Bitmap temSMain = BitmapFactory.decodeResource(getResources(), R.drawable.main);
		Bitmap temSLeft = BitmapFactory.decodeResource(getResources(), R.drawable.left);
		Bitmap temSRight = BitmapFactory.decodeResource(getResources(), R.drawable.right);
		Bitmap temSground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
		Bitmap temSStars = BitmapFactory.decodeResource(getResources(), R.drawable.stars);
		Bitmap temGameover = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
		Bitmap temCongrat = BitmapFactory.decodeResource(getResources(), R.drawable.congrat);
		Bitmap temMenu = BitmapFactory.decodeResource(getResources(), R.drawable.menubg);
		Bitmap temlanding = BitmapFactory.decodeResource(getResources(), R.drawable.landing);
		ship = Bitmap.createScaledBitmap(temShip, shipW, shipH, true);
		shipCrash = Bitmap.createScaledBitmap(temShipcrash, shipW, shipH, true);
		sMain = Bitmap.createScaledBitmap(temSMain, sMainSize, sMainSizeH, true);
		sLeft = Bitmap.createScaledBitmap(temSLeft, sLeftSize, sLeftSizeH, true);
		sRight = Bitmap.createScaledBitmap(temSRight, sLeftSize, sLeftSizeH, true);
		sGround = Bitmap.createScaledBitmap(temSground , sGroundSize, sGroundSizeH, true);
		sStars = Bitmap.createScaledBitmap(temSStars, sStarsSize, sStarsSizeH, true);
		sMessage = Bitmap.createScaledBitmap(temGameover, sMessageSize, sMessageSizeH, true);
		sMessage2 = Bitmap.createScaledBitmap(temCongrat, sMessageSize, sMessageSizeH, true);
		sMenu = Bitmap.createScaledBitmap(temMenu, width, height, true);
		landing = Bitmap.createScaledBitmap(temlanding, (int) (200 / sX), (int) (25 / sY), true);
		fillBMPshaderGround = new BitmapShader(sGround, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		fillBMPshaderStars = new BitmapShader(sStars, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		
		init();
		atMenu = true;
		mp = MediaPlayer.create(getContext(), R.raw.rocket);
		mp.setLooping(true);
		sP = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		explosion1 = sP.load(getContext(), R.raw.explosion, 1);
		InputStream is = this.getContext().getResources().openRawResource(R.drawable.explosion);
		explodeGif = Movie.decodeStream(is);
	}

	public SView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	/**
	 * Initialises ontouchlistener and holder.
	 */
	public void init() {
		setOnTouchListener(this);
		getHolder().addCallback(this);
	}
	
	/**
	 * Scale display from current testing device (S4)
	 * Add points for landing area
	 * Add points for the martian landscape
	 * Draw path for martian landscape
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Scale display from current testing device (S4)
		sX = 1920 / (float) w;
		sY = 1080 / (float) h;
		//Add points for landing area
		xcorland.add((int) (1140 / sX));	ycorland.add((int) (1005 / sY));
		xcorland.add((int) (1140 / sX));	ycorland.add((int) (1030 / sY));
		xcorland.add((int) (1340 / sX));	ycorland.add((int) (1030 / sY));
		xcorland.add((int) (1340 / sX));	ycorland.add((int) (1005 / sY));
		xcorland.add((int) (1140 / sX));	ycorland.add((int) (1005 / sY));
		//Add points for the martian landscape
		xcor.add(0);					ycor.add((int) (980 / sY));
		xcor.add((int) (80 / sX)); 		ycor.add((int) (880 / sY));
		xcor.add((int) (190 / sX));		ycor.add((int) (600 / sY));
		xcor.add((int) (300 / sX));		ycor.add((int) (700 / sY));
		xcor.add((int) (570 / sX));		ycor.add((int) (780 / sY));
		xcor.add((int) (710 / sX));		ycor.add((int) (560 / sY));
		xcor.add((int) (890 / sX));		ycor.add((int) (430 / sY));
		xcor.add((int) (1090 / sX)); 	ycor.add((int) (470 / sY));
		xcor.add((int) (1210 / sX)); 	ycor.add((int) (720 / sY));
		xcor.add((int) (1140/ sX)); 	ycor.add((int) (940 / sY));
		xcor.add((int) (1140 / sX)); 	ycor.add((int) (1030 / sY));
		xcor.add((int) (1340 / sX)); 	ycor.add((int) (1030 / sY));
		xcor.add((int) (1420 / sX)); 	ycor.add((int) (930 / sY));
		xcor.add((int) (1590 / sX)); 	ycor.add((int) (840 / sY));
		xcor.add((int) (1710 / sX)); 	ycor.add((int) (590 / sY));
		xcor.add((int) (1810 / sX)); 	ycor.add((int) (490 / sY));
		xcor.add(width); 				ycor.add((int) (520 / sY));
		xcor.add(width); 				ycor.add(height);
		xcor.add(0); 					ycor.add(height);
		xcor.add(0); 					ycor.add((int) (980 / sY));
		// Draw path for martian landscape
		path = new Path();
		for (int i = 0; i < xcor.size(); i++) {
			path.lineTo(xcor.get(i), ycor.get(i));
		}
	}
	
	/**
	 * Using OnTouch method to confirm the player
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(gameover){
			reset();
		}
		return true;
	}
	
	/**
	 * Main thread for SView to draw onto the canvas
	 */
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			while (!pause) {
				Canvas canvas = null;
				SurfaceHolder holder = getHolder();
				synchronized (holder) {
					canvas = holder.lockCanvas();
					// Stars background
					Paint paintstars = new Paint();
					paintstars.setColor(Color.BLACK);
					paintstars.setStyle(Paint.Style.FILL);
					paintstars.setShader(fillBMPshaderStars);
					canvas.drawPaint(paintstars);
					Paint paint1 = new Paint();
					paint1.setColor(Color.GREEN);
					paint1.setStyle(Paint.Style.FILL);
					paint1.setShader(fillBMPshaderGround);
					canvas.drawPath(path, paint1);
					// Fuel
					Paint paintFuel = new Paint();
					paintFuel.setColor(Color.DKGRAY);
					canvas.drawRect(0, (float)((height)-(5/sY)), (float)((width/2)+(5/sY)), (float)((height)-(35/sY)), paintFuel);
					paint.setColor(Color.CYAN);
					canvas.drawRect(0, (float)((height)-(10/sY)), fuel * (float)(width/2) / maxFuel, (float) ((height)-(30/sY)), paint);
					paint.setTextSize((float)(40/sY));
					canvas.drawText("Fuel:", 0, (float)((height)-(40/sY)), paint);
					if (!gameover) {
						// Allow the player to move right if theres fuel.
						if (yAxis > 2 && yAxis < 8 && !fuelFinished) {
							x = x + (MOVEMENT/sX);
							y = y - 1;
							t = 1.1;
							canvas.drawBitmap(sLeft, x - (65/sX), y + (28/sY), paint);
							fuel -= fuelUsage;
							mp.start();
						} 
						// Allow the player to move left if theres fuel.
						else if (yAxis < -2 && yAxis > -8 && !fuelFinished) {
							x = x - (MOVEMENT/sX);
							y = y - (1/sY);
							t = 1.1;
							canvas.drawBitmap(sRight, x + (35/sX), y + (28/sY), paint);
							fuel -= fuelUsage;
							mp.start(); 
						} 
						// Allow the player to move upward if theres fuel.
						else if (xAxis < 8 && xAxis > 2 && !fuelFinished) {
							y = y - (4/sY);
							t = 0.8;
							canvas.drawBitmap(sMain, x - (25/sX), y + (35/sY), paint);
							fuel -= (fuelUsage + 1);
							mp.start();
						} 
						// Make the player space ship falls due to gravity.
						else {
							y = (int) y + (int)(t + (0.5 * ((GRAVITY/sY) * t * t)));
							t = t + 0.01;
							mp.pause();
						}
					}
					// If ship hit the side
					if (x < 0) {
						x = width;
					}
					if (y < (float)(30/sY)) {
						y = (float)(30/sY);
					}
					if (x > width) {
						x = 0;
					}
					if (y > height) {
						y = height;
					}
				}
				if (fuel < 0) {
					fuelFinished = true;
				}
				canvas.drawBitmap(landing, xcorland.get(0), ycorland.get(0), paint);
				// Check if the player landed safely
				if (contains(xcorland, ycorland, x - (60/sX), y + (30/sY)) || contains(xcorland, ycorland, x + (60/sX), y + (30/sY))) {
					gameover = true;
					mp.pause();
					canvas.drawBitmap(sMessage2,  (float)((width/2)-(960/sX)), (float)((height/2)-(540/sX)), paint);
				}
				// Check if the player interact with the Terrain
				if (contains(xcor, ycor, x - (55/sX), y + (25/sY)) || contains(xcor, ycor, x + (55/sX), y + (25/sY)) || contains(xcor, ycor, x, y + (25/sY)) 
						|| contains(xcor, ycor, x - (28/sX), y + (25/sY))|| contains(xcor, ycor, x + (28/sX), y + (25/sY))) {
					long current = android.os.SystemClock.uptimeMillis();
					if (animStart == 0) 
					{
						sP.play(explosion1, 1, 1, 1, 0, 1f);
						animStart = 1;
					}
					int gifDuration = explodeGif.duration();
					if (gifDuration == 0)
					{
						gifDuration = 1000;
					}
					
					int time = (int) ((current - animStart) % gifDuration);
					explodeGif.setTime(time);
					
					Bitmap animBitmap = Bitmap.createBitmap((int) (80 / sX), (int) (80 / sY), Bitmap.Config.ARGB_8888);
					canvas.drawBitmap(shipCrash, x - (60/sX), y - (30/sY), paint);
					Canvas animCanvas = new Canvas(animBitmap);
					animCanvas.scale(width/1920f, height/1080f);
					explodeGif.draw(animCanvas, 0, 0, paint);
					canvas.drawBitmap(animBitmap,x - (60/sX),y - (60/sY),paint);
					canvas.drawBitmap(animBitmap,x + (5/sX),y - (50/sY),paint);
					gameover = true;
					mp.pause();
					canvas.drawBitmap(sMessage,  (float)((width/2)-(960/sX)), (float)((height/2)-(540/sX)), paint);
				}
				else
				{
					canvas.drawBitmap(ship, x - (60/sX), y - (30/sY), paint);
				}
				if(atMenu)
				{
					mp.pause();
					gameover = true;
					canvas.drawBitmap(sMenu, 0, 0, paint);
				}

				try {
					Thread.sleep(REFRESH_RATE);
				} catch (Exception e) {
				}

				holder.unlockCanvasAndPost(canvas);
			}
		}

	}

	/**
	 * Checks if the given path contains the x and y of the space ship
	 * @param xcor,ycor,x0,y0
	 * @return even or odd
	 */
	public boolean contains(ArrayList<Integer> xcor, ArrayList<Integer> ycor, double x0, double y0) {
		int crossings = 0;
		for (int i = 0; i < xcor.size() - 1; i++) {
			int x1 = xcor.get(i);
			int x2 = xcor.get(i + 1);
			int y1 = ycor.get(i);
			int y2 = ycor.get(i + 1);
			int dy = y2 - y1;
			int dx = x2 - x1;
			double slope = 0;
			if (dx != 0) {
				slope = (double) dy / dx;
			}
			boolean cond1 = (x1 <= x0) && (x0 < x2);
			boolean cond2 = (x2 <= x0) && (x0 < x1);
			boolean above = (y0 < slope * (x0 - x1) + y1);
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

	/**
	 * Get the sensor X axis and Y axis
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		xAxis = event.values[0];
		yAxis = event.values[1];
	}

	/**
	 * Reset the game
	 */
	public void reset() {
		t = 0.1;
		fuel = maxFuel;
		fuelFinished = false;
		pause = false;
		animStart = 0;
		gameover = false;
		atMenu = false;
		x = (float)(100/sX);
		y = (float)(40/sY);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	/**
	 * Start new thread
	 * Set Pause to false
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		main = new Thread(this);
		if (main != null)
			main.start();
		pause = false;
	}
	
	/**
	 * Pause the game
	 * Pause the sounds
	 * Unregister the listener for the sensor
	 */
	public void pause()
	{
		pause = true;
		mp.pause();
		newSensor.unregisterListener(this);
	}
	
	/**
	 * Resume Game, sounds
	 * Register the listener for the sensor
	 */
	public void resume(){
		mp.start();
		newSensor.registerListener(this, newSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		pause = true;
	}
}
