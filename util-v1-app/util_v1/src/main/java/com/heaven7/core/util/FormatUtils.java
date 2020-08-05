package com.heaven7.core.util;

import java.text.NumberFormat;

/**
 * the format utils
 * @author heaven7
 * @since 1.1.8
 */
public final class FormatUtils {

    //keep fraction count
    public static String maxFractionCount(String val, int count) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(count);
        return nf.format(Float.parseFloat(val));
    }

    public static String trimSuffixZero(String text, String defVal){
        if(text == null){
            return defVal;
        }
        if (text.indexOf(".") > 0) {
            //replace suffix zero. $ as end
            text = text.replaceAll("0+?$", "");
            //replace suffix digital
            text = text.replaceAll("[.]$", "");
        }
        return text;
    }
}
