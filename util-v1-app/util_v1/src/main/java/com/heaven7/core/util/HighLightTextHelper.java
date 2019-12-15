package com.heaven7.core.util;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * the high-light text helper. used to high light text as you like
 * @author heaven7
 * @since 1.1.6
 */
public class HighLightTextHelper {

    private String rawText;
    private String highLightText;
    private int defaultColor;
    private int highLightColor;

    public CharSequence getText(){
        if(TextUtils.isEmpty(highLightText)){
            return rawText;
        }
        int i = rawText.indexOf(highLightText);
        if(i >= 0){
            StyledText st = new StyledText();
            String leftText = rawText;
            do {
                leftText = setText(st, leftText, i);
                i = leftText.indexOf(highLightText);
            }while (i >= 0 && !TextUtils.isEmpty(leftText));

            if(!TextUtils.isEmpty(leftText)){
                st.appendForeground(leftText, defaultColor);
            }
            return st;
        }else {
            return rawText;
        }
    }

    private String setText(StyledText st, String rawText, int index) {
        if(index > 0){
            st.appendForeground(rawText.substring(0, index), defaultColor);
        }
        st.appendForeground(highLightText, highLightColor);
        return rawText.substring(index + highLightText.length());
    }

    protected HighLightTextHelper(HighLightTextHelper.Builder builder) {
        this.rawText = builder.rawText;
        this.highLightText = builder.highLightText;
        this.defaultColor = builder.defaultColor;
        this.highLightColor = builder.highLightColor;
    }

    public String getRawText() {
        return this.rawText;
    }

    public String getHighLightText() {
        return this.highLightText;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public int getHighLightColor() {
        return this.highLightColor;
    }

    public static class Builder {
        private String rawText;
        private String highLightText;
        private int defaultColor;
        private int highLightColor;

        public Builder setRawText(String rawText) {
            this.rawText = rawText;
            return this;
        }

        public Builder setHighLightText(String highLightText) {
            this.highLightText = highLightText;
            return this;
        }

        public Builder setDefaultColor(int defaultColor) {
            this.defaultColor = defaultColor;
            return this;
        }
        public Builder setDefaultColorFrom(TextView tv) {
            this.defaultColor = tv.getCurrentTextColor();
            return this;
        }

        public Builder setHighLightColor(int highLightColor) {
            this.highLightColor = highLightColor;
            return this;
        }

        public HighLightTextHelper build() {
            return new HighLightTextHelper(this);
        }
    }
}
