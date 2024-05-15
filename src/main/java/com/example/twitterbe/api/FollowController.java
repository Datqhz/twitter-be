package com.example.twitterbe.api;

import com.example.twitterbe.collection.Follow;
import com.example.twitterbe.dto.FollowResponse;
import com.example.twitterbe.exception.InternalException;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.FollowService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follow")
public class FollowController {

    private FollowService followService;

    @Autowired
    public FollowController(FollowService followService) {
        this.followService = followService;
    }

//    @GetMapping
//    public ResponseEntity<List<FollowResponse>> getListFollowed(@AuthenticationPrincipal CustomPrincipal customPrincipal){
//        return new ResponseEntity<List<FollowResponse>>(followService.getListUserFollowed(customPrincipal.getUid(), c), HttpStatus.OK);
//    }
    @PostMapping
    public ResponseEntity<String> followUser( @RequestBody Follow follow){
        try{
            follow.setNotify(true);
            followService.followUser(follow);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Create success", HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateFollow(@PathVariable String id, @AuthenticationPrincipal CustomPrincipal customPrincipal, @RequestBody Follow follow){
        follow.setId(new ObjectId(id));
        try{
            followService.followUser(follow);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Update success", HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> unfollow(@PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        try{
            followService.unfollowUser(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Delete success", HttpStatus.OK);
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<FollowResponse> getFollowInfo(@PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        FollowResponse data = new FollowResponse();
        try{
            data = followService.getFollowInfo(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        System.out.println("data: " + data);
        return new ResponseEntity<FollowResponse>(data, HttpStatus.OK);
    }
    @GetMapping("/following/{id}") //Get the list of Follows that UID is following
    public ResponseEntity<List<FollowResponse>> getListFollowingOfUserId(@PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<FollowResponse> data;
        try{
            data = followService.getListFollowing(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<FollowResponse>>(data, HttpStatus.OK);
    }
    @GetMapping("/followers/{id}") //Get a list of users who are follow the UID
    public ResponseEntity<List<FollowResponse>> getListFollowedOfUserId(@PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<FollowResponse> data;
        try{
            data = followService.getListFollower(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<FollowResponse>>(data, HttpStatus.OK);
    }

}
