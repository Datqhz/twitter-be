package com.example.twitterbe.api;

import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.BookmarkWithTweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.exception.InternalException;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.TweetService;
import com.example.twitterbe.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

@RestController
@RequestMapping(value = "api/v1/tweet")
public class TweetController {

    TweetService tweetService;
    UserService userService;
    @Autowired
    public TweetController(TweetService tweetService, UserService userService) {
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetOfUID(@RequestParam String uid, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getTweetsOfUserId(uid, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<TweetWithUserInfo>> getRandomTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getRandomTweet(customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String>postTweet(@RequestBody Tweet tweet,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        try{
            tweetService.postTweet(tweet);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Create Tweet success",HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String>deleteTweet(@PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        try{
            tweetService.deleteTweet(id);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("delete Tweet success",HttpStatus.OK);
    }
    @PutMapping("/personal/{id}")
    public ResponseEntity<String>changePersonal(@RequestBody String personal, @PathVariable String id,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        try{
            Tweet tweet = tweetService.findTweetById(id);
            tweet.setPersonal(Integer.parseInt(personal));
            tweetService.updateTweet(tweet);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Update Tweet success",HttpStatus.OK);
    }
    @GetMapping("/for-you")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetForU(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getRandomTweet(customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping("/following")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetFollowingTab(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getTweetForFollowingTab(customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping("/user-like")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetUserLiked(@RequestParam String uid, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getLikedOfUid(uid, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping("/reply")
    public ResponseEntity<List<TweetWithUserInfo>> getReplyTweetOfUser(@RequestParam String uid,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getReplyTweetOfUid(uid, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping("/media")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetMedia(@RequestParam String uid,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getTweetMediaOfUid(uid, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    /// Tweet Group
    @GetMapping("/group/{id}")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetOfGroup(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getTweetsOfGroup(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }
    @GetMapping("/group")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetsOfGroupUserJoin(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getTweetsOfGroupUserJoin(customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data, HttpStatus.OK);
    }

    //Like & unlike tweet
    @GetMapping("/like/{id}")
    public ResponseEntity<String> likeTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        try{
            tweetService.likeTweet(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Like success", HttpStatus.OK);
    }
    @GetMapping("/unlike/{id}")
    public ResponseEntity<String> unlikeTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        try{
            tweetService.unlikeTweet(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Unlike success", HttpStatus.OK);
    }

    @GetMapping("/undo-repost/{id}")
    public ResponseEntity<String> undoRepost(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        try{
            tweetService.undoRepost(customPrincipal.getUid(), id);
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<String>("Undo repost success", HttpStatus.OK);
    }
    // Get comment
    @GetMapping("/comment/{id}")
    public ResponseEntity<List<TweetWithUserInfo>> getCommentsOfTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal,@PathVariable String id){
        List<TweetWithUserInfo> data;
        try{
            data = tweetService.getCommentsOfTweet(id, customPrincipal.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<TweetWithUserInfo>>(data,HttpStatus.OK);
    }
}
