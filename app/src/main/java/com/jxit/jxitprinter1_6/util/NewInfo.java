package com.jxit.jxitprinter1_6.util;

import java.util.ArrayList;

/**
 * 作者：XPZ on 2018/5/8 18:04.
 */
public class NewInfo {


    public ArrayList<String> getStyle() {
        if (style == null) {
            return new ArrayList<>();
        }
        return style;
    }

    public void setStyle(ArrayList<String> style) {
        this.style = style;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "NewInfo{" +
                "style=" + style +
                ", text='" + text + '\'' +
                '}';
    }

    private ArrayList<String> style;
   private String text;

}
