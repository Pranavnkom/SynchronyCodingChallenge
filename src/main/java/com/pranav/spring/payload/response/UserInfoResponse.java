package com.pranav.spring.payload.response;

import java.util.List;

/*
 * Response format for account information endpoint
 * */
public class UserInfoResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private List<String> images;

    public UserInfoResponse(
            Long id,
            String firstName,
            String lastName,
            String username,
            String email,
            List<String> images
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.images = images;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}
