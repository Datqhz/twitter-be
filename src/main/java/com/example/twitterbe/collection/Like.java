package com.example.twitterbe.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "likes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Id
    ObjectId id;
    String tweetId;
    String uid;

}
