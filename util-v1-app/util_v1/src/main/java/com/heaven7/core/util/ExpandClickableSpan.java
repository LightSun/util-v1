package com.heaven7.core.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * the expend click span .which will bot effect measure.
 */
public class ExpandClickableSpan extends ClickableSpan{

    private int textColor; //foreground color
    private int backgroundColor;
    private int linkColor;
    private boolean underline;

    private CharSequence text;
    private OnTextClickListener mClickListener;

    protected ExpandClickableSpan(ExpandClickableSpan.Builder builder) {
        this.textColor = builder.textColor;
        this.backgroundColor = builder.backgroundColor;
        this.linkColor = builder.linkColor;
        this.underline = builder.underline;
        this.text = builder.text;
        this.mClickListener = builder.mClickListener;
    }

    @Override
    public void onClick(View widget) {
         if(mClickListener != null){
             mClickListener.onClickText(widget, text);
         }
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        if(textColor != 0){
            ds.setColor(textColor);
        }
        if(backgroundColor != 0){
            ds.bgColor = backgroundColor;
        }
        if(linkColor != 0){
            ds.linkColor = linkColor;
        }
        if(underline){
            ds.setUnderlineText(true);
        }
    }
    public interface OnTextClickListener{
        void onClickText(View view, CharSequence text);
    }

    public int getTextColor() {
        return this.textColor;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getLinkColor() {
        return this.linkColor;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public CharSequence getText() {
        return this.text;
    }

    public OnTextClickListener getClickListener() {
        return this.mClickListener;
    }

    public static class Builder {
        private int textColor; //foreground color
        private int backgroundColor;
        private int linkColor;
        private boolean underline;
        private CharSequence text;
        private OnTextClickListener mClickListener;

        public Builder setTextColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setLinkColor(int linkColor) {
            this.linkColor = linkColor;
            return this;
        }

        public Builder setUnderline(boolean underline) {
            this.underline = underline;
            return this;
        }

        public Builder setText(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder setClickListener(OnTextClickListener mClickListener) {
            this.mClickListener = mClickListener;
            return this;
        }

        public ExpandClickableSpan build() {
            return new ExpandClickableSpan(this);
        }
    }
}
