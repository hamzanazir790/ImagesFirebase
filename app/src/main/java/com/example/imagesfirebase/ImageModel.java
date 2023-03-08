package com.example.imagesfirebase;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageModel {

    String imagename;
    Uri image;
    Bitmap bitmap;

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImageModel(String imagename, Uri image, Bitmap bitmap) {
        this.imagename = imagename;
        this.image = image;
        this.bitmap = bitmap;
    }
}
