package com.tanzee.gamefirst;

import java.security.PublicKey;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Dock {

    Bitmap dockBitmap;
    int dockWidth, dockHeight;
    int leftmostPoint, rightmostPoint;
    DrawingThread drawingThread;
    boolean movingLeftFlag = false;
    boolean movingRightFlag = false;

    Point topLeftPoint = new Point(0, 0), bottomCenterPoint;

    public Dock(DrawingThread drawingThread, int bitmapId) {
        super();
        this.drawingThread = drawingThread;
        Bitmap tempBitmap = BitmapFactory.decodeResource(drawingThread.context.getResources(), bitmapId);
        tempBitmap = Bitmap.createScaledBitmap(tempBitmap, drawingThread.displayX, drawingThread.displayX * tempBitmap.getHeight() / tempBitmap.getWidth(), true);

        dockBitmap = tempBitmap;
        dockHeight = dockBitmap.getHeight();
        dockWidth = dockBitmap.getWidth();


        bottomCenterPoint = new Point((int) drawingThread.displayX / 2, (int) drawingThread.displayY);
        topLeftPoint.y = bottomCenterPoint.y - dockHeight;

        updateInfo();

        //stratMovingLeft();
        //stratMovingRight();
    }

    public void startMovingRight() {
        movingRightFlag = true;
        Thread righThread = new Thread() {
            @Override
            public void run() {
                //movingRightFlag=true;
                while (movingRightFlag) {
                    moveDocktoRight();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        //System.out.println("startMovingRight");
                    }
                }


            }


        };
        righThread.start();

    }

    public void stopMovingRight() {
        movingRightFlag = false;

    }

    public void startMovingLeft() {
        movingLeftFlag = true;
        Thread lefThread = new Thread() {
            @Override
            public void run() {
                //movingLeftFlag=true;
                while (movingLeftFlag) {
                    moveDocktoLeft();
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        //System.out.println("startMovingLeft");
                    }
                }


            }


        };
        lefThread.start();

    }

    public void stopMovingLeft() {
        movingLeftFlag = false;

    }


    private void updateInfo() {
        leftmostPoint = bottomCenterPoint.x - dockWidth / 2;
        rightmostPoint = bottomCenterPoint.x + dockWidth / 2;

        topLeftPoint.x = leftmostPoint;
    }

    public void moveDocktoLeft() {
        bottomCenterPoint.x -= 4;
        updateInfo();
    }

    public void moveDocktoRight() {
        bottomCenterPoint.x += 4;
        updateInfo();
    }


}
