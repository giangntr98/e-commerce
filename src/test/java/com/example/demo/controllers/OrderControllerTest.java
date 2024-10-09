package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void submitOrder_UserExists_ReturnsOrder() {
    // Arrange
    User user = new User();
    Cart cart = new Cart();

    // Setup items in cart
    cart.setItems(Collections.emptyList()); // Ensure items list is not null
    user.setCart(cart);

    when(userRepository.findByUsername("testuser")).thenReturn(user);

    // Act
    ResponseEntity<UserOrder> response = orderController.submit("testuser");

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    verify(orderRepository).save(any(UserOrder.class));
}

    @Test
    void submitOrder_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getOrdersForUser_UserExists_ReturnsOrderHistory() {
        // Arrange
        User user = new User();
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(new UserOrder()));

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getOrdersForUser_UserNotFound_ReturnsNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
