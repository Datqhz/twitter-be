package com.example.twitterbe.api;

import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.UserInfoWithFollow;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/user")
public class UserController {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<UserInfoWithFollow> getUser(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        return new ResponseEntity<UserInfoWithFollow>(userService.findUserByUID(customPrincipal.getUid()), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user){
        try{
            userService.addUser(user);
            return new ResponseEntity<String>("Create a new user successful!", HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<String>("Create a new user faild!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{s}")
    public ResponseEntity<List<User>> findUserByUsername(@PathVariable String s){
        System.out.println(s);
        return new ResponseEntity<List<User>> (userService.getListUsernameContainString(s), HttpStatus.OK);
    }
}
