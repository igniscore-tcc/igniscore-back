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
    public List<User> users() {
        return service.findAll();
    }

    @QueryMapping
    public User user(@Argument Integer id) {
        return service.findUserId(id);
    }

    @MutationMapping
    public User updateUserCompany(@Argument Integer id, @Argument Integer company) {
        return service.updateUserCompany(id, company);
    }
}
