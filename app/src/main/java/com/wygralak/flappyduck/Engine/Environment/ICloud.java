package com.wygralak.flappyduck.Engine.Environment;

import android.graphics.RectF;

/**
 * Created by robertogiba on 20.10.2017.
 */

public interface ICloud {
    void updateSize(int pitchWidth, int pitchHeight);

    void updatePosition(double ratio);

    void setPositionValidator(CloudPositionValidator positionValidator);

    void forceResetPosition();

    RectF getTopRect();
}
