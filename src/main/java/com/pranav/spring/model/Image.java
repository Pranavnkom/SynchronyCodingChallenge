package com.pranav.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * H2 table for `images`
 * with fields:
 * ID (varchar),
 * LINK (varchar),
 * USERNAME (varchar)
 * */
@Entity
@Table(name="images")
public class Image {

    @Id
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonProperty("id")
    public String id;
    @JsonProperty("link")
    public String link;
    public String username;

    public Image(String id, String link, String username) {
        this.id = id;
        this.link = link;
        this.username = username;
    }

    public Image() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "Images [id=" + id + ", link=" + link + ", username=" + username + "]";
    }


}
