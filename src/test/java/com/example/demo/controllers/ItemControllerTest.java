package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Item 1");
        item.setPrice(BigDecimal.valueOf(9.99));
    }

    @Test
    public void getItems_ReturnsListOfItems() {
        // Arrange
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item));

        // Act
        ResponseEntity<List<Item>> response = itemController.getItems();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(item.getName(), response.getBody().get(0).getName());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void getItemById_ItemExists_ReturnsItem() {
        // Arrange
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        // Act
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(item.getName(), response.getBody().getName());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getItemById_ItemNotFound_ReturnsNotFound() {
        // Arrange
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Item> response = itemController.getItemById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    public void getItemsByName_ItemExists_ReturnsListOfItems() {
        // Arrange
        when(itemRepository.findByName("Item 1")).thenReturn(Arrays.asList(item));

        // Act
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item 1");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(item.getName(), response.getBody().get(0).getName());

        verify(itemRepository, times(1)).findByName("Item 1");
    }

    @Test
    public void getItemsByName_ItemNotFound_ReturnsNotFound() {
        // Arrange
        when(itemRepository.findByName("NonExistingItem")).thenReturn(null);

        // Act
        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistingItem");

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        verify(itemRepository, times(1)).findByName("NonExistingItem");
    }

}
