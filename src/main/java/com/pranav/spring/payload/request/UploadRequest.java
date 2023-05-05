package com.pranav.spring.payload.request;

import javax.validation.constraints.NotBlank;

/*
 * Request format for image upload
 * */
public class UploadRequest {
    @NotBlank
    private String link;


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
