package com.example.twitterbe.api;

import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.TweetService;
import com.example.twitterbe.service.UserService;
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

    @GetMapping("/{uid}")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetOfUID(@PathVariable String uid){
        return new ResponseEntity<List<TweetWithUserInfo>>(tweetService.getTweetsOfUserId(uid), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<TweetWithUserInfo>> getTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<String> uids = new ArrayList<>();
        uids.add(customPrincipal.getUid());
        List<TweetWithUserInfo> result = tweetService.getTweetsOfListUID(uids, customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        return new ResponseEntity<List<TweetWithUserInfo>>(result,
                HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String>postTweet(@RequestBody Tweet tweet,@AuthenticationPrincipal CustomPrincipal customPrincipal){
        tweetService.postTweet(tweet);
        return new ResponseEntity<String>("Create Tweet success",HttpStatus.CREATED);
    }
    @GetMapping("/for-you")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetForU(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<String> uids = new ArrayList<>();
        uids.add("23424dfdsf");
        uids.add("5XXwNwgzZEhumYgktNYCal5fbjG3");
        List<TweetWithUserInfo> result = tweetService.getTweetsOfListUID(uids, customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
    }
    //error
//    @GetMapping("/is-like/{uid}")
//    public ResponseEntity<List<TweetWithUserInfo>> getTweetUIDLike(String uid){
//        List<TweetWithUserInfo> result = tweetService.getTweetLikedByUID(uid);
//        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
//    }
    /// Tweet Group
    @GetMapping("/group/{id}")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetOfGroup(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        List<TweetWithUserInfo> result = tweetService.getTweetsOfGroup(id, customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
    }
    @GetMapping("/group")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetsOfGroupUserJoin(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<TweetWithUserInfo> result = tweetService.getTweetsOfGroupUserJoin( customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        System.out.println("tweet of group: " + result.size());
        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
    }

    //Like & unlike tweet
    @GetMapping("/like/{id}")
    public ResponseEntity<String> likeTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        tweetService.likeTweet(id, customPrincipal.getUid());
        return new ResponseEntity<String>("Like success", HttpStatus.OK);
    }
    @GetMapping("/unlike/{id}")
    public ResponseEntity<String> unlikeTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        tweetService.unlikeTweet(id, customPrincipal.getUid());
        return new ResponseEntity<String>("Unlike success", HttpStatus.OK);
    }

    // Get comment
    @GetMapping("/comment/{id}")
    public ResponseEntity<List<TweetWithUserInfo>> getCommentsOfTweet(@AuthenticationPrincipal CustomPrincipal customPrincipal,@PathVariable String id){
        List<TweetWithUserInfo> result = tweetService.getCommentsOfTweet(id,customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        return new ResponseEntity<List<TweetWithUserInfo>>(result,HttpStatus.OK);
    }
}
