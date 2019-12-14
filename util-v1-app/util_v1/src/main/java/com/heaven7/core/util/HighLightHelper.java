package com.heaven7.core.util;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * @author heaven7
 */
public class HighLightHelper {

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
            while (i >= 0 && !TextUtils.isEmpty(leftText)){
                leftText = setText(st, leftText, i);
                i = leftText.indexOf(highLightText);
            }
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

    protected HighLightHelper(HighLightHelper.Builder builder) {
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

        public HighLightHelper build() {
            return new HighLightHelper(this);
        }
    }
}
