package com.heaven7.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;

import androidx.exifinterface.media.ExifInterface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by heaven7 on 2015/9/15.
 */
public final class ImageParser {

    /**
     * the callback of parse image
     * @since 1.1.6
     */
    public interface Callback{
        /**
         * get the desire resized dimensions. this dimensions indicate the final width and height of bitmap.
         * the array length is two.
         * @param maxWidth the max width
         * @param maxHeight the max height
         * @param actualWidth the actual width
         * @param actualHeight the actual height
         * @return the resized dimensions. [0] is width. [1] is height.
         */
        int[] getResizedDimensions(int maxWidth, int maxHeight, int actualWidth, int actualHeight);

        /**
         * find best sample size. which used to scale.
         * @param actualWidth the actual width
         * @param actualHeight the actual height
         * @param desiredWidth the desired width
         * @param desiredHeight the desired height
         * @return the best sample size
         */
        int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight);
    }

    /**
     * the image decoder
     */
    public interface IDecoder{
        /**
         * decode image to bitmap
         * @param param the param
         * @param options the options of bitmap decode
         * @return the bitmap
         */
        Bitmap decode(DecodeParam param, BitmapFactory.Options options);
        /**
         * get the image orientation
         * @param param the decode param
         * @return >=0 means the valid. -1 for unknown.
         */
        int getOrientation(DecodeParam param) throws IOException;
    }
    /** decode param */
    public static class DecodeParam{
        public String pathName;
        public byte[] imageArray;
        public int resId ; //image resource id
        public Resources resources;

        public DecodeParam(String pathName) {
            this.pathName = pathName;
        }
        public DecodeParam(byte[] imageArray) {
            this.imageArray = imageArray;
        }
        public DecodeParam(Resources resources,int resId) {
            this.resId = resId;
            this.resources = resources;
        }
    }
    private static final IDecoder sPathDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            return BitmapFactory.decodeFile(param.pathName,options);
        }
        @Override
        public int getOrientation(DecodeParam param) throws IOException{
            ExifInterface exif = new ExifInterface(param.pathName);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        }
    };
    // if check exif. this need sd-permission
    private static final IDecoder sResourceDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            return BitmapFactory.decodeResource(param.resources, param.resId, options);
        }
        @Override
        public int getOrientation(DecodeParam param) throws IOException {
            return -1;
        }
    };
    private static final IDecoder sByteArrayDecoder = new IDecoder() {
        @Override
        public Bitmap decode(DecodeParam param, BitmapFactory.Options options) {
            final byte[] bytes = param.imageArray;
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        }
        @Override
        public int getOrientation(DecodeParam param) throws IOException {
            ByteArrayInputStream in = new ByteArrayInputStream(param.imageArray);
            ExifInterface exif = new ExifInterface(in);
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        }
    };

    /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
    private static final Object sDecodeLock = new Object();
    private static final SimpleCallback DEFAULT = new SimpleCallback();

    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Bitmap.Config mDecodeConfig;
    /** true to check exif info of image. */
    private final boolean mCheckExif;
    /** the callback which used to find best ratio. */
    private Callback mCallback = DEFAULT;

    public ImageParser(int mMaxWidth, int mMaxHeight){
        this(mMaxWidth,mMaxHeight, Bitmap.Config.RGB_565);
    }
    public ImageParser(int mMaxWidth, int mMaxHeight, Bitmap.Config config) {
        this(mMaxWidth, mMaxHeight, config, false);
    }

    /**
     * create image parser.
     * @param mMaxWidth the max width of image
     * @param mMaxHeight the max height of image
     * @param config the image config
     * @param mCheckExif the cache dir of tmp file.
     * @since 1.1.5
     */
    public ImageParser(int mMaxWidth, int mMaxHeight, Bitmap.Config config, boolean mCheckExif) {
        this.mMaxWidth = mMaxWidth;
        this.mMaxHeight = mMaxHeight;
        this.mDecodeConfig = config;
        this.mCheckExif = mCheckExif;
    }

    public int getMaxWidth() {
        return mMaxWidth;
    }
    public int getMaxHeight() {
        return mMaxHeight;
    }
    public void setCallback(Callback mCallback) {
        if(mCallback == null){
            throw new NullPointerException();
        }
        this.mCallback = mCallback;
    }
    /**
     * decode to bitmap
     * @param decoder the decoder
     * @param param the decode param
     * @param param the decode param
     * @return the bitmap
     * @since 1.1.5
     */
    public Bitmap decodeToBitmap(IDecoder decoder, DecodeParam param){
        return decodeToBitmap(decoder, param, null);
    }
    /**
     * decode to bitmap
     * @param decoder the decoder
     * @param param the decode param
     * @param param the decode param
     * @param outInfo the out sample size and rotate info
     * @return the bitmap
     * @since 1.1.8
     */
    public Bitmap decodeToBitmap(IDecoder decoder, DecodeParam param, float[] outInfo){
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            synchronized (sDecodeLock) {
                bitmap = decoder.decode(param, decodeOptions);
            }
        } else {
            // If we have to resize this image, first get the natural bounds.
            decodeOptions.inJustDecodeBounds = true;
            synchronized (sDecodeLock) {
                decoder.decode(param, decodeOptions); //just decode bounds
            }
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            // Then compute the dimensions we would ideally like to decode to.
            int[] desiredSpecs = mCallback.getResizedDimensions(mMaxWidth, mMaxHeight, actualWidth, actualHeight);
            int desiredWidth = desiredSpecs[0];
            int desiredHeight = desiredSpecs[1];

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            // TODO(ficus): Do we need this or is it okay since API 8 doesn't support it?
            // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
            decodeOptions.inSampleSize = mCallback.findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            if(outInfo != null){
                outInfo[0] = decodeOptions.inSampleSize;
            }
            Bitmap tempBitmap;
            synchronized (sDecodeLock) {
                tempBitmap = decoder.decode(param, decodeOptions);
            }
            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }
        //if need to check the exif info.
        if(mCheckExif){
            bitmap = doExif(decoder, param, bitmap, outInfo);
        }
        return bitmap;
    }

    /**
     * parse image file to bitmap
     * @param pathName the image file path
     * @return the bitmap
     */
    public Bitmap parseToBitmap(String pathName){
        File file = new File(pathName);
        if (!file.exists()) {
            return null;
        }
        return decodeToBitmap(sPathDecoder, new DecodeParam(pathName));
    }

    /**
     * parse image data to bitmap
     * @param data the data
     * @return the bitmap
     */
    public Bitmap parseToBitmap(byte[] data){
        return decodeToBitmap(sByteArrayDecoder,new DecodeParam(data));
    }

    /**
     * parse resource id to bitmap
     * @param context the context
     * @param resId the image resource id
     * @return the bitmap
     */
    public Bitmap parseToBitmap(Context context, int resId){
        return decodeToBitmap(sResourceDecoder,new DecodeParam(context.getResources(),resId));
    }
    private static Bitmap doExif(IDecoder decoder, DecodeParam param, Bitmap bitmap, float[] outInfo) {
        try {
            int orientation = decoder.getOrientation(param);
            if(orientation < 0){
                if(Build.VERSION.SDK_INT >= 24){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    ExifInterface exif = new ExifInterface(new ByteArrayInputStream(baos.toByteArray()));
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                }else {
                    File sd = Environment.getExternalStorageDirectory();
                    File target = new File(sd, "img_parser_" + System.currentTimeMillis() +".jpg");
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(target);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        ExifInterface exif = new ExifInterface(target.getAbsolutePath());
                        orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                    }finally {
                        if(fos != null){
                            try {
                                fos.close();
                            }catch (IOException e){
                            }
                        }
                        target.delete();
                    }
                }
            }
            if(outInfo != null){
                outInfo[1] = orientation;
            }
            //no rotate
            if(orientation == 0){
                return bitmap;
            }
            Matrix matrix = new Matrix();
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            System.out.println(Logger.toString(e));
            //ignore
        }
        return bitmap;
    }

    /**
     * the sample callback.
     * @since 1.1.6
     */
    public static class SimpleCallback implements Callback{
        @Override
        public int[] getResizedDimensions(int maxWidth, int maxHeight, int actualWidth, int actualHeight) {
            int desiredWidth = getResizedDimension(maxWidth, maxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth,
                    actualHeight, actualWidth);

            return new int[]{desiredWidth, desiredHeight};
        }
        @Override
        public int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
            return findBestSampleSize0(actualWidth, actualHeight, desiredWidth, desiredHeight);
        }

        /**
         * Scales one side of a rectangle to fit aspect ratio.
         *
         * @param maxPrimary Maximum size of the primary dimension (i.e. width for
         *        max width), or zero to maintain aspect ratio with secondary
         *        dimension
         * @param maxSecondary Maximum size of the secondary dimension, or zero to
         *        maintain aspect ratio with primary dimension
         * @param actualPrimary Actual size of the primary dimension
         * @param actualSecondary Actual size of the secondary dimension
         */
        private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                               int actualSecondary) {
            // If no dominant value at all, just return the actual.
            if (maxPrimary == 0 && maxSecondary == 0) {
                return actualPrimary;
            }
            // If primary is unspecified, scale primary to match secondary's scaling ratio.
            if (maxPrimary == 0) {
                double ratio = (double) maxSecondary / (double) actualSecondary;
                return (int) (actualPrimary * ratio);
            }

            if (maxSecondary == 0) {
                return maxPrimary;
            }

            double ratio = (double) actualSecondary / (double) actualPrimary;
            int resized = maxPrimary;
            if (resized * ratio > maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }
        /**
         * Returns the largest power-of-two divisor for use in downscaling a bitmap
         * that will not result in the scaling past the desired dimensions.
         *
         * @param actualWidth Actual width of the bitmap
         * @param actualHeight Actual height of the bitmap
         * @param desiredWidth Desired width of the bitmap
         * @param desiredHeight Desired height of the bitmap
         */
        // Visible for testing.
        private static int findBestSampleSize0(
                int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
            double wr = (double) actualWidth / desiredWidth;
            double hr = (double) actualHeight / desiredHeight;
            double ratio = Math.min(wr, hr);
            float n = 1.0f;
            while ((n * 2) <= ratio) {
                n *= 2;
            }

            return (int) n;
        }
    }
}
