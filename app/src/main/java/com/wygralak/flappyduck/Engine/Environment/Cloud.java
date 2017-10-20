package com.wygralak.flappyduck.Engine.Environment;

import android.graphics.RectF;

import com.wygralak.flappyduck.Engine.Utils.BaseNode;
import com.wygralak.flappyduck.Vector2;

/**
 * Created by robertogiba on 20.10.2017.
 */

public class Cloud extends BaseNode implements ICloud {
    public static final float BASE_CLOUD_THICKNESS = 400f;

    public static final float DEFAULT_SPEED = 8f;
    protected static float defaultGoalSize;
    protected float speed = DEFAULT_SPEED;

    private RectF topRect = new RectF();

    private CloudPositionValidator positionValidator;

    public Cloud() {
        currentVector = new Vector2(-0.1f, 0.0f).normalize();
    }

    @Override
    public void updateSize(int pitchWidth, int pitchHeight) {
        this.pitchWidth = pitchWidth;
        this.pitchHeight = pitchHeight;
        defaultGoalSize = pitchHeight / 3f;
        positionValidator.updateMinCloudSpace(pitchWidth, pitchHeight);
        getTopRect().right = pitchWidth / 2;
        generateNewPosition();
    }

    @Override
    public void updatePosition(double ratio) {
        float speedWithRatio = speed * (float) ratio;
        float diffX = speedWithRatio * currentVector.x;
        float diffY = speedWithRatio * currentVector.y;

        updateRect(topRect, diffX, diffY);

        if (topRect.right < 0) {
            generateNewPosition();
        }
    }

    private void generateNewPosition() {
        float newXPosition = generateNewXPosition();
        float newYPostion = generateNewYPosition();

        topRect.set(newXPosition,
                newYPostion,
                newXPosition + BASE_CLOUD_THICKNESS,
                newYPostion + BASE_CLOUD_THICKNESS);
    }

    private float generateNewXPosition() {
        float random = (float) Math.random();
        return (1.5f * BASE_CLOUD_THICKNESS) * random + positionValidator.getFarRight() + positionValidator.getMinCloudSpace();
    }

    private float generateNewYPosition() {
        float random = (float) Math.random();
        return (pitchHeight - defaultGoalSize) * random;
    }

    private void updateRect(RectF rect, float diffX, float diffY) {
        float nextX = rect.left + diffX;
        float nextY = rect.top + diffY;
        rect.set(nextX, nextY, nextX + rect.width(), nextY + rect.height());
    }

    @Override
    public RectF getTopRect() {
        return topRect;
    }

    @Override
    public void setPositionValidator(CloudPositionValidator positionValidator) {
        this.positionValidator = positionValidator;
    }

    @Override
    public void forceResetPosition() {
        generateNewPosition();
    }
}
