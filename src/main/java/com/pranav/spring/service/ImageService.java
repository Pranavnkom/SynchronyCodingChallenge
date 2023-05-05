package com.pranav.spring.service;

import com.pranav.spring.exception.InvalidImageIdException;
import com.pranav.spring.model.Image;
import com.pranav.spring.repository.ImageRepository;
import com.pranav.spring.utility.UploadResponseHandler;
import com.pranav.spring.payload.response.UploadResponse;
import com.pranav.spring.utility.URLToBase64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
 * Service class for leveraging the imgur API for viewing, upload, and deletion
 * */
public class ImageService {
    private static final String IMGUR_CLIENT_ID = "5ef125d6d68bbd9";
    private static final String IMGUR_BEARER     = "7913ff1225c7f1b870dac63dc900b49ec1d69f45";
    public static final String IMGUR_URL = "https://api.imgur.com/3/image";

    private final URLToBase64 encoder = new URLToBase64();

    @Autowired
    AuthenticationManager authenticationManager;

    public ImageService() {
    }

    public void uploadImage(ImageRepository imageRepository, String imageLink, String username) throws IOException {

        String base64String = encoder.getBase64String(imageLink);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPostRequest = new HttpPost(IMGUR_URL);
        httpPostRequest.setHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
        httpPostRequest.setHeader("Authorization", "Bearer " + IMGUR_BEARER);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("image", base64String));
        UploadResponseHandler responseHandler = new UploadResponseHandler();
        httpPostRequest.setEntity(new UrlEncodedFormEntity(params));
        UploadResponse responseBody = (UploadResponse) httpClient.execute(httpPostRequest, responseHandler);
        Image image = new Image(responseBody.getId(), imageLink, username);
        httpClient.close();

        imageRepository.save(image);


    }

    public void deleteImage(ImageRepository imageRepository, String username, String imageId) throws IOException, InvalidImageIdException {
        Optional<Image> image = imageRepository.findByUsernameAndId(username, imageId);
        if (!image.isPresent()) throw new InvalidImageIdException();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete httpDeleteRequest = new HttpDelete(IMGUR_URL + "/" + imageId);
        httpDeleteRequest.setHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
        httpDeleteRequest.setHeader("Authorization", "Bearer " + IMGUR_BEARER);

        httpClient.execute(httpDeleteRequest);

        imageRepository.delete(image.get());
    }

    public Image viewImage(ImageRepository imageRepository, String username, String imageId) throws InvalidImageIdException {
        Optional<Image> image = imageRepository.findByUsernameAndId(username, imageId);
        if (!image.isPresent()) throw new InvalidImageIdException();
        return image.get();
    }

    public List<String> getImages(ImageRepository imageRepository, String username) {
        List<Image> images = imageRepository.findByUsername(username);
        ArrayList<String> imageIds = new ArrayList<>();
        for (Image image: images) {
            imageIds.add(image.getId());
        }
        return imageIds;
    }
}

