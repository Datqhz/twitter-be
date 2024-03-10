package com.example.twitterbe.api;

import com.example.twitterbe.collection.Like;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.LikeService;
import com.google.api.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/like")
@RestController
public class LikeController {

    private LikeService service;

    @Autowired
    public LikeController(LikeService service) {
        this.service = service;
    }

    @PostMapping()
    public ResponseEntity<String> likeTweet(@RequestBody Like like){
        service.likeTweet(like);
        return new ResponseEntity<String>("Like tweet success", HttpStatus.CREATED);
    }
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<String> deleteEmployee(@AuthenticationPrincipal CustomPrincipal customPrincipal, @PathVariable String tweetId) {
        service.unLikeTweet(tweetId, customPrincipal.getUid());
        System.out.println("unlike");
        return new ResponseEntity<String>("delete success!", HttpStatus.OK);
    }
}
