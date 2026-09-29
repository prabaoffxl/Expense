package com.expensejar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping({"/", "/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "forward:/index.html";
    }
}
