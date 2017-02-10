package com.example.zac.project1;

/**
 * Created by Zac on 2017-01-18.
 */

public class Line {

    public LinePoint start, end;
    public Line normal, PQ, XP, pqVect, PX, PQPrimeVect;
    float d;



    public Line() {}

    public Line(float startX, float startY, float endX, float endY) {
        this.start = new LinePoint(startX, startY);
        this.end = new LinePoint(endX, endY);
    }


    // Vector Constructor.
    public Line(float startX, float startY) {
        this.start = new LinePoint(startX, startY);
    }


    public LinePoint getStart() {
        return start;
    }



    public LinePoint getEnd() {
        return end;
    }



    /**
     * PROJECTION GET D DISTANCE CODE IN METHODS BELOW.
     *
     *
     *
     */


    //Use our XP to inverse and get PX for Fractional Length Calc.
    public Line findPQVector(Line pq) {
        Line PQVect;
        PQVect = new Line(pq.getEnd().getX() - pq.getStart().getX(), pq.getEnd().getY() - pq.getStart().getY());
        return PQVect;
    }


    //Pythagoras of line to get the length.
    public float getLength(LinePoint s, LinePoint e) {
        start = s;
        end = e;
        float horizontal = Math.abs(e.getX() - s.getX());
        float vertical = Math.abs(e.getY() - s.getY());
        return (float) Math.sqrt( (horizontal * horizontal) + (vertical * vertical));
    }



    /**
     * Sets/Gets the normal of a vector.
     */
    public Line findNormalVect(Line pq) {
        float flippedY = pq.getStart().getY() * -1;
        normal = new Line(flippedY, pq.getStart().getX());
        return normal;
    }




    //Find the line from PQ start to X point which we calculate distance and normal from.
    public Line findXP(LinePoint pq, LinePoint x) {
        XP = new Line(pq.getX() - x.getX(), pq.getY() - x.getY());
        return XP;
    }



    //Project B onto A.  RETURNS THE DISTANCE D.
    public float projXPtoN(float dotProd, Line XP, float normLength) {
        d = dotProd / normLength;
        return d;
    }



    //Calculate dot product.
    public float dotProd(Line a, Line b) {
        return ((a.getStart().getX() * b.getStart().getX()) + (a.getStart().getY() * b.getStart().getY()));
    }




    /**
     * PROJECTION GET FRACTIONAL LENGTH CODE BELOW.
     *
     *
     *
     */

    //Use our XP to inverse and get PX for Fractional Length Calc.
    public Line findPX() {
        PX = new Line(XP.getStart().getX() * -1, XP.getStart().getY() * -1 );
        return PX;
    }



    //Calculates the fractional percentage.  Passes in the calculated fractional length from Proj function and the PQ "Vector"(Line)
    public float fraction(float frLength, float pqLength) {
        return frLength / pqLength;
    }


    //Use our Gets PQ Prime vector.
    public Line findPQPrime(Line pqLine) {
        PQPrimeVect = new Line(pqLine.getEnd().getX() - pqLine.getStart().getX(), pqLine.getEnd().getY() - pqLine.getStart().getY());
        return PQPrimeVect;
    }



    //Using our fractionl percentage and PQ prime vector, this function calculates and returns the X Prime point we are looking for.
    public LinePoint findXPrime(Line pPrime, Line pqPrime, Line normalPrime, float fractionPercent, float dist, float normalLen ) {

        //calculate x's
        float x = pPrime.getStart().getX() + (fractionPercent * pqPrime.getStart().getX()) - (dist *(normalPrime.getStart().getX()/normalLen));
        float y = pPrime.getStart().getY() + (fractionPercent * pqPrime.getStart().getY()) - (dist *(normalPrime.getStart().getY()/normalLen));

        return new LinePoint (x, y);
    }



}
