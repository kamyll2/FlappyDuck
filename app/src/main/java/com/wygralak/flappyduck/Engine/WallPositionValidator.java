package com.wygralak.flappyduck.Engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wygralak on 2017-10-07.
 */

public class WallPositionValidator {
    private List<DuckWall> walls = new ArrayList<>();
    private float minWallSpace = DuckWall.defaultGoalSize * 2;

    private final Comparator<DuckWall> rightWallComparator = new RightWallComparator();

    public void addWall(DuckWall wall) {
        walls.add(wall);
    }

    public void addWalls(Collection<DuckWall> walls) {
        this.walls.addAll(walls);
    }

    boolean isNewPositionValid(DuckWall wall) {
        for (DuckWall myWall : walls) {
            if (wall != myWall) {
                if (wall.getTopRect().intersect(myWall.getTopRect()) ||
                        wall.getTopRect().left - myWall.getTopRect().right < minWallSpace ||
                        myWall.getTopRect().left - wall.getTopRect().right < minWallSpace) {
                    return false;
                }
            }
        }
        return true;
    }

    public float getMinWallSpace() {
        return minWallSpace;
    }

    void updateMinWallSpace(int width, int height) {
        minWallSpace = DuckWall.defaultGoalSize;
    }

    float getFarRight() {
        return Collections.max(walls, rightWallComparator).getTopRect().right;
    }

    private class RightWallComparator implements Comparator<DuckWall> {
        @Override
        public int compare(DuckWall wall, DuckWall t1) {
            return Float.compare(wall.getTopRect().right, t1.getTopRect().right);
        }
    }
}
