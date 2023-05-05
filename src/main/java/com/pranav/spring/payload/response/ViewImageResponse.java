package com.pranav.spring.payload.response;

/*
 * Response format for image viewing
 * */
public class ViewImageResponse {
    private String imgurImageLink;
    private String originalImageLink;

    private final String IMGUR_ADDRESS = "https://www.imgur.com/";

    public ViewImageResponse(String id, String link) {
        this.imgurImageLink = IMGUR_ADDRESS + id;
        this.originalImageLink = link;
    }

    public String getImgurImageLink() {return imgurImageLink;}

    public void setImgurImageLink(String id) {
        this.imgurImageLink = IMGUR_ADDRESS + id;
    }

    public String getOriginalImageLink() {return originalImageLink;}

    public void setOriginalImageLink(String link) {
        this.originalImageLink = link;
    }

}
