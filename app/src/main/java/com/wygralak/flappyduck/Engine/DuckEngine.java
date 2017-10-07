package com.wygralak.flappyduck.Engine;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInterpreter;
import com.wygralak.flappyduck.ColissionUtils.ICollisionInvoker;
import com.wygralak.flappyduck.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2016-03-10.
 */
public class DuckEngine implements ICollisionInvoker {
    public static final float DUCK_RADIUS = 20f;
    public static final float DEFAULT_SPEED = 6f;
    private static final float GRAVITY_STRANGTH = 0.05f;

    protected List<ICollisionInterpreter> collisionables;

    protected float currentX;
    protected float currentY;
    private float pitchWidth;
    private float pitchHeight;
    protected float speed = DEFAULT_SPEED;

    private Vector2 currentVector;

    public DuckEngine() {
        collisionables = new ArrayList<>();
        currentVector = new Vector2(0.0f, 0.1f).normalize();
    }

    public void setupDefaultPosition(float pitchWidth, float pitchHeight) {
        this.pitchWidth = pitchWidth;
        this.pitchHeight = pitchHeight;
        currentX = pitchWidth / 2f;
        currentY = pitchHeight / 2f;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void addColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.add(collisionInterpreter);
    }

    public void addColissionables(List<ICollisionInterpreter> list) {
        collisionables.addAll(list);
    }

    public void removeColissionable(ICollisionInterpreter collisionInterpreter) {
        collisionables.remove(collisionInterpreter);
    }

    public void setDefaultSpeed() {
        speed = DEFAULT_SPEED;
    }

    @Override
    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        currentX = currentX + speedWithRatio * currentVector.x;
        currentY = currentY + speedWithRatio * currentVector.y;
        currentVector.y += (ratio * GRAVITY_STRANGTH); //falling down
        currentVector.normalize();
    }

    @Override
    public float getCurrentPositionX() {
        return currentX;
    }

    @Override
    public float getCurrentPositionY() {
        return currentY;
    }

    @Override
    public float getCurrentSpeed() {
        return speed;
    }

    @Override
    public void setSpeedDirectly(float speed) {
        this.speed = speed;
    }

    public boolean checkForCollisions() {
        for (int i = 0; i < collisionables.size(); i++) {
            if (collisionables.get(i).checkForCollision(this, currentVector, currentX, currentY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateVector(Vector2 angle) {
        currentVector = angle;
        //mainActivity.setStatusText(currentVector.toString());
    }

    @Override
    public Vector2 getCurrentVector() {
        return currentVector;
    }

    @Override
    public void updatePositionDirectly(float newX, float newY) {
        currentY = newY;
        currentX = newX;
    }

    @Override
    public void updateSpeedWithRatio(float ratio) {
        speed = speed * ratio;
    }
}