package com.hmittag.gpssilence.objects;

import java.util.ArrayList;
import java.util.List;

public class AdvancedString {
    ////fields
    private String mString;
    ////constructor
    public AdvancedString() {
        this.mString = null;
    }
    public AdvancedString(String s) {
        this.mString = s;
    }
    ////methods
    //getter setter
    public void setString(String s) {
        this.mString = s;
    }
    public String getString()   {
        return this.mString;
    }
    //slice
    public String[] slice(char c) {
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < this.mString.length(); i++) {
            if (this.mString.charAt(i) == c)    {
                indexs.add(i);
            }
        }

        if (!indexs.isEmpty()) {
            String[] ret;
            if (indexs.get(indexs.size()-1) < (this.mString.length()-1))    {
                ret = new String[indexs.size()+1];
                int last_char = -1;
                for (int i = 0; i < indexs.size(); i++) {
                    ret[i] = this.mString.substring(last_char+1, indexs.get(i));
                    last_char = indexs.get(i);
                }
                ret[ret.length-1] = this.mString.substring(last_char+1);
            }
            else {
                ret = new String[indexs.size()];
                int last_char = -1;
                for (int i = 0; i < indexs.size(); i++) {
                    ret[i] = this.mString.substring(last_char+1, indexs.get(i));
                    last_char = indexs.get(i);
                }
            }
            return ret;
        }
        return null;
    }

    ////functions
    public static String[] slice(String in, char c)   {
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == c)    {
                indexs.add(i);
            }
        }

        if (!indexs.isEmpty()) {
            String[] ret;
            if (indexs.get(indexs.size()-1) < (in.length()-1))    {
                ret = new String[indexs.size()+1];
                int last_char = -1;
                for (int i = 0; i < indexs.size(); i++) {
                    ret[i] = in.substring(last_char+1, indexs.get(i));
                    last_char = indexs.get(i);
                }
                ret[ret.length-1] = in.substring(last_char+1);
            }
            else {
                ret = new String[indexs.size()];
                int last_char = -1;
                for (int i = 0; i < indexs.size(); i++) {
                    ret[i] = in.substring(last_char+1, indexs.get(i));
                    last_char = indexs.get(i);
                }
            }
            return ret;
        }
        return null;
    }
}
