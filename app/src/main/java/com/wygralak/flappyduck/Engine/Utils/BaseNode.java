package com.wygralak.flappyduck.Engine.Utils;

import android.graphics.Paint;

import com.wygralak.flappyduck.Vector2;

/**
 * Created by robertogiba on 20.10.2017.
 */

public abstract class BaseNode {
    protected float currentX;
    protected float currentY;

    protected float pitchWidth;
    protected float pitchHeight;

    protected Vector2 currentVector;
    protected Paint currentPaint;

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public float getPitchWidth() {
        return pitchWidth;
    }

    public void setPitchWidth(float pitchWidth) {
        this.pitchWidth = pitchWidth;
    }

    public float getPitchHeight() {
        return pitchHeight;
    }

    public void setPitchHeight(float pitchHeight) {
        this.pitchHeight = pitchHeight;
    }

    public Vector2 getCurrentVector() {
        return currentVector;
    }

    public void setCurrentVector(Vector2 currentVector) {
        this.currentVector = currentVector;
    }

    public Paint getCurrentPaint() {
        return currentPaint;
    }

    public void setCurrentPaint(Paint currentPaint) {
        this.currentPaint = currentPaint;
    }
}
