package com.example.twitterbe.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Document(collection = "bookmarks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {
    private ObjectId id;
    private String uid;
    private String tweetId;
    private Date dateBookmark;

}
