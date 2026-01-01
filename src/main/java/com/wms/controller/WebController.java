package com.wms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/admin")
    public String adminPanel() {
        return "admin";
    }

    @GetMapping("/products")
    public String products() {
        return "products";
    }

    @GetMapping("/purchase-orders")
    public String purchaseOrders() {
        return "purchase-orders";
    }

    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory";
    }

    @GetMapping("/locations")
    public String locationsPage() {
        return "locations";
    }

}