package com.tanzee.gamefirst;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DrawingThread extends Thread {

    private Canvas canvas;
    GameView gameView;
    Context context;

    boolean threadFlag = false;
    boolean touchedFlag = false;
    boolean pauseFlag = false;

    Bitmap backgroundBitmap;
    int displayX, displayY;
    int maxScore = 0;
    Paint scorePaint;

    ArrayList<Robot> allRobots;//go to updateDisplay and make rbots from this array using for loop
    ArrayList<Bitmap> allPossibleRobots;
    AnimationThread animationThread;

    Dock dock;
    ScoreCounter scoreCounter;

    public DrawingThread(GameView gameView, Context context) {
        super();
        this.gameView = gameView;
        this.context = context;
        initializeAll();


    }

    private void initializeAll() {
        //initialize the screen size according to phone_screen_size so that content size differentiate proporshonaly
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();    // this will return us the display of the phone
        //we want to take the measurement of the phone display using "Point".
        Point displayDimension = new Point();
        defaultDisplay.getSize(displayDimension);                    // it will get the x-y cordinate of the phone display,and now
        displayX = displayDimension.x;                                //we will take some int variables for x-y coordinates
        displayY = displayDimension.y;

        //now setting the background with a bitmap image decoding the resources of this context
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd7);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);//next we want to draw this bitmap in the display using updateDisplay()

        initializeAllPossibleRobots();
        scoreCounter = new ScoreCounter(this);
        dock = new Dock(this, R.drawable.dock);
        scorePaint = new Paint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextAlign(Align.CENTER);
        scorePaint.setTextSize(displayX / 10);
    }


    private void initializeAllPossibleRobots() {
        allRobots = new ArrayList<Robot>();
        allPossibleRobots = new ArrayList<Bitmap>();
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo1));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo2));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo3));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo4));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo5));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo6));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo7));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo8));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo9));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo10));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo11));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo12));
        allPossibleRobots.add(giveResizedRobotBitmap(R.drawable.robo13));

    }


    private Bitmap giveResizedRobotBitmap(int resourceID) {
        Bitmap tempBitmap = BitmapFactory.decodeResource(context.getResources(), resourceID);
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, displayX / 5, (tempBitmap.getHeight()) / (tempBitmap.getWidth()) * (displayX / 5), true);

        return tempBitmap;
    }

    @Override
    public void run() {
        threadFlag = true;
        animationThread = new AnimationThread(this);

        animationThread.start();
        scoreCounter.start();

        while (threadFlag) {
            canvas = gameView.surfaceHolder.lockCanvas();
            try {
                synchronized (gameView.surfaceHolder) {
                    updateDisplay();

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    gameView.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }//while loop ends
        scoreCounter.stopThread();
        animationThread.stopThread();
    }//run method ends here

    private void updateDisplay() {
        // TODO Auto-generated method stub
        //what to update and what to draw??? go to the initializeApp() method and
        //initialize things like backgroud of surface,robots u want to draw

        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        drawDock();

        for (int i = 0; i < allRobots.size(); i++) {
            Robot tempRobot = allRobots.get(i);
            canvas.drawBitmap(tempRobot.robotBitmap, tempRobot.centerX - (tempRobot.width / 2), tempRobot.centerY - (tempRobot.height), tempRobot.robotPaint);

        }
        if (pauseFlag) {
            pauseStateDraw();
        }
        //we want to see if the gravity changes because of sensor change using this method
        //drawSensorData();
        drawScore(context);
    }

    private void pauseStateDraw() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setAlpha(150);
        paint.setTextAlign(Align.CENTER);

        canvas.drawARGB(170, 0, 0, 0);
        canvas.drawText("PAUSED", displayX / 2, displayY / 2, paint);
    }

    private void drawSensorData() {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(displayX / 10);
        canvas.drawText("X axis : " + GameActivity.getgX(), 0, displayY / 3, paint);
        canvas.drawText("Y axis : " + GameActivity.getgX(), 0, (displayY / 3) + (displayX / 5), paint);

    }

    private void drawDock() {
        canvas.drawBitmap(dock.dockBitmap, dock.topLeftPoint.x, dock.topLeftPoint.y, null);
    }

    private void drawScore(Context context) {
        if (maxScore > 1000) {
            scorePaint.setColor(Color.GRAY);
            /*backgroundBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd6);
			backgroundBitmap=Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);*/


            if (maxScore > 10000) {
                scorePaint.setColor(Color.RED);
				/*backgroundBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd5);
				backgroundBitmap=Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);*/

                if (maxScore > 100000) {
                    scorePaint.setColor(Color.MAGENTA);
					/*backgroundBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd4);
					backgroundBitmap=Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);*/

                    if (maxScore > 1000000) {
                        scorePaint.setColor(Color.CYAN);
						/*backgroundBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd3);
						backgroundBitmap=Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);*/


                        if (maxScore > 10000000) {
                            scorePaint.setColor(Color.BLUE);
							/*backgroundBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.backgrnd2);
							backgroundBitmap=Bitmap.createScaledBitmap(backgroundBitmap, displayX, displayY, true);*/

                        }
                    }
                }
            }
        }
        canvas.drawText("Score : " + maxScore, displayX / 2, displayY / 7, scorePaint);
    }


    public void stopThread() {

        threadFlag = false;
    }


}