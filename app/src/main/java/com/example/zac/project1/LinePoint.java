package com.example.zac.project1;

import java.util.ArrayList;

/**
 * Created by Zac on 2017-01-18.
 * This class is essentially the same as Java's Point class but uses floats to make things easier.
 */

public class LinePoint {
    public float x, y;

    public LinePoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x1) {
        x = x1;
    }

    public float getX() {
        return this.x;
    }

    public void setY(float y1) {
        y = y1;
    }

    public float getY() {
        return this.y;
    }
}
