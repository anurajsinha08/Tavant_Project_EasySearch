package com.tavant.client.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tavant.client.model.Address;
import com.tavant.client.model.Authentication;
import com.tavant.client.payloads.ApiResponse;
import com.tavant.client.payloads.SignUpRequest;
import com.tavant.client.repository.AuthenticationRepository;
import com.tavant.client.service.AddressService;
import com.tavant.client.service.FileService;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ClientController {

	@Autowired
	AuthenticationRepository authenticationRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private Environment env;

	@Autowired
	AddressService addressService;

	@Autowired
	FileService storageService;

	@CrossOrigin("*")
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if (authenticationRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
		}

		if (authenticationRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"), HttpStatus.BAD_REQUEST);
		}

		Authentication authentication = new Authentication();
		authentication.setUsername(signUpRequest.getUsername());
		authentication.setEmail(signUpRequest.getEmail());
		authentication.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

		authenticationRepository.save(authentication);

		return ResponseEntity.status(HttpStatus.OK).body("Client registered successfully");
	}

	@GetMapping("/{user}")
	public ResponseEntity<?> getUserById(@PathVariable("user") String username) {

		Optional<Authentication> auth = authenticationRepository.findByUsername(username);

		if (auth.isPresent()) {
			return ResponseEntity.ok(auth.get());
		} else {
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found");
		}
		return null;
	}

	@CrossOrigin("*")
	@PostMapping("/add")
	public boolean addAddress(@RequestBody Address address) {

		System.out.println(address);
		return addressService.addAddress(address);
	}

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			storageService.store(file);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File Not Uploaded");
		}
		return null;
	}

}
