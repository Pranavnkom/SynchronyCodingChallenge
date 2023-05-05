package com.pranav.spring.utility;

import com.pranav.spring.payload.response.UploadResponse;
import org.apache.commons.io.Charsets;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/*
 * Utility for extracting relevant details from Imgur's Upload API response
 * */
public class UploadResponseHandler implements ResponseHandler {

    @Override
    public UploadResponse handleResponse(final HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        UploadResponse rspObject = new UploadResponse();
        rspObject.setStatusCode(status);

        if (status >= 200 && status < 300) {
            HttpEntity entity = response.getEntity();
            Header headerEncoding = response.getEntity().getContentEncoding();

            Charset enocodedCharset = (headerEncoding == null) ? StandardCharsets.UTF_8 : Charsets.toCharset(headerEncoding.toString());
            String jsonResponse = EntityUtils.toString(entity, enocodedCharset);
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject dataObject = (JSONObject) jsonObject.get("data");
            rspObject.setLink((String) dataObject.get("link"));
            rspObject.setId((String) dataObject.get("id"));
        }

        return rspObject;
    }
}
