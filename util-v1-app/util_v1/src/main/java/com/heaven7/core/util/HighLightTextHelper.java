package com.heaven7.core.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private boolean useRegular;

    protected HighLightTextHelper(HighLightTextHelper.Builder builder) {
        this.rawText = builder.rawText;
        this.highLightText = builder.highLightText;
        this.defaultColor = builder.defaultColor;
        this.highLightColor = builder.highLightColor;
        this.useRegular = builder.useRegular;
    }

    public CharSequence getText(){
        if(TextUtils.isEmpty(highLightText)){
            return rawText;
        }
        if(useRegular){
            return getTextByRegular();
        }else {
            return getSimpleText();
        }
    }

    private CharSequence getTextByRegular() {
        Matcher matcher = Pattern.compile(highLightText).matcher(rawText);
        if(matcher.find()){
            StyledText st = new StyledText();
            int lastEnd = 0;
            do {
                String text = matcher.group();
                int start = matcher.start();
                int end = matcher.end();
                if(start > lastEnd){
                    st.appendForeground(rawText.substring(lastEnd, start), defaultColor);
                }
                st.appendForeground(text, highLightColor);
                lastEnd = end;
            }while (matcher.find());
            if(lastEnd < rawText.length() - 1){
                st.appendForeground(rawText.substring(lastEnd + 1), defaultColor);
            }
            return st;
        }
        return rawText;
    }

    private CharSequence getSimpleText(){
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

    public boolean isUseRegular() {
        return this.useRegular;
    }

    public static class Builder {
        private String rawText;
        private String highLightText;
        private int defaultColor;
        private int highLightColor;
        private boolean useRegular;

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

        public Builder setHighLightColor(int highLightColor) {
            this.highLightColor = highLightColor;
            return this;
        }

        public Builder setUseRegular(boolean useRegular) {
            this.useRegular = useRegular;
            return this;
        }

        public HighLightTextHelper build() {
            return new HighLightTextHelper(this);
        }
    }
}
