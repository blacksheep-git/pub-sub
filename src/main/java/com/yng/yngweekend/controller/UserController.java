package com.yng.yngweekend.controller;

import com.yng.yngweekend.domain.User;
import com.yng.yngweekend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController{
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;
    public UserController(final UserService userService){
        this.userService = userService;
    }

    @GetMapping("")
    public String works(){
        return "works";
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        LOGGER.info("Request received for [/api/v1/users]");
        return userService.getUsers();
    }
}