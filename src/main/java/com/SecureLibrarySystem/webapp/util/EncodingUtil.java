package com.SecureLibrarySystem.webapp.util;

import java.util.Base64;

public class EncodingUtil {

    public static String encode(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decode(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
