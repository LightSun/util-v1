package com.heaven7.core.util;

/**
 * the highlight helper
 * @author heaven7
 * @since 1.1.6
 */
public class HighLightHelper {

    private String rawText;
    private String highLightText;
    private int defaultColor;
    private int highLightColor;

    public CharSequence getText(){
        int i = rawText.indexOf(highLightText);
        if(i >= 0){
            if(i == 0){
                String suffix = rawText.substring(highLightText.length());
                return new StyledText().appendForeground(highLightText, highLightColor)
                        .appendForeground(suffix, defaultColor);
            }else{
                String prefix = rawText.substring(0, i);
                String suffix = rawText.substring(i + highLightText.length());
                return new StyledText()
                        .appendForeground(prefix, defaultColor)
                        .appendForeground(highLightText, highLightColor)
                        .appendForeground(suffix, defaultColor);
            }
        }else {
            return rawText;
        }
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

        public Builder setHighLightColor(int highLightColor) {
            this.highLightColor = highLightColor;
            return this;
        }

        public HighLightHelper build() {
            return new HighLightHelper(this);
        }
    }
}
