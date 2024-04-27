package com.example.twitterbe.api;

import com.example.twitterbe.collection.Bookmark;
import com.example.twitterbe.dto.BookmarkWithTweet;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.BookmarkService;
import com.example.twitterbe.service.TweetService;
import com.google.api.Http;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/v1/bookmark")
public class BookmarkController {

    TweetService tweetService;
    BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(TweetService tweetService, BookmarkService bookmarkService) {
        this.tweetService = tweetService;
        this.bookmarkService = bookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<BookmarkWithTweet>>getBookmarkCurrentUser(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        return new ResponseEntity<List<BookmarkWithTweet>>(tweetService.getBookmarkOfUid(customPrincipal.getUid()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> bookmarkTweet(@RequestBody Bookmark bookmark){
        bookmark.setId(new ObjectId());
        bookmark.setDateBookmark(new Date());
        bookmarkService.save(bookmark);
        return new ResponseEntity<String>("bookmark successful", HttpStatus.CREATED);
    }
    @DeleteMapping("/{tweetId}")
    public ResponseEntity<String> removeBookmark(@PathVariable String tweetId, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        bookmarkService.remove(customPrincipal.getUid(), tweetId);
        return new  ResponseEntity<String>("remove successful",HttpStatus.OK);
    }
    @GetMapping("/clear-all")
    public ResponseEntity<String> clearAllBookmark( @AuthenticationPrincipal CustomPrincipal customPrincipal){
        bookmarkService.clearAllBookmark(customPrincipal.getUid());
        return new  ResponseEntity<String>("remove all successful",HttpStatus.OK);
    }
}
