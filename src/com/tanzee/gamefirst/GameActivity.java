package com.tanzee.gamefirst;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class GameActivity extends Activity {

    private static float gX, gY;//Gravity coordinates x and y

    GameView gameView;
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;//to initialize these sensors create method initializeSensors()->onCreate
    Sensor accelerometerSensor;//initialize this into intializeSensors()


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //will add a flag so that the screen dose not get locked if not touched
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//to set the game always in portrait mode :)

        initializeSensors();

        //setting our own created view in the display
        //gameView=new GameView(this);
        setContentView(R.layout.game_sceen);
        gameView = (GameView) findViewById(R.id.myGameView);
        initializeButtons();
    }


    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorEventListener = new SensorEventListener() {


            //when any type of sensor we will use,this method should be used to tell sensor-type and respond to changes
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    gX = -event.values[0];
                    gY = event.values[1];
                    if (gY < 0) {
                        stopUsingSensors();
                        gameView.drawingThread.animationThread.stopThread();
                        gameView.drawingThread.scoreCounter.stopThread();
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GameActivity.this);
                        alertBuilder.setTitle("No Cheating!!!!");
                        alertBuilder.setIcon(R.drawable.block);
                        alertBuilder.setMessage("Yor are Shaking or Holing your phone upside down!!!!");
                        alertBuilder.setPositiveButton("Restart", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                restartGame(null);

                            }
                        });
                        alertBuilder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopGame(null);
                            }
                        });
                        alertBuilder.show();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub

            }
        };

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //after initializing the sensors let the application use these sensors
        startUsingSensors();
    }

    private void startUsingSensors() {
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, sensorManager.SENSOR_DELAY_NORMAL);

    }

    private void stopUsingSensors() {
        sensorManager.unregisterListener(sensorEventListener);
    }


    public void initializeButtons() {

        Button moveLeftButton = (Button) findViewById(R.id.leftButton);

        moveLeftButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gameView.drawingThread.dock.startMovingLeft();
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.drawingThread.dock.stopMovingLeft();
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
        Button moveRightButton = (Button) findViewById(R.id.rightButton);

        moveRightButton.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        gameView.drawingThread.dock.startMovingRight();
                        break;
                    case MotionEvent.ACTION_UP:
                        gameView.drawingThread.dock.stopMovingRight();
                        break;
                    default:
                        break;
                }

                return false;
            }
        });


    }

    @Override
    protected void onPause() {
        stopUsingSensors();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startUsingSensors();

        super.onResume();
    }

    @Override
    protected void onStop() {

        stopUsingSensors();
        super.onStop();
    }

    //getters and setters of gravity x y values
    public static float getgX() {
        return gX;
    }

    public static void setgX(float gX) {
        GameActivity.gX = gX;
    }

    public static float getgY() {
        return gY;
    }

    public static void setgY(float gY) {
        GameActivity.gY = gY;
    }


    public void pauseGame(View view) {
        if (gameView.drawingThread.pauseFlag == false) {
            gameView.drawingThread.animationThread.stopThread();
            gameView.drawingThread.pauseFlag = true;
            view.setBackgroundResource(R.drawable.lock_open);
        } else {

            gameView.drawingThread.animationThread = new AnimationThread(gameView.drawingThread);
            gameView.drawingThread.animationThread.start();
            view.setBackgroundResource(R.drawable.lock);
            gameView.drawingThread.pauseFlag = false;


        }

    }//onpause ends here

    public void restartGame(View view) {
        //gameView.drawingThread.allRobots.clear();
        stopUsingSensors();
        startUsingSensors();

        gameView.drawingThread.stopThread();
        gameView.drawingThread = new DrawingThread(gameView, this);
        gameView.drawingThread.start();

        Toast.makeText(getBaseContext(), "Game ReStarted!", Toast.LENGTH_SHORT).show();

    }

    public void stopGame(View view) {
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

}
