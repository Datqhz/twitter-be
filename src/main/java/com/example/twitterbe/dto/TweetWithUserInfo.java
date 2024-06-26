package com.example.twitterbe.dto;

import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.collection.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TweetWithUserInfo {
    private String idAsString;
    private String content;
    private String uid;

    private List<String> imageLinks;
    private List<String> videoLinks;
    private Date uploadDate;
    private int personal;
    private UserInfoWithFollow userCreate;
    private long totalLike;
    private long totalComment;
    private long totalRepost;
    private long totalQuote;
    private long totalBookmark;
    private boolean isBookmark;
    private boolean isLike;
    private boolean isRepost;
    private String groupName;
    private UserInfoWithFollow replyToUser;
    private TweetWithUserInfo repostTweet;
    private String commentTweetId;

}
