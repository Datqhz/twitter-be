package com.example.twitterbe.dto;

import com.example.twitterbe.collection.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoWithFollow {
    private User user;
    private long numOfFollowing;
    private long numOfFollowed;
    private boolean isFollow;
}
