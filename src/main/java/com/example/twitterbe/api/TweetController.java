package com.example.twitterbe.api;

import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/v1/tweet")
public class TweetController {

    TweetService tweetService;
    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
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
    public ResponseEntity<String>postTweet(@RequestBody Tweet tweet){
        System.out.println(tweet);
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
    @GetMapping("/is-like/{uid}")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetUIDLike(String uid){
        List<TweetWithUserInfo> result = tweetService.getTweetLikedByUID(uid);
        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
    }
    @GetMapping("/group/{id}")
    public ResponseEntity<List<TweetWithUserInfo>> getTweetOfGroup(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String id){
        List<TweetWithUserInfo> result = tweetService.getTweetsOfGroup(id, customPrincipal.getUid());
        result.forEach(TweetWithUserInfo::covertIdToString);
        return new ResponseEntity<List<TweetWithUserInfo>>(result, HttpStatus.OK);
    }
}
