package com.example.twitterbe.service;

import com.example.twitterbe.collection.Follow;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.UserInfoWithFollow;
import com.example.twitterbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class UserService {
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    @Autowired
    public UserService(UserRepository userRepository,MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public UserInfoWithFollow findUserByUID(String uid, String currentUID){
        UserInfoWithFollow user = new UserInfoWithFollow();
        user.setUser(userRepository.findUserByUID(uid));
        user.setNumOfFollowing(countFollowing(uid));
        user.setNumOfFollowers(countFollowers(uid));
        user.setFollow(isFollowUserId(uid, currentUID));
        return user;
    }
    public void addUser(User user){
        userRepository.save(user);
    }
    //count of user following user has uid is "id"
    public long countFollowing(String id){
        Query query = new Query(Criteria.where("userFollow").is(id));
        return mongoTemplate.count(query, Follow.class);
    }
    //count of user who followed by user has id
    public long countFollowers(String id){
        Query query = new Query(Criteria.where("userFollowed").is(id));
        return mongoTemplate.count(query, Follow.class);
    }

    public List<User> getListUsernameContainString(String s){
        Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
        Criteria criteria = Criteria.where("username").regex(pattern);
        Query query = new Query(criteria);
        List<User> users = mongoTemplate.find(query, User.class);
        System.out.println(users.isEmpty());
        return users;
    }
    public User findUser(String uid){
        return userRepository.findUserByUID(uid);
    }

    public boolean isFollowUserId(String uid, String currentUID){ // check current user is following user has uid
        Query query = new Query(Criteria.where("userFollow").is(currentUID).and("userFollowed").is(uid));
        Follow follows = mongoTemplate.findOne(query, Follow.class);
        return follows!=null;
    }
    public UserInfoWithFollow mapToUserInfoWithFollow(User user, String currentUID){
        UserInfoWithFollow userInfoWithFollow = new UserInfoWithFollow();
        userInfoWithFollow.setUser(user);
        userInfoWithFollow.setNumOfFollowing(countFollowing(user.getUid()));
        userInfoWithFollow.setNumOfFollowers(countFollowers(user.getUid()));
        userInfoWithFollow.setFollow(isFollowUserId(user.getUid(), currentUID));
        return userInfoWithFollow;
    }
}
