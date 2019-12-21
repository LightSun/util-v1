package com.heaven7.core.util;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * the spans which can effect measure state.
 * @author heaven7
 */
public class ExpandMeasurableSpan extends MetricAffectingSpan implements Parcelable {

    private boolean bold;
    private boolean underline;
    private boolean italic;
    private boolean strikeThru; //'delete-line'
    private boolean subscript;
    private boolean superscript;

    private int textSize;  //in pix
    private String mFamily;
    private Typeface mTypeface;
    private float mProportion;

    @Override
    public void updateMeasureState(TextPaint ds) {
        updateDrawState(ds);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if(textSize != 0){
            ds.setTextSize(textSize);
        }
        if(bold){
            ds.setFakeBoldText(true);
        }
        if(underline){
            ds.setUnderlineText(true);
        }
        if(italic){
            ds.setTextSkewX(-0.25f);
        }
        if(strikeThru){
            ds.setStrikeThruText(true);
        }
        if(mFamily != null){
            applyFontFamily(ds, mFamily);
        }
        if(mTypeface != null){
            ds.setTypeface(mTypeface);
        }
        if(mProportion != 0){
            ds.setTextSize(ds.getTextSize() * mProportion);
        }
        if(subscript){
            ds.baselineShift -= (int) (ds.ascent() / 2);
        }
        if(superscript){
            ds.baselineShift += (int) (ds.ascent() / 2);
        }
    }

    private static void applyFontFamily(Paint paint, String family) {
        int style;
        Typeface old = paint.getTypeface();
        if (old == null) {
            style = Typeface.NORMAL;
        } else {
            style = old.getStyle();
        }
        final Typeface styledTypeface = Typeface.create(family, style);
        int fake = style & ~styledTypeface.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(styledTypeface);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.bold ? (byte) 1 : (byte) 0);
        dest.writeByte(this.underline ? (byte) 1 : (byte) 0);
        dest.writeByte(this.italic ? (byte) 1 : (byte) 0);
        dest.writeByte(this.strikeThru ? (byte) 1 : (byte) 0);
        dest.writeByte(this.subscript ? (byte) 1 : (byte) 0);
        dest.writeByte(this.superscript ? (byte) 1 : (byte) 0);

        dest.writeInt(this.textSize);
        dest.writeString(this.mFamily);
        dest.writeFloat(this.mProportion);
        if(mTypeface != null){
            LeakyTypefaceStorage.writeTypefaceToParcel(mTypeface, dest);
        }
    }
    protected ExpandMeasurableSpan(Parcel in) {
        this.bold = in.readByte() != 0;
        this.underline = in.readByte() != 0;
        this.italic = in.readByte() != 0;
        this.strikeThru = in.readByte() != 0;
        this.subscript = in.readByte() != 0;
        this.superscript = in.readByte() != 0;

        this.textSize = in.readInt();
        this.mFamily = in.readString();
        this.mProportion = in.readFloat();
        this.mTypeface = LeakyTypefaceStorage.readTypefaceFromParcel(in);
    }

    public static final Creator<ExpandMeasurableSpan> CREATOR = new Creator<ExpandMeasurableSpan>() {
        @Override
        public ExpandMeasurableSpan createFromParcel(Parcel source) {
            return new ExpandMeasurableSpan(source);
        }

        @Override
        public ExpandMeasurableSpan[] newArray(int size) {
            return new ExpandMeasurableSpan[size];
        }
    };
}
