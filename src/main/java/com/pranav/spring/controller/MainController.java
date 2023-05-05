package com.pranav.spring.controller;

import com.pranav.spring.exception.InvalidImageIdException;
import com.pranav.spring.model.Image;
import com.pranav.spring.model.User;
import com.pranav.spring.payload.request.ImageRequest;
import com.pranav.spring.payload.request.UploadRequest;
import com.pranav.spring.payload.response.MessageResponse;
import com.pranav.spring.payload.response.UserInfoResponse;
import com.pranav.spring.payload.response.ViewImageResponse;
import com.pranav.spring.repository.ImageRepository;
import com.pranav.spring.repository.UserRepository;
import com.pranav.spring.security.jwt.JwtUtils;
import com.pranav.spring.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
 * Entry point for image upload,viewing, and deletion API
 * */
@RestController
@RequestMapping("/api")
public class MainController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ImageRepository imageRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	ImageService imageService = new ImageService();

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@PostMapping("/upload")
	public ResponseEntity<?> uploadImage(@Valid @RequestBody UploadRequest uploadRequest) {
		try {
			Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
			imageService.uploadImage(imageRepository, uploadRequest.getLink(), loggedInUser.getName());
			logger.info("Image with link=" + uploadRequest.getLink() + " uploaded");
			return ResponseEntity.ok().body(new MessageResponse("Image Uploaded!"));
		} catch (IOException e) {
			logger.error("Failed uploading image with link=" + uploadRequest.getLink() + ".\n" + e.getMessage());
			return ResponseEntity.internalServerError().body(new MessageResponse("Upload Failed: " + e.getMessage()));
		}
	}

	@GetMapping("/user-info")
	public ResponseEntity<?> getUserInfo() {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		List<String> imageIds = imageService.getImages(imageRepository, loggedInUser.getName());

		Optional<User> user = this.userRepository.findByUsername(loggedInUser.getName());

		if (user.isEmpty()) return  ResponseEntity.badRequest().body(new MessageResponse("Can't find user"));

		return ResponseEntity.ok().body(new UserInfoResponse(
				user.get().getId(),
				user.get().getFirstName(),
				user.get().getLastName(),
				user.get().getUsername(),
				user.get().getEmail(),
				imageIds
		));
	}

	@GetMapping("/view-image")
	public ResponseEntity<?> viewImage(@Valid @RequestBody ImageRequest viewImageRequest) {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
		try {
			Image image = imageService.viewImage(imageRepository, loggedInUser.getName(), viewImageRequest.getId());
			return ResponseEntity.ok().body(new ViewImageResponse(image.getId(), image.getLink()));
		} catch(InvalidImageIdException e) {
			logger.error("Invalid image id (" + viewImageRequest.getId() + ") entered for view-image request");
			return ResponseEntity.internalServerError().body(new MessageResponse("No such image"));
		}
	}

	@DeleteMapping("/delete-image")
	public ResponseEntity<?> deleteImage(@Valid @RequestBody ImageRequest deleteImageRequest) {
		Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();

		try {
			imageService.deleteImage(imageRepository, loggedInUser.getName(), deleteImageRequest.getId());
			logger.error("Deleted image with id=" + deleteImageRequest.getId());
			return ResponseEntity.ok().body(new MessageResponse("Image " + deleteImageRequest.getId() + " deleted!"));
		} catch (IOException e) {
			logger.error("Failed deleting image with id=" + deleteImageRequest.getId());
			return ResponseEntity.internalServerError().body(new MessageResponse("Deletion Failed: " + e.getMessage()));
		} catch (InvalidImageIdException e) {
			logger.error("Invalid image id (" + deleteImageRequest.getId() + ") entered for delete-image request");
			return ResponseEntity.internalServerError().body(new MessageResponse("No such image"));
		}
	}


}
