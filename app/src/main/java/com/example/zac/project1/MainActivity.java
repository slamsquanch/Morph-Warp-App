package com.example.zac.project1;

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

import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int IMAGE_GALLERY_REQUEST_LEFT = 1;
    private static final int IMAGE_GALLERY_REQUEST_RIGHT = 3;
    private ImageView imageViewLeft, imageViewRight;
    private Bitmap image, leftImage, rightImage;
    private boolean leftHasImage, rightHasImage = false;
    private boolean drawMode = true;
    private boolean editMode = false;
    private boolean rightSide;
    private int indexLeft, indexRight = 0;
    int lineIndex = -1;
    private Canvas myCanvas;
    private Bitmap tempBitmap, cleanImage;
    private float currentX, currentY, startX, startY, endX, endY;
    Paint paint;
    ArrayList<Line> leftList = new ArrayList<Line>();
    ArrayList<Line> rightList = new ArrayList<Line>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Gets a reference to the image view that holds an image.
        imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewRight = (ImageView) findViewById(R.id.imageViewRight);

    }


    //Load in the left image.
    public void loadGalleryImageLeft(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST_LEFT);
    }


    //Load in the right image.
    public void loadGalleryImageRight(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST_RIGHT);
    }

    //Opens photo gallery to stream in images.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == IMAGE_GALLERY_REQUEST_LEFT || requestCode == IMAGE_GALLERY_REQUEST_RIGHT) {
                if (resultCode == RESULT_OK && null != data) {

                    // Get the Image from data.  Address of img on SD card / drive.
                    Uri imgURI = data.getData();

                    //Input stream stuff here.
                    InputStream inputStream;

                    inputStream = getContentResolver().openInputStream(imgURI);

                    //get a bitMap from the stream.
                    //image = BitmapFactory.decodeStream(inputStream);

                    //Show the image to the user.
                    switch (requestCode) {
                        case IMAGE_GALLERY_REQUEST_LEFT:
                            leftImage = BitmapFactory.decodeStream(inputStream);
                            imageViewLeft.setImageBitmap(leftImage);
                            imageViewLeft.buildDrawingCache();
                            leftImage = imageViewLeft.getDrawingCache();
                            imageViewLeft.setOnTouchListener(imageTouched);
                            setUpCanvas(imageViewLeft, leftImage);
                            leftHasImage = true;
                            break;

                        case IMAGE_GALLERY_REQUEST_RIGHT:
                            rightImage = BitmapFactory.decodeStream(inputStream);
                            imageViewRight.setImageBitmap(rightImage);
                            imageViewRight.buildDrawingCache();
                            rightImage = imageViewRight.getDrawingCache();
                            imageViewRight.setOnTouchListener(imageTouched);
                            setUpCanvas(imageViewRight, rightImage);
                            rightHasImage = true;
                            break;
                    }
                }
            } else {
                Toast.makeText(this, "You have not selected an image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


    //Makes a new bitmap of the image then creates a canvas of that bitmap to draw on.
    private void setUpCanvas(ImageView imgView, Bitmap img) {
        //Create a new image bitmap and attach a brand new canvas to it.
        tempBitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(),
                Bitmap.Config.RGB_565);
        myCanvas = new Canvas(tempBitmap);

        //Draw the image bitmap into the canvas
        myCanvas.drawBitmap(img, 0, 0, null);

        imgView.setImageBitmap(tempBitmap);

    }


    //Button that turns edit mode on/off.
    public void toggleEditOnOff(View view) {
        if (!editMode && drawMode) {
            editMode = true;
            drawMode = false;
        } else {
            editMode = false;
            drawMode = true;
        }
    }


    //False means left photo is being interacted with.  True means right.
    private void sideTouched(ImageView imageView) {
        if (imageView == imageViewLeft) {
            rightSide = false;
        } else if (imageView == imageViewRight) {
            rightSide = true;
        }
    }


    //CHECKS RADIUS OF THE PERSON'S TOUCH WITH EXISTING POINTS COORDINATES.
    public boolean radiusCheck(float touchX, float touchY, float pointX, float pointY) {
        float pointRadius = 55;
        if ((touchX - pointX) * (touchX - pointX) + (touchY - pointY) * (touchY - pointY) <= pointRadius * pointRadius) {
            // TOUCH INSIDE THE CIRCLE!
            return true;
        }
        return false;

    }


    private boolean checkPointTouch(ArrayList<Line> lines, float touchX, float touchY) {
        int index = 0;
        Line line;
        for (Line ln : lines) {
            if (radiusCheck(touchX, touchY, ln.getStart().getX(), ln.getStart().getY())) {
                //the user touched an existing point
                //index = lines.indexOf(ln);
                //line = new Line(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY());
                //lines.set(index, line);
                return true;
            }

            if (radiusCheck(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY())) {
                //the user touched an existing point
                //index = lines.indexOf(ln);
                //line = new Line(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY());
                //lines.set(index, line);
                return true;
            }
        }
        return false;
    }


    private void drawPointLines(ArrayList<Line> lines, int side) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);

        if (side == 0) {
            setUpCanvas(imageViewRight, rightImage);
        }
        if (side == 1) {
            setUpCanvas(imageViewLeft, leftImage);
        }
            for (Line ln : lines) {
                myCanvas.drawCircle(ln.start.getX(), ln.start.getY(), 16, paint);
                myCanvas.drawCircle(ln.end.getX(), ln.end.getY(), 16, paint);
                paint.setColor(Color.BLUE);
                myCanvas.drawLine(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY(), paint);
                paint.setColor(Color.RED);

            }

        if (side == 0) {
            imageViewRight.setImageBitmap(tempBitmap);
        }
        
        if (side == 1) {
            imageViewLeft.setImageBitmap(tempBitmap);
        }

}



    //Loops through Each line and its points and redraws them.
    private void refreshLines(ArrayList<Line> lines) {
        if (rightSide) {
           // imageViewRight.setImageBitmap(rightImage);
           // imageViewRight.buildDrawingCache();
            //rightImage = imageViewRight.getDrawingCache();
            //imageViewRight.setOnTouchListener(imageTouched);
            setUpCanvas(imageViewRight, rightImage);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);

            for (Line ln : lines) {
                myCanvas.drawCircle(ln.start.getX(), ln.start.getY(), 16, paint);
                myCanvas.drawCircle(ln.end.getX(), ln.end.getY(), 16, paint);
                paint.setColor(Color.BLUE);
                myCanvas.drawLine(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY(), paint);
                paint.setColor(Color.RED);
                imageViewRight.setImageBitmap(tempBitmap);
                //drawEndPoint(ln.getStart());
               // drawEndPoint(ln.getEnd());
                //lineDraw(ln.getStart(), ln.getEnd());
            }
        }
        else {
            //imageViewLeft.setImageBitmap(leftImage);
           // imageViewLeft.buildDrawingCache();
            //leftImage = imageViewLeft.getDrawingCache();
           // imageViewLeft.setOnTouchListener(imageTouched);
            setUpCanvas(imageViewLeft, leftImage);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);

            for (Line ln : lines) {
                myCanvas.drawCircle(ln.start.getX(), ln.start.getY(), 16, paint);
                myCanvas.drawCircle(ln.end.getX(), ln.end.getY(), 16, paint);
                paint.setColor(Color.BLUE);
                myCanvas.drawLine(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY(), paint);
                paint.setColor(Color.RED);
                imageViewLeft.setImageBitmap(tempBitmap);
            }
        }

    }


   /* public boolean whichPoint(ArrayList<Line> linesList, float touchX, float touchY, float releaseX, float releaseY) {



    } */






    public void replaceLine(int i, Line line, ArrayList<Line> list) {
        list.set(i, line);
        refreshLines(list);
    }


    //Draws the end points of a line. (both start and end)
    private void drawEndPoint(LinePoint point) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        myCanvas.drawCircle(point.getX(), point.getY(), 16, paint);
        imageViewLeft.setImageBitmap(tempBitmap);
        imageViewRight.setImageBitmap(tempBitmap);
    }


    //Draws the line between two end points.
    private void lineDraw(LinePoint point1, LinePoint point2) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        float startX = point1.getX();
        float startY = point1.getY();
        float endX = point2.getX();
        float endY = point2.getY();
        myCanvas.drawLine(startX, startY, endX, endY, paint);
        imageViewLeft.setImageBitmap(tempBitmap);
        imageViewRight.setImageBitmap(tempBitmap);
    }


    //Gets the start point of a line at a specfic index
    public LinePoint getStartPoint(ArrayList<Line> linesList, int index){
        Line line = linesList.get(index);
        return line.getStart();
    }

    //Gets the end point of a line at a specfic index
    public LinePoint getEndPoint(ArrayList<Line> lines, int index){
        Line line = lines.get(index);
        return line.getEnd();
    }


    private void getOtherPoint(ArrayList<Line> linesList, float touchX, float touchY, float releaseX, float releaseY) {
        int index;
        Line line;
        LinePoint pt;
        for (Line ln : linesList) {
            if (radiusCheck(touchX, touchY, ln.getStart().getX(), ln.getStart().getY())) {   //IF it's the start point they grabbed...
                //the user touched an existing point
                index = linesList.indexOf(ln);
                pt = ln.getEnd();
                line = new Line(pt.getX(), pt.getY(), releaseX, releaseY);
                //System.out.println("get Index of edit: " + index);
                //System.out.println("edit Points X: " + releaseX + " Y: " + releaseY);
                replaceLine(index, line, linesList);
                break;
            }
            else if (radiusCheck(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY())) {  //User Grabbed the END POINT of a line...
                index = linesList.indexOf(ln);
                pt = ln.getStart();
                line = new Line(releaseX, releaseY, pt.getX(), pt.getY());
                //System.out.println("get Index of edit: " + index);
                //System.out.println("edit Points X: " + releaseX + " Y: " + releaseY);
                replaceLine(index, line, linesList);
                break;
            }
        }
    }



    //Screen touch event handler.
    View.OnTouchListener imageTouched = new View.OnTouchListener() {
            public boolean onTouch(View imageView, MotionEvent event) {

            ImageView imgView = (ImageView) imageView;

            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    sideTouched(imgView);

                    break;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_UP:
                    //DRAWING
                    if (!editMode)     {
                        endX = event.getX();
                        endY = event.getY();
                        Line lineRight = new Line(startX, startY, endX, endY);
                        Line lineLeft = new Line(startX, startY, endX, endY);
                        rightList.add(lineRight);
                        leftList.add(lineLeft);
                        drawPointLines(rightList, 0);
                        drawPointLines(leftList, 1);
                        //refreshLines(rightList);
                        //refreshLines(leftList);
                        //System.out.println("Start Coords X: " + startX + "  Y: " + startY);
                        //System.out.println("End Coords X: " + endX + "  Y: " + endY);

                        lineIndex++;
                    }
                    //EDITING
                    else {
                        endX = event.getX();
                        endY = event.getY();
                        //System.out.println("Edit touch X: " + startX + "  Y: " + startY);
                        //System.out.println("Edit release X: " + endX + "  Y: " + endY);
                        if (!rightSide) {
                                //System.out.println("LEFT SCREEN EDIT");
                                getOtherPoint(leftList, startX, startY, endX, endY);
                                //LinePoint p = getStartPoint(leftList, lineIndex);
                        }
                        else {
                            //System.out.println("RIGHT SCREEN EDIT");
                            //LinePoint p = getStartPoint(rightList, lineIndex);
                            getOtherPoint(rightList, startX, startY, endX, endY);
                        }
                    }

                    break;
            }
            return true;
        }
    };


}
