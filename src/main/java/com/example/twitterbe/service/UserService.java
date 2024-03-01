package com.example.twitterbe.service;

import com.example.twitterbe.collection.Comment;
import com.example.twitterbe.collection.Follow;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.UserInfoWithFollow;
import com.example.twitterbe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    @Autowired
    public UserService(UserRepository userRepository,MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public UserInfoWithFollow findUserByUID(String uid){
        UserInfoWithFollow user = new UserInfoWithFollow();
        user.setUser(userRepository.findUserByUID(uid));
        user.setNumOfFollowing(countFollowing(uid));
        user.setNumOfFollowed(countFollowed(uid));
        return user;
    }
    public void addUser(User user){
        userRepository.save(user);
    }
    //count of user following user has uid is "id"
    public long countFollowing(String id){
        Query query = new Query(Criteria.where("followedId").is(id));
        return mongoTemplate.count(query, Follow.class);
    }
    //count of user who followed by user has id
    public long countFollowed(String id){
        Query query = new Query(Criteria.where("followingId").is(id));
        return mongoTemplate.count(query, Follow.class);
    }
}
