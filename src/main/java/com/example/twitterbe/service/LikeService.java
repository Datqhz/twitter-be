package com.example.twitterbe.service;

import com.example.twitterbe.collection.Like;
import com.example.twitterbe.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private LikeRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public LikeService(LikeRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public void unLikeTweet(String tweetId, String uid){
        Query query = new Query(Criteria.where("tweetId").is(tweetId).and("uid").is(uid));
        mongoTemplate.remove(query, Like.class);
    }

    public void likeTweet(Like like){
        repository.save(like);
    }
}
