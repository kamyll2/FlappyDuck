package com.wygralak.flappyduck.Engine;

import android.graphics.Paint;
import android.graphics.RectF;

import com.wygralak.flappyduck.ColissionUtils.ICollisionInterpreter;

/**
 * Created by robertogiba on 20.10.2017.
 */

public interface IDuckWall extends ICollisionInterpreter{
    void updateSize(int pitchWidth, int pitchHeight);
    void updatePosition(double ratio);
    void forceResetPosition();
    void setPositionValidator(WallPositionValidator positionValidator);

    Paint getCurrentPaint();
    ICollisionInterpreter getEmptySpaceCollisionable();
    RectF getTopRect();
    RectF getBottomRect();
}
