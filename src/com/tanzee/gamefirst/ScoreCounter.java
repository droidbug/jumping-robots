package com.tanzee.gamefirst;

public class ScoreCounter extends Thread {

    float maximumScore;
    DrawingThread drawingThread;
    boolean threadrunningFlag = false;


    public ScoreCounter(DrawingThread drawingThread) {
        super();
        this.drawingThread = drawingThread;
    }


    @Override
    public void run() {
        threadrunningFlag = true;
        while (threadrunningFlag) {
            float tempMaxScore = 0;
            for (Robot robot : drawingThread.allRobots) {
                if (robot.centerY < tempMaxScore) {
                    tempMaxScore += robot.centerY;
                }
                drawingThread.maxScore = (int) (drawingThread.maxScore > (-tempMaxScore) ? drawingThread.maxScore : (-tempMaxScore));

            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void stopThread() {
        threadrunningFlag = false;

    }


}
