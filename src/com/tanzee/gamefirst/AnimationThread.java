package com.tanzee.gamefirst;

public class AnimationThread extends Thread {

    private boolean flag = false;
    float gravityX, gravityY;
    float timeConstant = 0.3f;  //robot's time respond in sensor-value-change
    float retardationRatio = -0.7f; //decreases velocity


    //to save some space from screen borders of the half of Robots height and width->
    int width, height;
    int left, right, top, bottom; //to update these dimension make a method updateDimensions()


    DrawingThread drawingThread;//so that we can work with robots :)


    public AnimationThread(DrawingThread drawingThread) {
        super();
        this.drawingThread = drawingThread;
        updateDimensions();
    }

    private void updateDimensions() {
        width = drawingThread.allPossibleRobots.get(0).getWidth();
        height = drawingThread.allPossibleRobots.get(0).getHeight();

        left = width / 2;
        top = height;
        right = drawingThread.displayX - (width / 2);
        bottom = drawingThread.displayY;//(height/3);
    }


    @Override
    public void run() {
        flag = true;
        while (flag) {
            updateAllPositions();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private void updateAllPositions() {
        gravityX = GameActivity.getgX();//current position
        gravityY = GameActivity.getgY();//current postion

        if (drawingThread.touchedFlag) {
            for (int i = 0; i < drawingThread.allRobots.size() - 1; i++) {
                updateRobotsPositions(drawingThread.allRobots.get(i), i);
            }
        } else {
            for (int i = 0; i < drawingThread.allRobots.size(); i++) {
                updateRobotsPositions(drawingThread.allRobots.get(i), i);
            }

        }

    }

    private void updateRobotsPositions(Robot robot, int position) {
        robot.centerX += robot.velocityX * timeConstant + (0.5 * gravityX * timeConstant * timeConstant);
        robot.velocityX += gravityX * timeConstant;

        robot.centerY += robot.velocityY * timeConstant + (0.5 * gravityY * timeConstant * timeConstant);
        robot.velocityY += gravityY * timeConstant;

        constrainPosition(robot, position);

    }

    private void constrainPosition(Robot robot, int position) {
        if (robot.centerX < left) {
            robot.centerX = left;
            robot.velocityX *= retardationRatio;
        } else if (robot.centerX > right) {
            robot.centerX = right;
            robot.velocityX *= retardationRatio;

        }
        /*if (robot.centerY<top) {
			robot.centerY=top;
			robot.velocityY*=retardationRatio;
		}else */
        if (robot.centerY > bottom) {
            if (isRobotOutsideDock(robot)) {
                robot.isRobotFellDown = true;
                if (robot.centerY > bottom + height) {
                    drawingThread.allRobots.remove(position);
                }
            }
            if (robot.isRobotFellDown == false) {
                robot.centerY = bottom;
                robot.velocityY *= retardationRatio;
            }

        }
    }

    private boolean isRobotOutsideDock(Robot robot) {
        if (robot.centerX - (width / 2) < drawingThread.dock.leftmostPoint || robot.centerX - (width / 2) > drawingThread.dock.rightmostPoint) {
            return true;
        }

        return false;
    }


    public void stopThread() {
        flag = false;

    }

}
