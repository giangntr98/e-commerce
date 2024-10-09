package com.example.demo.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testFindById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testFindByUserName_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("testuser");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testFindByUserName_NotFound() {
        when(userRepository.findByUsername("unknownuser")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("unknownuser");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateUser_Success() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("testpassword");
        createUserRequest.setConfirmPassword("testpassword");

        when(bCryptPasswordEncoder.encode("testpassword")).thenReturn("encodedPassword");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("encodedPassword", user.getPassword());
        verify(cartRepository).save(any(Cart.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testCreateUser_PasswordTooShort() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("short");
        createUserRequest.setConfirmPassword("short");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUser_PasswordMismatch() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password1");
        createUserRequest.setConfirmPassword("password2");

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
