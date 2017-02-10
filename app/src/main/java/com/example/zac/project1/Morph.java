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
    ArrayList<Bitmap> bitmaps, morphedImages;
    float numFrames = 0;
    Warp warp = new Warp();




    //DON'T FORGET TO SET NUM FRAMES FIRST!
    public void setNumFrames(int num) {
        numFrames = num;
    }

    // 1. Driver morph method calls the other morphing methods.
    public void doTheMorph(ArrayList<Line> linesLeft, ArrayList<Line> linesRight, Bitmap imgLeft, Bitmap imgRight) {
        incrValues = calcIncrementValues(linesLeft, linesRight, numFrames);      // Step 2
        bitmaps = new ArrayList<Bitmap>();
        bitmaps.add(imgLeft);
        for (int i = 1; i < numFrames; i++) {
            findFrameLines(linesLeft, linesRight, incrValues, i, imgLeft, imgRight);             //Step 3
        }
    }




    // 2. Calculates the increment values between every pair of lines and returns them in a list.
    public ArrayList<Line> calcIncrementValues(ArrayList<Line> linesLeft, ArrayList<Line> linesRight, float numFrames) {
        ArrayList<Line> increment = new ArrayList<Line>();
        if (linesLeft.size() == linesRight.size()) {
            for (int i = 0; i < linesLeft.size(); i++) {
                Line incLine = new Line((linesLeft.get(i).getStart().getX() - linesRight.get(i).getStart().getX()) / numFrames,
                        (linesLeft.get(i).getStart().getY() - linesRight.get(i).getStart().getY()) / numFrames,
                        (linesLeft.get(i).getEnd().getX() - linesRight.get(i).getEnd().getX()) / numFrames,
                        (linesLeft.get(i).getEnd().getY() - linesRight.get(i).getEnd().getY()) / numFrames);
                increment.add(incLine);
            }
        }
        return increment;
    }



    // 3. Calculate THIS frame's lines.
    public void findFrameLines(ArrayList<Line> leftLines, ArrayList<Line> rightLines, ArrayList<Line> increments, float curFrame, Bitmap imgL, Bitmap imgR) {
        ArrayList<Line> calcLines = calcFrameLines(leftLines, increments, curFrame);
        Bitmap left = warp.warpLeft(imgL, leftLines, calcLines);
        Bitmap right = warp.warpRight(imgR, rightLines, calcLines);
        //GET LEFT (LeftLines, calcLines, imgL)
        //GET RIGHT (RightLines, calcLines, imgR)
        morphedImages = new ArrayList<Bitmap>();
        morphedImages.add(crossfade(left, right, curFrame));        // Step 5

    }



    // 4. Calculates the lines for the current frame being morphed.
    public ArrayList<Line> calcFrameLines(ArrayList<Line> leftLines, ArrayList<Line> increments,float curFrame) {
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
        float colourPercent1 = (numFrames - curFrame) / numFrames;
        float colourPercent2 = curFrame / numFrames;
        Bitmap newImage  = Bitmap.createBitmap(leftFrame.getWidth(), leftFrame.getHeight(),
                Bitmap.Config.RGB_565);

        if (leftFrame.getWidth() == rightFrame.getWidth() && leftFrame.getHeight() == rightFrame.getHeight()) {
            for (float x = 0; x < leftFrame.getWidth(); x++) {
                for (float y = 0; y < leftFrame.getHeight(); y++) {
                    int lPixel = leftFrame.getPixel((int)x, (int)y);
                    int rPixel = rightFrame.getPixel((int)x, (int)y);
                    int red = ((int)Color.red(lPixel) * (int)colourPercent1)
                            + ((int)Color.red(rPixel) * (int)colourPercent2);
                    int green = ((int)Color.green(lPixel) * (int)colourPercent1)
                            + ((int)Color.green(rPixel) * (int)colourPercent2);
                    int blue = ((int)Color.blue(lPixel) * (int)colourPercent1)
                            + ((int)Color.blue(rPixel) * (int)colourPercent2);
                    newImage.setPixel((int)x, (int)y, Color.argb(255, red, green, blue));
                }
            }
        }
        return newImage;
    }
}
