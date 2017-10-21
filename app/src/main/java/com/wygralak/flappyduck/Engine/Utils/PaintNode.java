package com.wygralak.flappyduck.Engine.Utils;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by robertogiba on 22.10.2017.
 */

public class PaintNode extends BaseNode {
    protected Paint currentPaint;

    public PaintNode() {
        this(Color.BLACK);
    }

    public PaintNode(int color) {
        currentPaint = new Paint();
        currentPaint.setColor(color);
    }

    public Paint getCurrentPaint() {
        return currentPaint;
    }

    public void setCurrentPaint(Paint currentPaint) {
        this.currentPaint = currentPaint;
    }
}
