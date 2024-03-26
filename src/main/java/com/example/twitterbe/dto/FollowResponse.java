package com.example.twitterbe.dto;

import com.example.twitterbe.collection.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowResponse {
    private String idAsString;
    private UserInfoWithFollow userFollow;
    private UserInfoWithFollow userFollowed;
    private boolean isNotify;
}
