package com.wygralak.flappyduck.ColissionUtils;

import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-03-10.
 */
public interface ICollisionInvoker {
    void updateVector(Vector2 angle);
    Vector2 getCurrentVector();

    void updatePositionDirectly(float newX, float newY);
    void updatePosition(double ratio);
    float getCurrentPositionX();
    float getCurrentPositionY();

    float getCurrentSpeed();
    void setSpeedDirectly(float speed);
    void updateSpeedWithRatio(float ratio);
}
