package com.example.zac.project1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;


import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity {

    //  IMAGE DISPLAYING AND DRAWING.
    private static final int IMAGE_GALLERY_REQUEST_LEFT = 1;
    private static final int IMAGE_GALLERY_REQUEST_RIGHT = 3;
    private ImageView imageViewLeft, imageViewRight;
    private Bitmap image, leftImage, rightImage, drawingBitmap, finalLeftBmap, finalRightBmap;
    ArrayList<Line> leftList = new ArrayList<>();
    ArrayList<Line> rightList = new ArrayList<>();
    private Canvas myCanvas;
    Paint paint;


    //  BOOLEANS
    private boolean leftHasImage, rightHasImage = false;
    private boolean drawMode = true;
    private boolean editMode = false;
    private boolean deleteLine = false;
    private boolean rightSide;
    private int indexLeft, indexRight = 0;
    private boolean morphed = false;


    //  NUMBERS
    int lineIndex = -1;
    int deleteIndex = 0;
    private float currentX, currentY, startX, startY, endX, endY;


    //  Morph / Warp stuff
    Line line;
    Morph morph = new Morph();
    ArrayList<Line> incrValues;
    ArrayList<Line> calcLines;
    ArrayList<Bitmap> newBitmaps;
    float numFrames = 0;
    int frame = 0;   //frame is the index of morphedImages array in morph.
    int last;
    AnimationDrawable morphAnimation;
    EditText myEdit;







    /**
     * App startup.
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Gets a reference to the image view that holds an image.
        imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewRight = (ImageView) findViewById(R.id.imageViewRight);
        findViewById(R.id.editText).setOnKeyListener(inputListen);

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
                            imageViewLeft.buildDrawingCache();          //scale?
                            leftImage = imageViewLeft.getDrawingCache();    //scale?
                            imageViewLeft.setOnTouchListener(imageTouched);
                            setUpCanvas(imageViewLeft, leftImage);
                            leftHasImage = true;
                            break;

                        case IMAGE_GALLERY_REQUEST_RIGHT:
                            rightImage = BitmapFactory.decodeStream(inputStream);
                            imageViewRight.setImageBitmap(rightImage);
                            imageViewRight.buildDrawingCache();         //scale?
                            rightImage = imageViewRight.getDrawingCache();      //scale?
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
        drawingBitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(),
                Bitmap.Config.RGB_565);
        myCanvas = new Canvas(drawingBitmap);

        //Draw the image bitmap into the canvas
        myCanvas.drawBitmap(img, 0, 0, null);

        imgView.setImageBitmap(drawingBitmap);

    }


    public void inputFrames(View view) {
        EditText myEdit = (EditText) findViewById(R.id.editText);
        String inputString =  myEdit.getText().toString();
        int numFrame = Integer.parseInt(inputString);
        morph.setNumFrames(numFrame);
    }




    //Remove the left screen's photo.
    public void deleteLeftPicture(View view) {
        leftImage = null;
        imageViewLeft.setImageBitmap(null);
        leftHasImage = false;
    }



    //Remove the right screen's photo.
    public void deleteRightPicture(View view) {
        rightImage = null;
        imageViewRight.setImageBitmap(null);
        rightHasImage = false;
    }





    //Load or remove right image button.
    public void rightImgToggle(View view) {
       if (rightHasImage)  {
           deleteRightPicture(view);
       }
        else {
           loadGalleryImageRight(view);
       }
    }




    //Load or remove left image button.
    public void leftImgToggle(View view) {
        if (leftHasImage)  {
            deleteLeftPicture(view);
        }
        else {
            loadGalleryImageLeft(view);
        }
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
        return (touchX - pointX) * (touchX - pointX) + (touchY - pointY) * (touchY - pointY) <= pointRadius * pointRadius;

    }




    //Draws the lines from a list.  Takes in arraylist and integer indicating which photo to draw on.
    private void drawLines(ArrayList<Line> lines, int side) {
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
                myCanvas.drawLine(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY(), paint);

            }

        if (side == 0) {
            imageViewRight.setImageBitmap(drawingBitmap);
        }

        if (side == 1) {
            imageViewLeft.setImageBitmap(drawingBitmap);
        }

    }




    //Loops through Each line and its points and redraws them.
    private void refreshLines(ArrayList<Line> lines) {
        if (rightSide)
            setUpCanvas(imageViewRight, rightImage);
        else
            setUpCanvas(imageViewLeft, leftImage);

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.RED);

            for (Line ln : lines) {
                myCanvas.drawCircle(ln.start.getX(), ln.start.getY(), 16, paint);
                myCanvas.drawCircle(ln.end.getX(), ln.end.getY(), 16, paint);
                myCanvas.drawLine(ln.start.getX(), ln.start.getY(), ln.end.getX(), ln.end.getY(), paint);

                if (rightSide)
                    imageViewRight.setImageBitmap(drawingBitmap);
                else
                    imageViewLeft.setImageBitmap(drawingBitmap);
            }
    }



    //Replace a pre-existing line with the editied one.
    public void replaceLine(int i, Line line, ArrayList<Line> list) {
        list.set(i, line);
        refreshLines(list);
    }




    //Checks to see if an existing point has been selected within the touch radius, then finds it's partner point for the same line.
    private void checkEndPoints(ArrayList<Line> linesList, float touchX, float touchY, float releaseX, float releaseY) {
        int index;
        Line line;
        LinePoint pt;
        for (Line ln : linesList) {
            if (radiusCheck(touchX, touchY, ln.getStart().getX(), ln.getStart().getY())) {   //IF it's the start point they grabbed...
                //the user touched an existing point
                index = linesList.indexOf(ln);
                pt = ln.getEnd();
                line = new Line(releaseX, releaseY, pt.getX(), pt.getY());
                replaceLine(index, line, linesList);
                break;
            }
            else if (radiusCheck(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY())) {  //User Grabbed the END POINT of a line...
                index = linesList.indexOf(ln);
                pt = ln.getStart();
                line = new Line(pt.getX(), pt.getY(), releaseX, releaseY);
                replaceLine(index, line, linesList);
                break;
            }
        }
    }




    //Toggle Button switch on to select a line to remove.
    public void deleteLineToggle(View view) {
        if (!deleteLine)
            deleteLine = true;
        else
            deleteLine = false;
    }



    //delete a selected line
    public void deleteLine(ArrayList<Line> linesList, float touchX, float touchY) {
        int index;
        for (Line ln : linesList) {
            if (radiusCheck(touchX, touchY, ln.getStart().getX(), ln.getStart().getY()) || radiusCheck(touchX, touchY, ln.getEnd().getX(), ln.getEnd().getY())) {   //IF it's an end point they grabbed...
                //the user touched an existing point
                deleteIndex = linesList.indexOf(ln);
                linesList.remove(deleteIndex);;
                break;
            }
        }

    }



    //Clears all lines drawn.
    public void clearLines(View view) {
        rightList.clear();
        leftList.clear();
        imageViewRight.setImageBitmap(rightImage);
        imageViewLeft.setImageBitmap(leftImage);
    }



    //MORPH BUTTON.  Uses Morph object to morph the 2 images.
    public void activateMorph(View view) {
        int i = 0;
        if(morph.numFrames <= 0) {
            morph.setNumFrames(3);
        }
        morph.doTheMorph(leftList, rightList, leftImage, rightImage);
        morphAnimation = new AnimationDrawable();
        morphed = true;

        morphAnimation.addFrame(new BitmapDrawable(getResources(), morph.morphedImages.get(0)), 600);
        for (i = 1; i < morph.morphedImages.size() - 2; i++) {
            morphAnimation.addFrame(new BitmapDrawable(getResources(), morph.morphedImages.get(i)), 200);
        }
        morphAnimation.addFrame(new BitmapDrawable(getResources(), morph.morphedImages.get(i)), 600);
        last = morph.morphedImages.size() - 1;

        morphAnimation.start();
        imageViewLeft.setImageDrawable(morphAnimation);
        imageViewRight.setImageBitmap(morph.morphedImages.get(last-1));

    }


    //step through the morphed frames.  Previous frame.
    public void previousFrame(View view) {
        if (morphed) {
            last = morph.morphedImages.size() - 2;
            if (frame == 0) {
                frame = last;      //frame is the index of morphedImages array in morph.
            } else {
                frame--;
            }
            imageViewRight.setImageBitmap(morph.morphedImages.get(frame));
        }
    }




    //step through the morphed frames.  Next frame.
    public void nextFrame(View view) {
        if (morphed) {
            if (frame == last) {
                frame = 0;
            } else {
                frame++;
            }
            imageViewRight.setImageBitmap(morph.morphedImages.get(frame));
        }
    }







    View.OnKeyListener inputListen = new View.OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press
                imageViewLeft.requestFocus();
                myEdit = (EditText) findViewById(R.id.editText);
                String inputString =  myEdit.getText().toString();
                int numFrame = Integer.parseInt(inputString);
                morph.setNumFrames(numFrame);
                return true;
            }
            return false;
        }
    };



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

                    if (editMode && deleteLine) {
                        if (!rightSide) {
                            deleteLine(leftList, startX, startY);
                            rightList.remove(deleteIndex);
                            refreshLines(leftList);
                            rightSide = true;
                            refreshLines(rightList);
                        }
                        else {
                            deleteLine(rightList, startX, startY);
                            leftList.remove(deleteIndex);
                            refreshLines(rightList);
                            rightSide = false;
                            refreshLines(leftList);
                        }
                    }
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
                        drawLines(rightList, 0);
                        drawLines(leftList, 1);

                        lineIndex++;
                    }
                    //EDITING
                    else {
                        endX = event.getX();
                        endY = event.getY();

                        if (!rightSide)
                                checkEndPoints(leftList, startX, startY, endX, endY);
                        else
                            checkEndPoints(rightList, startX, startY, endX, endY);
                    }

                    break;
            }
            return true;
        }
    };
}

