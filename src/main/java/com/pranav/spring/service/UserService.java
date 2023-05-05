package com.pranav.spring.service;

import com.pranav.spring.exception.EmailAlreadyInUseException;
import com.pranav.spring.exception.UsernameAlreadyInUseException;
import com.pranav.spring.model.User;
import com.pranav.spring.repository.UserRepository;

/*
* Service class for carrying out user/authentication functions
* */
public class UserService {



    public UserService() {
    }

    public void signUpUser(
            UserRepository userRepository,
            String username,
            String email,
            String passwordEncrypted,
            String firstName,
            String lastName
    ) throws UsernameAlreadyInUseException, EmailAlreadyInUseException {
        if (userRepository.existsByUsername(username)) {throw new UsernameAlreadyInUseException(); }
        if (userRepository.existsByEmail(email)) {throw new EmailAlreadyInUseException(); }

        User user = new User(username, email, passwordEncrypted, firstName, lastName);

        userRepository.save(user);
    }

}
