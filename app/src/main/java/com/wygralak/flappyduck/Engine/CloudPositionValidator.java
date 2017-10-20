package com.wygralak.flappyduck.Engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by robertogiba on 20.10.2017.
 */

public class CloudPositionValidator {
    private List<Cloud> clouds = new ArrayList<>();
    private float minCloudSpace = DuckWall.defaultGoalSize * 2;

    private final Comparator<Cloud> rightCloudComparator = new RightCloudComparator();

    public void addCloud(Cloud cloud) {
        clouds.add(cloud);
    }

    public void addClouds(Collection<Cloud> clouds) {
        this.clouds.addAll(clouds);
    }

    public float getMinCloudSpace() {
        return minCloudSpace;
    }

    void updateMinCloudSpace(int width, int height) {
        minCloudSpace = Cloud.defaultGoalSize;
    }

    float getFarRight() {
        return Collections.max(clouds, rightCloudComparator).getTopRect().right;
    }

    private class RightCloudComparator implements Comparator<Cloud> {
        @Override
        public int compare(Cloud cloud, Cloud t1) {
            return Float.compare(cloud.getTopRect().right, t1.getTopRect().right);
        }
    }
}
