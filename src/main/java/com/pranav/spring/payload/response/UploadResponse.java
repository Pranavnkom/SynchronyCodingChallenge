package com.pranav.spring.payload.response;

/*
 * Response format for image upload
 * */
public class UploadResponse {
    private String link;
    private String id;
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (statusCode >= 200 && statusCode < 300) {
            return "ResponseObject{" +
                    "link='" + link + '\'' +
                    ", id='" + id + '\'' +
                    ", status='" + statusCode + '\'' +
                    '}';
        } else {
            return "ResponseObject{" +
                    ", status='" + statusCode + '\'' +
                    '}';
        }
    }
}
