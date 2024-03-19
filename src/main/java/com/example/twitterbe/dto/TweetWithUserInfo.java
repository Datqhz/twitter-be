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
    private ObjectId id;
    private String idAsString;
    private String content;
    private String uid;

    private List<String> imageLinks;
    private List<String> videoLinks;
    private Date uploadDate;
    private int personal;
    private User user;// create
    private long totalLike;
    private long totalComment;
    private boolean isLike;
    private String groupName;
    private User replyToUser;
    private Tweet repostTweet;
    private String commentTweetId;

    public void covertIdToString(){
        idAsString = id.toString();
    }
}
