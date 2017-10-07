package com.wygralak.flappyduck.Engine;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInvoker;
import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class DuckWall extends BasePitchWall {

    public static final float defaultSpeed = 6f;
    protected float currentX;
    protected float currentY;
    protected float speed = defaultSpeed;

    private Vector2 currentVector;
    private int pithWidth;

    public DuckWall() {
        currentPaint = new Paint();
        currentPaint.setColor(Color.CYAN);
        currentVector = new Vector2(-0.1f, 0.0f).normalize();
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        this.pithWidth = pitchWidth;
        defaultGoalSize = pitchHeight / 2f;
        super.set((float) pitchWidth - BASE_WALL_THICKNESS,
                BASE_WALL_THICKNESS,
                (float) pitchWidth,
                ((float) pitchHeight / 2f) - defaultGoalSize / 2f);
        currentX = left;
        currentY = top;
    }

    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        currentX = currentX + speedWithRatio * currentVector.x;
        currentY = currentY + speedWithRatio * currentVector.y;
        super.set(currentX, currentY, currentX + width(), currentY + height());
        if (left < 0) {
            currentX = pithWidth;
            super.set(currentX, currentY, currentX + width(), currentY + height());
        }
    }

    @Override
    public boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
        if (isCollision(x, y)) {
            invoker.updateVector(new Vector2(-Math.abs(currentVector.x), currentVector.y));
            //validateBallOutsideWall(invoker);
            invoker.updateSpeedWithRatio(DEFAULT_SPEED_ABSORB);
            return true;
        }
        return false;
    }

    @Override
    protected boolean isCollision(float x, float y) {
        return y - DuckEngine.DUCK_RADIUS < this.bottom &&
                x + DuckEngine.DUCK_RADIUS > this.left &&
                y - DuckEngine.DUCK_RADIUS > this.top &&
                x + DuckEngine.DUCK_RADIUS < this.right;
    }
}
