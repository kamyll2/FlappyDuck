package com.wygralak.flappyduck.Engine;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInterpreter;
import com.wygralak.flappyduck.ColissionUtils.ICollisionInvoker;
import com.wygralak.flappyduck.Engine.Utils.BaseNode;
import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-04-14.
 */
public class DuckWall extends BaseNode implements IDuckWall {
    public static final float BASE_WALL_THICKNESS = 180f;
    public static final float DEFAULT_SPEED_ABSORB = 0.8f;
    public static final float INCREASED_SPEED_ABSORB = 0.5f;
    public static final float DECREASED_SPEED_ABSORB = 1.4f;

    public static final float DEFAULT_SPEED = 6f;
    protected static float defaultGoalSize;
    protected float speed = DEFAULT_SPEED;

    private RectF topRect = new RectF();
    private RectF bottomRect = new RectF();
    private RectF emptySpaceRect = new RectF();
    private EmptySpaceCollisionable emptySpaceCollisionable = new EmptySpaceCollisionable();

    private WallPositionValidator positionValidator;

    public DuckWall() {
        this(Color.GREEN);
    }

    public DuckWall(int color) {
        currentPaint = new Paint();
        currentPaint.setColor(color);
        currentVector = new Vector2(-0.1f, 0.0f).normalize();
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        this.pitchWidth = pitchWidth;
        this.pitchHeight = pitchHeight;
        defaultGoalSize = pitchHeight / 3f;
        positionValidator.updateMinWallSpace(pitchWidth, pitchHeight);
        getTopRect().right = pitchWidth / 2;
        generateNewPosition();
    }

    @Override
    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        float diffX = speedWithRatio * currentVector.x;
        float diffY = speedWithRatio * currentVector.y;
        updateRect(topRect, diffX, diffY);
        updateRect(bottomRect, diffX, diffY);
        updateRect(emptySpaceRect, diffX, diffY);

        if (topRect.right < 0) {
            generateNewPosition();
        }
    }

    private void generateNewPosition() {
        float newXPosition = generateNewXPosition();
        float newEmptySpaceY = generateNewEmptySpaceY();
        topRect.set(newXPosition,
                0,
                newXPosition + BASE_WALL_THICKNESS,
                newEmptySpaceY);
        emptySpaceRect.set(newXPosition,
                newEmptySpaceY,
                newXPosition + BASE_WALL_THICKNESS,
                newEmptySpaceY + defaultGoalSize);
        bottomRect.set(newXPosition,
                newEmptySpaceY + defaultGoalSize,
                newXPosition + BASE_WALL_THICKNESS,
                pitchHeight);
        emptySpaceCollisionable.recycle();
    }

    private float generateNewEmptySpaceY() {
        float random = (float) Math.random();
        return (pitchHeight - defaultGoalSize) * random;
    }

    private float generateNewXPosition() {
        float random = (float) Math.random();
        return (1.5f * BASE_WALL_THICKNESS) * random + positionValidator.getFarRight() + positionValidator.getMinWallSpace();
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        float nextX = rect.left + diffX;
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
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
    public Paint getCurrentPaint() {
        return currentPaint;
    }

    protected boolean isCollision(float x, float y) {
        return isRectInCollision(topRect, x, y) || isRectInCollision(bottomRect, x, y);
    }

    private boolean isRectInCollision(RectF rect, float x, float y) {
        return y - DuckEngine.DUCK_RADIUS < rect.bottom &&
                x + DuckEngine.DUCK_RADIUS > rect.left &&
                y + DuckEngine.DUCK_RADIUS > rect.top &&
                x - DuckEngine.DUCK_RADIUS < rect.right;
    }

    @Override
    public ICollisionInterpreter getEmptySpaceCollisionable() {
        return emptySpaceCollisionable;
    }

    @Override
    public RectF getTopRect() {
        return topRect;
    }

    @Override
    public RectF getBottomRect() {
        return bottomRect;
    }

    @Override
    public void forceResetPosition() {
        generateNewPosition();
    }

    @Override
    public void setPositionValidator(WallPositionValidator positionValidator) {
        this.positionValidator = positionValidator;
    }

    private class EmptySpaceCollisionable implements ICollisionInterpreter {
        private boolean invokedOncePerRecycle = false;

        @Override
        public boolean checkForCollision(ICollisionInvoker invoker, Vector2 currentVector, float x, float y) {
            if (!invokedOncePerRecycle) {
                if (x - DuckEngine.DUCK_RADIUS > emptySpaceRect.right) {
                    invokedOncePerRecycle = true;
                    return true;
                }
            }
            return false;
        }

        private void recycle() {
            invokedOncePerRecycle = false;
        }
    }
}
