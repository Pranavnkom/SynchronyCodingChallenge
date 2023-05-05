package com.pranav.spring.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pranav.spring.model.Image;

/*
 * JPA Repository interface for interacting with the images table in H2
 * */
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUsername(String username);

    Optional<Image> findByUsernameAndId(String username, String id);
}
