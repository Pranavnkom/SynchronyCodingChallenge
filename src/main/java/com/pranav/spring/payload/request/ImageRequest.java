package com.pranav.spring.payload.request;

import javax.validation.constraints.NotBlank;

/*
 * Request format for image viewing and deletion
 * */
public class ImageRequest {
    @NotBlank
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
