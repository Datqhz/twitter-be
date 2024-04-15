package com.example.twitterbe.dto;

import com.example.twitterbe.collection.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotifyWithTweet {
    TweetWithUserInfo tweet;
    int type; // 1 reply, quote. 2 reposted. 3 new post
}
