package com.igniscore.api.controller;

import com.igniscore.api.model.User;
import com.igniscore.api.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @QueryMapping
    public List<User> users(){
        return service.findAll();
    }

    @MutationMapping
    public User createUser(@Argument String name,
                           @Argument String email,
                           @Argument String password) {
        return service.create(name, email, password);
    }

}
