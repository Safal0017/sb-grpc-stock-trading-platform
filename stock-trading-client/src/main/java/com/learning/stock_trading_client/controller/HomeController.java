package com.learning.stock_trading_client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(){
        return "index";
    }

    @GetMapping("/bulk-orders")  // Add this route
    public String bulkOrders() {
        return "bulk-order";  // Renders bulk-order.html
    }

    @GetMapping("/live-trading")
    public String liveTrading() {
        return "live-trading";
    }
}