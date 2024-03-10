package com.example.twitterbe.service;

import com.example.twitterbe.collection.Comment;
import com.example.twitterbe.collection.Like;
import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;

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
    public List<TweetWithUserInfo> getTweetsOfListUID(List<String> uids, String currentUID){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("uid")
                .foreignField("uid")
                .as("user"); // Specify the attribute name in TweetWithUserInfo

        UnwindOperation unwindOperation = Aggregation.unwind("$user");

        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("uid").in(uids)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id")
                // Additional stages as needed
        );
        List<TweetWithUserInfo> result = mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
        result.forEach(element->{
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(countLike(element.getId().toString()));
            element.setLike(isLikeTweet(element.getId().toString(), currentUID));
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
    public boolean isLikeTweet(String tweetId, String uid){
        Query query = new Query(Criteria.where("tweetId").is(tweetId).and("uid").is(uid));
        List<Like> temp  = mongoTemplate.find(query, Like.class);
        return !temp.isEmpty();
    }
    //error query
    public List<TweetWithUserInfo> getTweetLikedByUID(String uid){
        MatchOperation matchOperation = Aggregation.match(Criteria.where("uid").is(uid));

        // Lookup operation để join tweet với like
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("likes")
                .localField("id")
                .foreignField("tweetId")
                .as("likes");

        // Match operation để lọc tweet có ít nhất một like
        MatchOperation matchTweetWithLikes = Aggregation.match(Criteria.where("likes").exists(true));

        // Projection operation để chọn ra các trường cần thiết
        ProjectionOperation projectOperation = Aggregation.project()
                .andExpression("id").as("id")
                .andExpression("content").as("content")
                .andExpression("uid").as("uid")
                .andExpression("imageLinks").as("imageLinks")
                .andExpression("videoLinks").as("videoLinks")
                .andExpression("uploadDate").as("uploadDate")
                .andExpression("personal").as("personal")
                .and("user").nested(bind("id", "user._id").and("username", "user.username").and("avatar", "user.avatar"))
                .andExclude("_id", "likes");

        // Sort operation để sắp xếp theo thời gian (desc)
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "uploadDate");

        // Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                matchTweetWithLikes,
                projectOperation,
                sortOperation
        );
        List<TweetWithUserInfo> result = mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
        result.forEach(element->{
            element.covertIdToString();
            List<User> users = mongoTemplate.find(new Query(Criteria.where("uid").is(element.getUid())), User.class);
            element.setUser(users.get(0));
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(countLike(element.getId().toString()));
            element.setLike(true);

        });
        return result;
    }
    public List<TweetWithUserInfo> getTweetsOfGroup(String groupId, String uid){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("users")
                .localField("uid")
                .foreignField("uid")
                .as("user"); // Specify the attribute name in TweetWithUserInfo

        UnwindOperation unwindOperation = Aggregation.unwind("$user");

        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("groupId").in(groupId)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                lookupOperation,
                unwindOperation,
                Aggregation.sort(Sort.Direction.DESC, "_id")
                // Additional stages as needed
        );
        List<TweetWithUserInfo> result = mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
        result.forEach(element->{
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(countLike(element.getId().toString()));
            element.setLike(isLikeTweet(element.getId().toString(), uid));
        });
        return result;
    }
}
