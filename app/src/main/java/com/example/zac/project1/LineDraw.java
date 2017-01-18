package com.example.zac.project1;

/**
 * Created by Zac on 2017-01-18.
 */

public class LineDraw {

    public LinePoint start, end;

    public LineDraw(double x, double y) {
        this.start = new LinePoint(x, y);
        this.end = new LinePoint(x, y);
    }
}
