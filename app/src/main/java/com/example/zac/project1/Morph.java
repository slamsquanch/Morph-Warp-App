package com.example.zac.project1;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by Zac on 2017-02-08.
 */

public class Morph {

    //ArrayList<Line> increments;
    ArrayList<Line> incrValues;
    ArrayList<Line> calcLines;
    ArrayList<Bitmap> morphedImages;
    ArrayList<Line> increment;
    float numFrames = 0;
    Warp warp = new Warp();
    Line incLine;

    public Morph() {
        System.out.println("Morph created");
        morphedImages = new ArrayList<Bitmap>();
    }



    //DON'T FORGET TO SET NUM FRAMES FIRST!
    public void setNumFrames(int num) {
        numFrames = num;
    }

    // 1. Driver morph method calls the other morphing methods.
    public void doTheMorph(ArrayList<Line> linesLeft, ArrayList<Line> linesRight, Bitmap imgLeft, Bitmap imgRight) {
        //System.out.println("doTheMorph()");
        //incrValues = calcIncrementValues(linesLeft, linesRight, numFrames);      // Step 2
        calcIncrementValues(linesLeft, linesRight, numFrames);      // Step 2
        morphedImages.add(imgLeft);
        for (int i = 1; i <= numFrames; i++) {
            //System.out.println("doTheMorph() for loop");
            findFrameLines(linesLeft, linesRight, increment, i, imgLeft, imgRight);             //Step 3
        }
        morphedImages.add(imgRight);
    }




    // 2. Calculates the increment values between every pair of lines and returns them in a list.
    public void calcIncrementValues(ArrayList<Line> linesLeft, ArrayList<Line> linesRight, float frames) {
        //System.out.println("calcIncrementValues()");
        increment = new ArrayList<Line>();
        if (linesLeft.size() == linesRight.size()) {
            for (int i = 0; i < linesLeft.size(); i++) {
                incLine = new Line((linesLeft.get(i).getStart().getX() - linesRight.get(i).getStart().getX()) / frames,
                        (linesLeft.get(i).getStart().getY() - linesRight.get(i).getStart().getY()) / frames,
                        (linesLeft.get(i).getEnd().getX() - linesRight.get(i).getEnd().getX()) / frames,
                        (linesLeft.get(i).getEnd().getY() - linesRight.get(i).getEnd().getY()) / frames);
                increment.add(incLine);
            }
        }
        //return increment;
    }



    // 3. Calculate THIS frame's lines.
    public void findFrameLines(ArrayList<Line> leftLines, ArrayList<Line> rightLines, ArrayList<Line> increments, float curFrame, Bitmap imgL, Bitmap imgR) {
        //System.out.println("Inside findFrameLines()");
        ArrayList<Line> calcLines = calcFrameLines(leftLines, increments, curFrame);
        Bitmap left = warp.warpLeft(imgL, calcLines, leftLines);
        Bitmap right = warp.warpRight(imgR, calcLines, rightLines);
        //GET LEFT (LeftLines, calcLines, imgL)
        //GET RIGHT (RightLines, calcLines, imgR)
        morphedImages.add(crossfade(left, right, curFrame));        // Step 5
        System.out.println("morphedImages size: " + morphedImages.size());

    }



    // 4. Calculates the lines for the current frame being morphed.
    public ArrayList<Line> calcFrameLines(ArrayList<Line> leftLines, ArrayList<Line> increments, float curFrame) {
        //System.out.println("inside calcFrameLines()");
        calcLines = new ArrayList<Line>();
        if (leftLines.size() == increments.size()) {
            for (int i = 0; i < leftLines.size(); i++) {
                calcLines.add(new Line(leftLines.get(i).getStart().getX() + increments.get(i).getStart().getX() * curFrame,
                        leftLines.get(i).getStart().getY() + increments.get(i).getStart().getY() * curFrame,
                        leftLines.get(i).getEnd().getX() + increments.get(i).getEnd().getX() * curFrame,
                        leftLines.get(i).getEnd().getY() + increments.get(i).getEnd().getY() * curFrame));
            }
        }
        return calcLines;
    }


    /**
     * 5. Cross faces the 2 images (each frame) together.
     * @param leftFrame
     * @param rightFrame
     * @param curFrame
     * @return
     */
    public Bitmap crossfade(Bitmap leftFrame, Bitmap rightFrame, float curFrame) {
        //System.out.println("Inside crossFade()");
        float colourPercent1 = (numFrames - curFrame) / numFrames;
        float colourPercent2 = curFrame / numFrames;
        Bitmap newImage  = Bitmap.createBitmap(leftFrame.getWidth(), leftFrame.getHeight(),
                Bitmap.Config.ARGB_8888);
        if (leftFrame.getWidth() == rightFrame.getWidth() && leftFrame.getHeight() == rightFrame.getHeight()) {
            for (float x = 0; x < leftFrame.getWidth(); x++) {
                for (float y = 0; y < leftFrame.getHeight(); y++) {
                    int lPixel = leftFrame.getPixel((int)x, (int)y);
                    int rPixel = rightFrame.getPixel((int)x, (int)y);
                    int red = (int)(Color.red(lPixel) * colourPercent1
                            + Color.red(rPixel) * colourPercent2);
                    int green = (int)(Color.green(lPixel) * colourPercent1
                            + Color.green(rPixel) * colourPercent2);
                    int blue = (int)(Color.blue(lPixel) * colourPercent1
                            + Color.blue(rPixel) * colourPercent2);
                    newImage.setPixel((int)x, (int)y, Color.argb(255, red, green, blue));
                }
            }
        }
        return newImage;
    }
}
