package com.example.twitterbe.service;

import com.example.twitterbe.collection.Comment;
import com.example.twitterbe.collection.Like;
import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetService {
    private TweetRepository tweetRepository;

    private MongoTemplate mongoTemplate;
    @Autowired
    public TweetService(TweetRepository tweetRepository, MongoTemplate mongoTemplate) {
        this.tweetRepository = tweetRepository;
        this.mongoTemplate = mongoTemplate;
    }
    //Get all tweet created by user has uid
    public List<TweetWithUserInfo> getTweetsOfUserId(String uid){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("uid")
                .foreignField("uid")
                .as("user"); // Specify the attribute name in TweetWithUserInfo

        UnwindOperation unwindOperation = Aggregation.unwind("$user");

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("uid").is(uid)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation
                // Additional stages as needed
        );

        return mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
    }

    public void postTweet(Tweet tweet){
        tweetRepository.save(tweet);
    }

    // Get tweet create by list of user have uid in list
    public List<TweetWithUserInfo> getTweetsOfListUID(List<String> uids){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("uid")
                .foreignField("uid")
                .as("user"); // Specify the attribute name in TweetWithUserInfo

        UnwindOperation unwindOperation = Aggregation.unwind("$user");

        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("uid").in(uids)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation
                // Additional stages as needed
        );
        List<TweetWithUserInfo> result = mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
        result.forEach(element->{
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(countLike(element.getId().toString()));
        });
        return result;
    }
    public long countLike(String tweetId){
        Query query = new Query(Criteria.where("tweetId").is(tweetId));
        return mongoTemplate.count(query, Like.class);
    }
    public long countComment(String tweetId){
        Query query = new Query(Criteria.where("tweetId").is(tweetId));
        return mongoTemplate.count(query, Comment.class);
    }
}
