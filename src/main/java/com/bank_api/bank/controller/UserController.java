package com.bank_api.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bank_api.bank.service.UserService;


@Controller
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // TODO: implement
}
