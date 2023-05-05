package com.pranav.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pranav.spring.model.User;
import com.pranav.spring.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AuthControllerIntegrationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
        this.userRepository.deleteAll();
    }

    @Test
    public void create_one_user() throws Exception {
        final File jsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userToCreate = Files.readString(jsonFile.toPath());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(userToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(this.userRepository.findAll()).hasSize(1);

        User insertedUser = this.userRepository.findAll().get(0);
        User mockUser = new ObjectMapper().readValue(userToCreate, User.class);

        assertThat(insertedUser.getUsername()).isEqualTo(mockUser.getUsername());
        assertThat(insertedUser.getEmail()).isEqualTo(mockUser.getEmail());
        assertThat(insertedUser.getFirstName()).isEqualTo(mockUser.getFirstName());
        assertThat(insertedUser.getLastName()).isEqualTo(mockUser.getLastName());
    }

    @Test
    public void attempt_to_create_two_users_with_same_email() throws Exception {
        final File userJsonFile = new ClassPathResource("init/user1.json").getFile();
        final String userToCreate = Files.readString(userJsonFile.toPath());

        final File invalidUserEmailJsonFile = new ClassPathResource("init/invalid_user1_same_email.json").getFile();
        final String invalidUserEmailToCreate = Files.readString(invalidUserEmailJsonFile.toPath());

        final File invalidUserUsernameJsonFile = new ClassPathResource("init/invalid_user1_same_username.json").getFile();
        final String invalidUserUsernameToCreate = Files.readString(invalidUserUsernameJsonFile.toPath());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(userToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(invalidUserEmailToCreate))
                .andDo(print())
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(invalidUserUsernameToCreate))
                .andDo(print())
                .andExpect(status().isBadRequest());

        assertThat(this.userRepository.findAll()).hasSize(1);
    }

    @Test
    public void create_two_users() throws Exception {
        final File jsonFile1 = new ClassPathResource("init/user1.json").getFile();
        final String user1ToCreate = Files.readString(jsonFile1.toPath());

        final File jsonFile2 = new ClassPathResource("init/user2.json").getFile();
        final String user2ToCreate = Files.readString(jsonFile2.toPath());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(user1ToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(user2ToCreate))
                .andDo(print())
                .andExpect(status().isOk());

        assertThat(this.userRepository.findAll()).hasSize(2);

        User mockUser1 = new ObjectMapper().readValue(user1ToCreate, User.class);
        User mockUser2 = new ObjectMapper().readValue(user1ToCreate, User.class);

        assertThat(this.userRepository.findByUsername(mockUser1.getUsername()).isPresent()).isTrue();
        assertThat(this.userRepository.findByUsername(mockUser2.getUsername()).isPresent()).isTrue();
    }
    @Test
    public void verify_api_requires_authentication() throws Exception {
        this.mockMvc.perform(get("/api/user-info"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
