package com.pranav.spring.utility;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

/*
 * Utility class for converting image download links into base64 for use with Imgur's Upload API
 * */
public class URLToBase64 {
    byte[] byteData;
    String base64;

    public String getBase64String (String link) throws IOException {
        InputStream in = new URL(link).openStream();
        byteData = IOUtils.toByteArray(in);
        base64 = Base64.getEncoder().encodeToString(byteData);

        return base64;
    }
}