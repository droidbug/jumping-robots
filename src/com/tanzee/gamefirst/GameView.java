package com.tanzee.gamefirst;

import java.util.Random;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.VelocityTracker;

public class GameView extends SurfaceView implements Callback {
    Context context;
    SurfaceHolder surfaceHolder;
    DrawingThread drawingThread;
    VelocityTracker velocityTracker;


    public GameView(Context context) {
        super(context);
        this.context = context;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        drawingThread = new DrawingThread(this, context);
        //initialize velocity tracker
        velocityTracker = VelocityTracker.obtain();

    }


    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        drawingThread = new DrawingThread(this, context);
        //initialize velocity tracker
        velocityTracker = VelocityTracker.obtain();
    }


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        drawingThread = new DrawingThread(this, context);
        //initialize velocity tracker
        velocityTracker = VelocityTracker.obtain();
    }


    //------------------implemented classes of Callback interface of SurfaceHolder--------------------
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            drawingThread.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            restartThread();
        }
    }

    private void restartThread() {
        drawingThread.stopThread();
        drawingThread = null;
        drawingThread = new DrawingThread(this, context);
        drawingThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        drawingThread.stopThread();
    }


    //we have to override a ontouch method as everything on this surface will take place through touch
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (drawingThread.pauseFlag == true) {
            return true;
        }

        Point touchPoint = new Point((int) event.getX(), (int) event.getY());//we need a touchpoint where we want to draw the robot
        Random random = new Random();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawingThread.touchedFlag = true;
                drawingThread.allRobots.add(new Robot(drawingThread.allPossibleRobots.get(random.nextInt(13)), touchPoint));
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(30);
                drawingThread.allRobots.get(drawingThread.allRobots.size() - 1).setVelocity(velocityTracker);//tracked the velocity of last robot
                drawingThread.touchedFlag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //when we will move finger,then velocity is needed to be tracked
                velocityTracker.addMovement(event);//this will end when we will take up finger,means ACTION_UP
                drawingThread.allRobots.get(drawingThread.allRobots.size() - 1).setCenter(touchPoint);
                break;

            default:
                break;
        }
        return true;
    }


}
