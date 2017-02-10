package com.example.zac.project1;

import android.graphics.Bitmap;

import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;
import static com.example.zac.project1.R.id.imageViewLeft;
import static com.example.zac.project1.R.id.imageViewRight;


/**
 * Created by Zac on 2017-02-06.
 */

public class Warp {
    //Line XP, normal, PX, PQ;
    float d;
    Line line;
    public Bitmap finalLeftBmap, finalRightBmap;





    /**
     *    WARP image left to right.  Source left.
     * @param
     */
    public Bitmap warpLeft(Bitmap left, ArrayList<Line> leftLines, ArrayList<Line> rightLines) {

        line = new Line();
        Bitmap finalLeftBmap = Bitmap.createBitmap(left.getWidth(), left.getHeight(), Bitmap.Config.RGB_565);

        // try {

        for (float x = 0; x < left.getWidth(); x++) {
            for (float y = 0; y < left.getHeight(); y++) {

                double totalWeight = 0;
                float totalDeltaHoriz = 0;
                float totalDeltaVert = 0;
                LinePoint xPrime = new LinePoint(0, 0);

                for (int i = 0; i < (float) leftLines.size(); i++) {


                    //System.out.println("************** SOURCE PROJECTION BELOW  *******************");
                    Line pqVect = line.findPQPrime(rightLines.get(i));    //Makes lineLeft (PQ) a vector with 1 set of points, not 2.
                    Line normal = line.findNormalVect(pqVect);
                    float pqLength = line.getLength(rightLines.get(i).getStart(), rightLines.get(i).getEnd());//
                    Line XPvect = line.findXP(rightLines.get(i).getStart(), new LinePoint(x, y));  //PLUG P COORDS IN HERE  and x point
                    float dotProd = line.dotProd(XPvect, normal);
                    float normLength = pqLength;
                    float d = line.projXPtoN(dotProd, XPvect, normLength);

                    //Right side with primes
                    //System.out.println("************** PRIMES BELOW  *******************");
                    Line PX = line.findPX(); //lineLeft used to be
                    float dotProd2 = line.dotProd(PX, pqVect);
                    float fractL = line.projXPtoN(dotProd2, PX, pqLength);
                    float percent = line.fraction(fractL, pqLength);

                    //Find X Prime
                    //System.out.println("************** X PRIME CALC BELOW  *******************");
                    Line pqPrime = line.findPQPrime(leftLines.get(i));
                    float pqPrimeLength = line.getLength(leftLines.get(i).getStart(), leftLines.get(i).getEnd());
                    Line pPrime = new Line(leftLines.get(i).getStart().getX(), leftLines.get(i).getStart().getY());
                    Line norm2 = line.findNormalVect(pqPrime);
                    float norm2Length = pqPrimeLength;
                    xPrime = line.findXPrime(pPrime, pqPrime, norm2, percent, d, norm2Length);

                    //WEIGHTS
                    double weight = (float)Math.pow(1 / (d+ 0.01), 2);
                    totalWeight += weight;
                    LinePoint deltaX = new LinePoint(xPrime.getX() - x, xPrime.getY() - y);
                    totalDeltaHoriz += deltaX.getX() * weight;
                    totalDeltaVert += deltaX.getY() * weight;


                    //imageViewRight.buildDrawingCache();         //scale?
                    //finalLeftBmap = imageViewRight.getDrawingCache();      //scale?
                    //setUpCanvas(imageViewRight, finalLeftBmap);
                }
                xPrime.setX((float)(x + (totalDeltaHoriz / totalWeight)));
                xPrime.setY((float)(y + (totalDeltaVert / totalWeight)));

                if(xPrime.getX() >= finalLeftBmap.getWidth()) {
                    xPrime.setX(finalLeftBmap.getWidth() - 1);
                }
                if(xPrime.getY() >= finalLeftBmap.getHeight()) {
                    xPrime.setY(finalLeftBmap.getHeight() - 1);
                }
                if(xPrime.getY() < 0) {
                    xPrime.setY(0);
                }
                if(xPrime.getX() < 0) {
                    xPrime.setX(0);
                }

                //DRAW PIXELS HERE
                finalLeftBmap.setPixel((int)x, (int)y, left.getPixel((int) xPrime.getX(), (int) xPrime.getY()));
            }
        }
        //imageViewRight.setImageBitmap(finalLeftBmap);
        //}
        // catch (Exception e){
        //      System.out.println("EXCEPTION OCCURED.");
        // }
        System.out.println("out of loop.");
        return finalLeftBmap;

    }




    /**
     *    WARP image Right to left.  Source right.
     * @param
     */
    public Bitmap warpRight(Bitmap right, ArrayList<Line> leftLines, ArrayList<Line> rightLines) {

        line = new Line();
        Bitmap finalRightBmap = Bitmap.createBitmap(right.getWidth(), right.getHeight(), Bitmap.Config.RGB_565);

        // try {

        for (float x = 0; x < right.getWidth(); x++) {
            for (float y = 0; y < right.getHeight(); y++) {

                double totalWeight = 0;
                float totalDeltaHoriz = 0;
                float totalDeltaVert = 0;
                LinePoint xPrime = new LinePoint(0, 0);

                for (int i = 0; i < (float) rightLines.size(); i++) {


                    // ************** SOURCE PROJECTION BELOW  *******************
                    Line pqVect = line.findPQPrime(leftLines.get(i));    //Makes lineLeft (PQ) a vector with 1 set of points, not 2.
                    Line normal = line.findNormalVect(pqVect);
                    float pqLength = line.getLength(leftLines.get(i).getStart(), leftLines.get(i).getEnd());//
                    Line XPvect = line.findXP(leftLines.get(i).getStart(), new LinePoint(x, y));  //PLUG P COORDS IN HERE  and x point
                    float dotProd = line.dotProd(XPvect, normal);
                    float normLength = pqLength;
                    float d = line.projXPtoN(dotProd, XPvect, normLength);

                    // Right side with primes
                    // ************** PRIMES BELOW  *******************
                    Line PX = line.findPX(); //lineLeft used to be
                    float dotProd2 = line.dotProd(PX, pqVect);
                    float fractL = line.projXPtoN(dotProd2, PX, pqLength);
                    float percent = line.fraction(fractL, pqLength);

                    // Find X Prime
                    // ************** X PRIME CALC BELOW  *******************
                    Line pqPrime = line.findPQPrime(rightLines.get(i));
                    float pqPrimeLength = line.getLength(rightLines.get(i).getStart(), rightLines.get(i).getEnd());
                    Line pPrime = new Line(rightLines.get(i).getStart().getX(), rightLines.get(i).getStart().getY());
                    Line norm2 = line.findNormalVect(pqPrime);
                    float norm2Length = pqPrimeLength;
                    xPrime = line.findXPrime(pPrime, pqPrime, norm2, percent, d, norm2Length);


                    // ****** WEIGHTS *******
                    double weight = (float)Math.pow(1 / (d+ 0.01), 2);
                    totalWeight += weight;
                    LinePoint deltaX = new LinePoint(xPrime.getX() - x, xPrime.getY() - y);
                    totalDeltaHoriz += deltaX.getX() * weight;
                    totalDeltaVert += deltaX.getY() * weight;


                }
                xPrime.setX((float)(x + (totalDeltaHoriz / totalWeight)));
                xPrime.setY((float)(y + (totalDeltaVert / totalWeight)));

                if(xPrime.getX() >= finalRightBmap.getWidth()) {
                    xPrime.setX(finalRightBmap.getWidth() - 1);
                }
                if(xPrime.getY() >= finalRightBmap.getHeight()) {
                    xPrime.setY(finalRightBmap.getHeight() - 1);
                }
                if(xPrime.getY() < 0) {
                    xPrime.setY(0);
                }
                if(xPrime.getX() < 0) {
                    xPrime.setX(0);
                }

                //DRAW PIXELS HERE
                finalRightBmap.setPixel((int)x, (int)y, right.getPixel((int) xPrime.getX(), (int) xPrime.getY()));
            }
        }
       // imageViewLeft.setImageBitmap(finalRightBmap);
        //}
        // catch (Exception e){
        //      System.out.println("EXCEPTION OCCURED.");
        // }
        System.out.println("out of loop.");
        return finalRightBmap;
    }




}
