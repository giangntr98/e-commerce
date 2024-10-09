package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    public static final Logger log = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        List<Item> items = itemRepository.findAll();

        if (items.isEmpty()) {
            log.warn("No items found in the repository.");
            return ResponseEntity.notFound().build();
        }

        log.info("Found {} items in the repository.", items.size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            log.warn("No item found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Item found with id: {}", id);
        return ResponseEntity.ok(item.get());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        List<Item> items = itemRepository.findByName(name);

        if (items == null || items.isEmpty()) {
            log.warn("No items found with name: {}", name);
            return ResponseEntity.notFound().build();
        }

        log.info("Found {} items with name: {}", items.size(), name);
        return ResponseEntity.ok(items);

    }

}
