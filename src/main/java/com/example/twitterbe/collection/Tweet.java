package com.example.twitterbe.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "tweets")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tweet {
    @Id
    private ObjectId id;
    private String content;
    private String uid;
    private List<String> imageLinks;
    private List<String> videoLinks;
    private Date uploadDate;
    private String groupId;
    private int personal; // 1  is public for all, 2 is for follower, 3 is private
    private String replyTo;
    private String repost;
    private String commentTweetId;
    private List<String> usersLike;
}
