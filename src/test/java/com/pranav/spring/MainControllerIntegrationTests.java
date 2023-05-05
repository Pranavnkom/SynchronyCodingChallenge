package com.pranav.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranav.spring.model.Image;
import com.pranav.spring.model.User;
import com.pranav.spring.repository.ImageRepository;
import com.pranav.spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MainControllerIntegrationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    private final String MOCK_USER_1_USERNAME = "pnk2000";
    private final String MOCK_USER_2_USERNAME = "jdoe10";

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        this.userRepository.deleteAll();
        this.imageRepository.deleteAll();

        final File jsonFile1 = new ClassPathResource("init/user1.json").getFile();
        final String user1ToCreate = Files.readString(jsonFile1.toPath());

        final File jsonFile2 = new ClassPathResource("init/user2.json").getFile();
        final String user2ToCreate = Files.readString(jsonFile2.toPath());

        final File userCredentialsJsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userCredentials = Files.readString(userCredentialsJsonFile.toPath());

        this.mockMvc.perform(post("/api/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(user1ToCreate));
        this.mockMvc.perform(post("/api/auth/signup")
                .contentType(APPLICATION_JSON)
                .content(user2ToCreate));

        this.mockMvc.perform(post("/api/auth/signin")
                .contentType(APPLICATION_JSON)
                .content(userCredentials));
    }

    @Test
    @WithMockUser(MOCK_USER_1_USERNAME)
    public void access_user_info() throws Exception {
        final File jsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userToCreate = Files.readString(jsonFile.toPath());
        User mockUser = new ObjectMapper().readValue(userToCreate, User.class);
        this.mockMvc.perform(get("/api/user-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", aMapWithSize(6)))
                .andExpect(jsonPath("$.firstName").value(mockUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(mockUser.getLastName()))
                .andExpect(jsonPath("$.username").value(mockUser.getUsername()))
                .andExpect(jsonPath("$.email").value(mockUser.getEmail()))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images").isEmpty());
    }

    /*
    *  Have two mock users separately upload images and verify that they can only see their images
    *  */
    @Test
    public void upload_images() throws Exception {
        final File user1JsonFile = new ClassPathResource("init/user1.json").getFile();
        final String user1ToCreate = Files.readString(user1JsonFile.toPath());

        User mockUser1 = new ObjectMapper().readValue(user1ToCreate, User.class);

        final File image1JsonFile = new ClassPathResource("init/image1_upload.json").getFile();
        final String image1ToCreate = Files.readString(image1JsonFile.toPath());

        this.mockMvc.perform(post("/api/upload")
                        .contentType(APPLICATION_JSON)
                        .content(image1ToCreate)
                        .with(user(MOCK_USER_1_USERNAME)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/user-info").with(user(MOCK_USER_1_USERNAME)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", aMapWithSize(6)))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images[0]")
                        .value(imageRepository.findByUsername(mockUser1.getUsername()).get(0).getId()));

        this.mockMvc.perform(post("/api/auth/signout"))
                .andDo(print())
                .andExpect(status().isOk());

        // Now have user 2 upload

        final File user2JsonFile = new ClassPathResource("init/user2.json").getFile();
        final String user2ToCreate = Files.readString(user2JsonFile.toPath());

        User mockUser2 = new ObjectMapper().readValue(user2ToCreate, User.class);

        final File user2CredentialsJsonFile = new ClassPathResource("init/user2.json").getFile();
        final String user2Credentials = Files.readString(user2CredentialsJsonFile.toPath());

        final File image2JsonFile = new ClassPathResource("init/image2_upload.json").getFile();
        final String image2ToCreate = Files.readString(image2JsonFile.toPath());


        this.mockMvc.perform(post("/api/auth/signin")
                        .contentType(APPLICATION_JSON)
                        .content(user2Credentials))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/api/upload")
                        .contentType(APPLICATION_JSON)
                        .content(image2ToCreate)
                        .with(user(MOCK_USER_2_USERNAME)))
                .andDo(print())
                .andExpect(status().isOk());


        this.mockMvc.perform(get("/api/user-info").with(user(MOCK_USER_2_USERNAME)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$", aMapWithSize(6)))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images[0]")
                        .value(imageRepository.findByUsername(mockUser2.getUsername()).get(0).getId()));
    }

    @Test
    @WithMockUser(MOCK_USER_1_USERNAME)
    public void upload_image_and_view_links() throws Exception {
        final File userJsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userToCreate = Files.readString(userJsonFile.toPath());

        User mockUser = new ObjectMapper().readValue(userToCreate, User.class);

        final File imageJsonFile = new ClassPathResource("init/image1_upload.json").getFile();
        final String imageToCreate = Files.readString(imageJsonFile.toPath());

        this.mockMvc.perform(post("/api/upload")
                        .contentType(APPLICATION_JSON)
                        .content(imageToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        Image mockImage = imageRepository.findByUsername(mockUser.getUsername()).get(0);

        this.mockMvc.perform(get("/api/view-image")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":\"" + mockImage.getId() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imgurImageLink")
                        .value("https://www.imgur.com/" + mockImage.getId()))
                .andExpect(jsonPath("$.originalImageLink").value(mockImage.getLink()));
    }

    @Test
    @WithMockUser(MOCK_USER_1_USERNAME)
    public void upload_image_and_delete_image() throws Exception {
        final File userJsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userToCreate = Files.readString(userJsonFile.toPath());

        User mockUser = new ObjectMapper().readValue(userToCreate, User.class);

        final File imageJsonFile = new ClassPathResource("init/image1_upload.json").getFile();
        final String imageToCreate = Files.readString(imageJsonFile.toPath());

        this.mockMvc.perform(post("/api/upload")
                        .contentType(APPLICATION_JSON)
                        .content(imageToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        Image mockImage = imageRepository.findByUsername(mockUser.getUsername()).get(0);

        this.mockMvc.perform(delete("/api/delete-image")
                        .contentType(APPLICATION_JSON)
                        .content("{\"id\":\"" + mockImage.getId() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/user-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images").isEmpty());

    }
}
