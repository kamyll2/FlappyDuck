package com.wygralak.flappyduck.ColissionUtils;


import com.wygralak.flappyduck.Vector2;

/**
 * Created by Kamil on 2016-03-10.
 */
public interface ICollisionInterpreter {
    boolean checkForCollisionAndHandle(ICollisionInvoker invoker, Vector2 currentVector, float x, float y);
}
