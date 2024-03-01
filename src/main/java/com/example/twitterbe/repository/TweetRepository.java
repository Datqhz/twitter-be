package com.example.twitterbe.repository;

import com.example.twitterbe.collection.Tweet;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends MongoRepository<Tweet, ObjectId> {

    @Query("{'uid':?0}")
    public List<Tweet> findTweetByUid(String uid);
}
