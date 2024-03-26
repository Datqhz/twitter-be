package com.example.twitterbe.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "follows")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    @Id
    ObjectId id;
    private String userFollow;
    private String userFollowed;
    private boolean isNotify;
}
