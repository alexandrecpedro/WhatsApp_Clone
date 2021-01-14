package com.murilofb.wppclone.helpers;

import android.util.Base64;

public class Base64H {
    public static String encode(String originalText) {
        return Base64.encodeToString(originalText.getBytes(), Base64.DEFAULT);
    }

    public static String decode(String encodedText) {
        return new String((Base64.decode(encodedText, Base64.DEFAULT)));
    }
}
