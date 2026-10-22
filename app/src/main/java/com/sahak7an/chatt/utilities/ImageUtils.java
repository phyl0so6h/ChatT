package com.sahak7an.chatt.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    private ImageUtils() {

    }

    // Images are stored as Base64 inside Firestore documents (1 MiB limit),
    // so they are downscaled and compressed before encoding.
    public static String encode(Bitmap bitmap, int maxSize, int quality) {

        if (bitmap == null) {
            return null;
        }

        Bitmap scaledBitmap = scaleToFit(bitmap, maxSize);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.WEBP, quality, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    public static Bitmap decode(String encodedImage) {

        if (encodedImage == null || encodedImage.isEmpty()) {
            return null;
        }

        try {

            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        } catch (IllegalArgumentException e) {

            return null;

        }

    }

    // Scales the bitmap down so that its longest side is at most maxSize, keeping the aspect ratio.
    public static Bitmap scaleToFit(Bitmap bitmap, int maxSize) {

        if (bitmap == null) {
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int longestSide = Math.max(width, height);

        if (longestSide <= maxSize) {
            return bitmap;
        }

        float scale = (float) maxSize / longestSide;

        return Bitmap.createScaledBitmap(
                bitmap,
                Math.max(1, Math.round(width * scale)),
                Math.max(1, Math.round(height * scale)),
                true
        );

    }

    public static Bitmap decodeScaled(String encodedImage, int maxSize) {

        return scaleToFit(decode(encodedImage), maxSize);

    }

}
