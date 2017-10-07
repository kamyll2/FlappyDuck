package com.wygralak.flappyduck.Engine;

import android.graphics.Color;
import android.graphics.Paint;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInvoker;
import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class DuckWall extends BasePitchWall {

    public static final float DEFAULT_SPEED = 6f;
    protected float currentX;
    protected float currentY;
    protected float speed = DEFAULT_SPEED;

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
    }

    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        float nextX = left + speedWithRatio * currentVector.x;
        float nextY = top + speedWithRatio * currentVector.y;
        if (nextX < 0) {
            nextX = pithWidth;
        }
        super.set(nextX, nextY, nextX + width(), nextY + height());
    }

    @Override
    public boolean checkForCollision(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
/*        if (isCollision(x, y)) {
            invoker.updateVector(new Vector2(-Math.abs(currentVector.x), currentVector.y));
            //validateBallOutsideWall(invoker);
            invoker.updateSpeedWithRatio(DEFAULT_SPEED_ABSORB);
            return true;
        }
        return false;*/
        return isCollision(x, y);
    }

    @Override
    protected boolean isCollision(float x, float y) {
        return y - DuckEngine.DUCK_RADIUS < this.bottom &&
                x + DuckEngine.DUCK_RADIUS > this.left &&
                y - DuckEngine.DUCK_RADIUS > this.top &&
                x + DuckEngine.DUCK_RADIUS < this.right;
    }
}
