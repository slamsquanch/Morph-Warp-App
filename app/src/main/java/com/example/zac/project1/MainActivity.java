package com.example.zac.project1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

public class MainActivity extends Activity {
    private static final int IMAGE_GALLERY_REQUEST_LEFT = 1;
    private static final int IMAGE_GALLERY_REQUEST_RIGHT = 3;
    private ImageView imageViewLeft, imageViewRight;
    private Bitmap image;
    private boolean leftHasImage, rightHasImage = false;


protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Gets a reference to the image view that holds an image.
        imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewRight = (ImageView) findViewById(R.id.imageViewRight);

    }



    public void loadGalleryImageLeft(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST_LEFT);
    }


    public void loadGalleryImageRight (View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, IMAGE_GALLERY_REQUEST_RIGHT);
    }


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
                    image = BitmapFactory.decodeStream(inputStream);

                    //Show the image to the user.
                    switch (requestCode) {
                        case IMAGE_GALLERY_REQUEST_LEFT:
                            imageViewLeft.setImageBitmap(image);
                            setUpCanvas(imageViewLeft);
                            leftHasImage = true;
                            break;

                        case IMAGE_GALLERY_REQUEST_RIGHT:
                            imageViewRight.setImageBitmap(image);
                            setUpCanvas(imageViewRight);
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


    public void setUpCanvas(ImageView imgView) {
        //Create a new image bitmap and attach a brand new canvas to it.
        Bitmap tempBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(),
                Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        //Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(image, 0, 0, null);

        //Draw lines ontop of this canvas
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        tempCanvas.drawCircle(60, 60, 8, paint);

        imgView.setImageBitmap(tempBitmap);

    }

}
