package com.example.zac.project1;

/**
 * Created by Zac on 2017-01-18.
 */

public class Line {

    public LinePoint start, end;

    public Line(float startX, float startY, float endX, float endY) {
        this.start = new LinePoint(startX, startY);
        this.end = new LinePoint(endX, endY);
    }

    public LinePoint getStart() {
        return start;
    }

    public LinePoint getEnd() {
        return end;
    }
}
