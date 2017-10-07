package com.wygralak.flappyduck.Engine;

import android.graphics.Color;
import android.graphics.Paint;

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
    public static final float SELF_SLOWING_RATIO = 1.0f;
    public static final Paint BALL_PAINT = new Paint();
    public static final float defaultSpeed = 6f;

    protected List<ICollisionInterpreter> collisionables;

    protected float currentX;
    protected float currentY;
    private float pitchWidth;
    private float pitchHeight;
    protected float speed = defaultSpeed;

    private Vector2 currentVector;


    public DuckEngine() {
        BALL_PAINT.setColor(Color.WHITE);
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
        speed = defaultSpeed;
    }

    @Override
    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        currentX = currentX + speedWithRatio * currentVector.x;
        currentY = currentY + speedWithRatio * currentVector.y;
        currentVector.y += 0.05;
        currentVector.normalize();
    }

    public void considerFriction() {
        speed *= SELF_SLOWING_RATIO;
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
            if (collisionables.get(i).checkForCollisionAndHandle(this, currentVector, currentX, currentY)) {
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
