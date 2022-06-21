package com.pitts.photo_detector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Miscellaneous {
    /**
     * Copied directly from https://stackoverflow.com/questions/3373860/convert-a-bitmap-to-grayscale-in-android
     *
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /**
     * Bitmap encoder to String
     * Copied from https://stackoverflow.com/questions/47431742/how-to-convert-bitmap-to-byte-array-and-byte-array-to-bitmap-in-android
     *
     * @param bmp
     * @return
     */
    public static String BitmapToString(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /**
     * String decoder to Bitmap
     * Copied from https://stackoverflow.com/questions/47431742/how-to-convert-bitmap-to-byte-array-and-byte-array-to-bitmap-in-android
     *
     * @param str
     * @return
     */
    public static Bitmap StringToBitmap(String str) {
        byte[] data = android.util.Base64.decode(str, android.util.Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }
}
