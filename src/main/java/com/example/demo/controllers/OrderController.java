package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    public static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        log.info("Received request to submit order for username: {}", username);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        }

        log.info("User found with username: {}", username);

        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);

        log.info("Order successfully created and saved for user: {}", username);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        log.info("Received request to submit order for username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        }

        log.info("User found with username: {}", username);
        List<UserOrder> userOrderList = orderRepository.findByUser(user);
        if (userOrderList.isEmpty()) {
            log.warn("No orders found for user with username: {}", username);  // log cảnh báo nếu không tìm thấy đơn hàng
            return ResponseEntity.notFound().build();
        }
        log.info("Orders found for user with username: {}. Number of orders: {}", username, userOrderList.size());  // log thông tin nếu tìm thấy đơn hàng
        return ResponseEntity.ok(userOrderList);
    }
}
