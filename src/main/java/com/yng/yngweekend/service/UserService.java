package com.yng.yngweekend.service;

import com.yng.yngweekend.domain.User;
import com.yng.yngweekend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserService {
    UserRepository userRepository;
    @Autowired
    public UserService(final UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }
}
