package com.example.twitterbe.api;

import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.GroupResponse;
import com.example.twitterbe.dto.UserInfoWithFollow;
import com.example.twitterbe.exception.InternalException;
import com.example.twitterbe.exception.NotFoundException;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
        UserInfoWithFollow user;
        try{
            user = userService.findUserByUID(customPrincipal.getUid(),customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<UserInfoWithFollow>(user, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String> addUser(@RequestBody User user){
        try{
            user.setCreateDate(new Date());
            userService.addUser(user);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Create a new user successful!", HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User user){
        try{
            User user1 = userService.findUser(user.getUid());
            user1.setBio(user.getBio());
            user1.setAvatarLink(user.getAvatarLink());
            user1.setWallLink(user.getWallLink());
            user1.setDisplayName(user.getDisplayName());
            System.out.println(user1.getId().toString());
            userService.addUser(user1);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Update successful!", HttpStatus.CREATED);
    }
    @GetMapping("/find/{s}")
    public ResponseEntity<List<User>> findUserByUsername(@PathVariable String s){
        List<User> res;
        try{
            res = userService.getListUsernameContainString(s);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        if(res.size()==0) {
            throw new NotFoundException("Couldn't found any user has name contain \"" + s + "\"");
        }
        return new ResponseEntity<List<User>>(res, HttpStatus.OK);
    }
    @GetMapping("/{uid}")
    public ResponseEntity<UserInfoWithFollow> getUserHasUid(@PathVariable String uid,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        UserInfoWithFollow res;
        try{
            res = userService.findUserByUID(uid, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        if(res == null) {
            throw new NotFoundException("Couldn't found any user has uid \"" + uid + "\"");
        }
        return new ResponseEntity<UserInfoWithFollow>(res, HttpStatus.OK);
    }
}
