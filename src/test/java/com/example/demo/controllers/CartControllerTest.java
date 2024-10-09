package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Item item;
    private Cart cart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(9.99));

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);
    }

    @Test
    public void addToCart_UserNotFound_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistentUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void addToCart_ItemNotFound_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    public void addToCart_ValidRequest_AddsItemsToCart() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Cart> response = cartController.addTocart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, cart.getItems().size());
        assertEquals(item, cart.getItems().get(0));

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void removeFromCart_ValidRequest_RemovesItemsFromCart() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        cart.addItem(item); // Adding item to cart before testing remove

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, cart.getItems().size());

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void removeFromCart_UserNotFound_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistentUser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(null);

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void removeFromCart_ItemNotFound_ReturnsNotFound() {
        // Arrange
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(itemRepository, times(1)).findById(anyLong());
    }
}
