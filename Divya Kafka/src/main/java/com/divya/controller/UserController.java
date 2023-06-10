package com.divya.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divya.config.JwtUtils;
import com.divya.model.User;
import com.divya.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtUtils jwtUtils;

  @PostMapping("/register")
  public String registerUser(@RequestBody User user) {
    userRepository.save(
      User.builder().id(user.getId()).phone(user.getPhone()).password(encoder.encode(user.getPassword())).build());
    return "user " + user.getPhone() + " saved sucessfully";
  }

  // generate token
  @PostMapping("/login")
  public Map<String, String> generateToken(@RequestBody User user) {
    Map<String, String> token = new HashMap<>();

    User u = userRepository.findByPhone(user.getPhone()).get();

    if (Objects.isNull(u)) {
      log.error("No user found");
      throw new BadCredentialsException("No user found");
    }
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    if (!encoder.matches(user.getPassword(), u.getPassword())) {
      log.error("In correct password");
      throw new BadCredentialsException("In correct password");
    }

    token.put("token", jwtUtils.generateJwtToken(user));
    return token;
  }

  @GetMapping("/details")
  public List<User> getUsers() {
    return userRepository.findAll();
  }

}
