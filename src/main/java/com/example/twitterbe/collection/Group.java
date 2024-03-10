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

@Document(collection = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    private ObjectId groupId;
    private String groupName;
    @DBRef
    private User groupOwner;
    private List<String> rulesName;
    private List<String>  rulesContent;
    private Date createDate;
    private String review;
    private String groupImg;
    @DBRef
    private List<User> groupMembers;
}
