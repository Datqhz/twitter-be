package com.example.twitterbe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkWithTweet {
    TweetWithUserInfo tweet;
    String uid;
    String idAsString; // id bookmark
}
