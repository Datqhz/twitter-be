package com.example.twitterbe.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private ObjectId id;
    private String uid;
    private Date createDate;
    private String bio;
    private String displayName;
    private String username;
    private String avatarLink;
    private String wallLink;
    private String phoneNumber;
    private String email;
}
