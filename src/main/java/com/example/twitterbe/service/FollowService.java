package com.example.twitterbe.service;

import com.example.twitterbe.collection.Follow;
import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.FollowResponse;
import com.example.twitterbe.repository.FollowRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class FollowService {
    MongoTemplate mongoTemplate;
    FollowRepository followRepository;
    UserService userService;

    @Autowired
    public FollowService(MongoTemplate mongoTemplate, FollowRepository followRepository, UserService userService) {
        this.mongoTemplate = mongoTemplate;
        this.followRepository = followRepository;
        this.userService = userService;
    }

    public List<FollowResponse> getListUserFollowed(String uid, String currentUid){ // Get the list of Follows that UID is following
        Query query = new Query(Criteria.where("userFollow").is(uid));
        List<Follow> follows = mongoTemplate.find(query, Follow.class);
        return follows.stream()
                .map(e->mapToFollowResponse(e, currentUid))
                .collect(Collectors.toList());
    }
    public List<FollowResponse> getListUserFollowing(String uid, String currentUid){ // Get a list of users who are follow the UID
        Query query = new Query(Criteria.where("userFollowed").is(uid));
        List<Follow> follows = mongoTemplate.find(query, Follow.class);
        return follows.stream()
                .map(e->mapToFollowResponse(e, currentUid))
                .collect(Collectors.toList());
    }
    public void followUser(Follow follow){
        followRepository.save(follow);
    }
    public void unfollowUser(String uid, String currentUid){
        Query query = new Query(Criteria.where("userFollowed").is(uid).and("userFollow").is(currentUid));
        mongoTemplate.remove(query, Follow.class);
    }
    public List<String> getListUidFollowed(String currentUid){
        Query query = new Query(Criteria.where("userFollow").is(currentUid));
        List<Follow> follows = mongoTemplate.find(query, Follow.class);
        return follows.stream()
                .map(Follow::getUserFollowed)
                .collect(Collectors.toList());
    }
    public List<String> getListUserFollowingNotify(String currentUid){ // Get a list of following users has isNotify true
        Query query = new Query(Criteria.where("userFollowed").is(currentUid).and("isNotify").is(true));
        List<Follow> follows = mongoTemplate.find(query, Follow.class);
        return follows.stream()
                .map(Follow::getUserFollow)
                .collect(Collectors.toList());
    }
    public boolean isFollowUserId(String uid, String currentUID){ // check current user is following user has uid
        Query query = new Query(Criteria.where("userFollow").is(currentUID).and("userFollowed").is(uid));
        Follow follows = mongoTemplate.findOne(query, Follow.class);
        return follows!=null;
    }
    private FollowResponse mapToFollowResponse(Follow follow, String currentUID){
        FollowResponse followResponse = new FollowResponse();
        followResponse.setUserFollow(userService.mapToUserInfoWithFollow(userService.findUser(follow.getUserFollow()), currentUID));
        followResponse.setUserFollowed(userService.mapToUserInfoWithFollow(userService.findUser(follow.getUserFollowed()), currentUID));
        followResponse.setNotify(follow.isNotify());
        followResponse.setIdAsString(follow.getId().toString());
        return followResponse;
    }
}
