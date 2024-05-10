package com.example.twitterbe.service;

import com.example.twitterbe.collection.*;
import com.example.twitterbe.dto.BookmarkWithTweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.repository.GroupRepository;
import com.example.twitterbe.repository.TweetRepository;
import com.example.twitterbe.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;

@Service
public class TweetService {
    private TweetRepository tweetRepository;
    private GroupService groupService;
    private UserService userService;
    private MongoTemplate mongoTemplate;
    private FollowService followService;
    private BookmarkService bookmarkService;

    @Autowired
    public TweetService(TweetRepository tweetRepository, GroupService groupService, UserService userService, MongoTemplate mongoTemplate, FollowService followService, BookmarkService bookmarkService) {
        this.tweetRepository = tweetRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.mongoTemplate = mongoTemplate;
        this.followService = followService;
        this.bookmarkService = bookmarkService;
    }

    //Get all tweet created by user has uid
    public List<TweetWithUserInfo> getTweetsOfUserId(String uid, String currentUid){
        Query query;
        if (uid.equals(currentUid)) {
            query = Query.query(Criteria.where("uid").is(uid)).with(Sort.by(Sort.Order.desc("uploadDate")));
        }else {
            query = Query.query(Criteria.where("uid").is(uid).and("personal").is(1)).with(Sort.by(Sort.Order.desc("uploadDate")));
        }
        return mongoTemplate.find(query, Tweet.class).stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }

    public void postTweet(Tweet tweet){
        tweet.setUploadDate(new Date());
        tweet.setId(new ObjectId());
        tweetRepository.save(tweet);
        if(tweet.getReplyTo()!=null && !tweet.getReplyTo().equals(tweet.getUid())) {
            Tweet reply = getTweetById(tweet.getCommentTweetId());
            if(followService.isFollowUserIdAndTurnOnNotify(tweet.getUid(),reply.getUid())){
                Notification notification = new Notification();
                notification.setNotifyFrom(tweet.getUid());
                notification.setNotifyDate(new Date());
                notification.setTweetId(tweet.getId().toString());
                List<String> userNotifys = new ArrayList<>();
                userNotifys.add(reply.getUid());
                notification.setUsersNotify(userNotifys);
                mongoTemplate.insert(notification);
//                notificationService.notify(notification);
            }
        }else {
            Notification notification = new Notification();
            notification.setNotifyDate(new Date());
            notification.setNotifyFrom(tweet.getUid());
            notification.setTweetId(tweet.getId().toString());
            notification.setUsersNotify(followService.getListUserFollowingTurnOnNotify(tweet.getUid()));
            mongoTemplate.insert(notification);
//            notificationService.notify(notification);
        }
    }
    public void updateTweet(Tweet tweet){
        tweetRepository.save(tweet);
    }
    public void deleteTweet(String tweetId){
        Query query = new Query(Criteria.where("repost").is(tweetId).and("content").is(""));
        List<Tweet> reposts = mongoTemplate.find(query, Tweet.class);
        tweetRepository.delete(findTweetById(tweetId));
        tweetRepository.deleteAll(reposts);
    }

    public Tweet findTweetById(String tweetId){
        return tweetRepository.findTweetById(new ObjectId(tweetId));
    }

    public List<TweetWithUserInfo> getTweetForFollowingTab(String currentUid){
        List<String> userFollowing = followService.getListUidFollowed(currentUid);
        List<String> groupIds = groupService.getAllGroupIdJoined(currentUid);
        Criteria criteria = new Criteria();
        criteria.orOperator(
                Criteria.where("uid").in(userFollowing),
                Criteria.where("uid").is(currentUid),
                Criteria.where("groupId").in(groupIds)
        ).and("replyTo").is(null).and("personal").is(1);
        Query query = new Query(criteria);
        List<Tweet> tweets = mongoTemplate.find(query.with(Sort.by(Sort.Order.desc("uploadDate"))), Tweet.class);
        return tweets.stream()
                .map(tweet -> mapToTweetWithUserInfo(tweet,currentUid))
                .collect(Collectors.toList());
    }
    public List<TweetWithUserInfo> getRandomTweet(String currentUid){
        SampleOperation matchStage = Aggregation.sample(10);
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("replyTo").is(null).and("groupId").is("").and("personal").is(1)
        );
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                matchStage
                );
        return mongoTemplate.aggregate(aggregation, "tweets", Tweet.class).getMappedResults().stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }

    public List<TweetWithUserInfo> getReplyTweetOfUid(String uid, String currentUid){
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("replyTo").ne(null).and("personal").is(1).and("uid").is(uid)
        );
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation
        );
        return mongoTemplate.aggregate(aggregation, "tweets", Tweet.class).getMappedResults().stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }
    public List<TweetWithUserInfo> getTweetMediaOfUid(String uid, String currentUid){
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("imageLinks").not().size(0).and("personal").is(1).and("uid").is(uid)
        );
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation
        );
        return mongoTemplate.aggregate(aggregation, "tweets", Tweet.class).getMappedResults().stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }
    public List<TweetWithUserInfo> getLikedOfUid(String uid, String currentUid){
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("usersLike").is(uid).and("personal").is(1)
        );
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation
        );
        return mongoTemplate.aggregate(aggregation, "tweets", Tweet.class).getMappedResults().stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }


    public Tweet getTweetById(String tweetId){
        Query query = new Query(Criteria.where("_id").is(tweetId));
        return mongoTemplate.findOne(query, Tweet.class);
    }
    public long countComment(String tweetId){
        Query query = new Query(Criteria.where("commentTweetId").is(tweetId));
        return mongoTemplate.count(query, Tweet.class);
    }
    public long countRepost(String tweetId){
        Query query = new Query(Criteria.where("repost").is(tweetId)
                .and("content").ne("")
                .and("imageLinks").size(0)
                .and("videoLinks").size(0));
        return mongoTemplate.count(query, Tweet.class);
    }
    public long countQuote(String tweetId){
        Query query = new Query();
        query.addCriteria(
                Criteria.where("repost").is(tweetId).orOperator(
                        Criteria.where("content").ne(""),
                        Criteria.where("imageLinks").ne(Collections.emptyList()),
                        Criteria.where("videoLinks").ne(Collections.emptyList())
                )
        );
        return mongoTemplate.count(query, Tweet.class);
    }
    public long countBookmark(String tweetId){
        Query query = new Query(Criteria.where("tweetId").is(tweetId));
        return mongoTemplate.count(query, Bookmark.class);
    }

    public List<TweetWithUserInfo> getTweetsOfGroup(String groupId, String currentUid){
        Query query = Query.query(Criteria.where("groupId").is(groupId)).with(Sort.by(Sort.Order.desc("uploadDate")));
        return mongoTemplate.find(query, Tweet.class).stream().map(
                (e)-> mapToTweetWithUserInfo(e, currentUid)
        ).toList();
    }
    public List<TweetWithUserInfo> getTweetsOfGroupUserJoin(String uid){
        // Extract group IDs
        List<String> groupIds  = groupService.getAllGroupIdJoined(uid);
        // Find tweets where groupId is in the list of user's group memberships
        Query tweetQuery = new Query(Criteria.where("groupId").in(groupIds)).with(Sort.by(Sort.Order.desc("uploadDate")));
        List<Tweet> tweets = mongoTemplate.find(tweetQuery, Tweet.class);
        // Map tweets to TweetWithUserInfo DTOs
        return tweets.stream()
                .map(tweet -> mapToTweetWithUserInfo(tweet, uid))
                .collect(Collectors.toList());
    }

    //Like tweet and unlike
    public void likeTweet(String tweetId, String uid){
        Query query = Query.query(Criteria.where("_id").is(tweetId));
        Update update = new Update().push("usersLike", uid);
        mongoTemplate.updateFirst(query, update, Tweet.class);
    }
    public void unlikeTweet(String tweetId, String uid){
        Query query = Query.query(Criteria.where("_id").is(tweetId));
        Update update = new Update().pull("usersLike", uid);
        mongoTemplate.updateFirst(query, update, Tweet.class);
    }
    public void undoRepost(String currentUID, String repostId){
        Query query = Query.query(Criteria.where("repost").is(repostId).and("content").is("").and("uid").is(currentUID));
        mongoTemplate.remove(query, Tweet.class);
    }
    public boolean isRepostTweet(String curretUID, String repostId){
        Query query = new Query(Criteria.where("uid").is(curretUID).and("content").is("").and("repost").is(repostId));
        Tweet tweet = mongoTemplate.findOne(query, Tweet.class);
        return tweet!=null;
    }
    //Get comment
    public List<TweetWithUserInfo> getCommentsOfTweet(String tweetId, String uid){
        Query query = Query.query(Criteria.where("commentTweetId").is(tweetId));
        List<Tweet> tweets = mongoTemplate.find(query, Tweet.class);
        return tweets.stream()
                .map(tweet -> mapToTweetWithUserInfo(tweet, uid))
                .collect(Collectors.toList());
    }

    public List<BookmarkWithTweet> getBookmarkOfUid(String uid){
        List<Bookmark> bookmarks = bookmarkService.getListBookmarkOfUid(uid);
        List<BookmarkWithTweet> rs = new ArrayList<>();
        for(Bookmark bookmark : bookmarks){
            BookmarkWithTweet temp = new BookmarkWithTweet();
            temp.setUid(bookmark.getUid());
            temp.setIdAsString(bookmark.getId().toString());
            Tweet tweet = getTweetById(bookmark.getTweetId());
            temp.setTweet(mapToTweetWithUserInfo(tweet, uid));
            rs.add(temp);
        }
        return rs;
    }

    public TweetWithUserInfo mapToTweetWithUserInfo(Tweet tweet,  String currentUID) {
        TweetWithUserInfo tweetDto = new TweetWithUserInfo();
        tweetDto.setIdAsString(tweet.getId().toString()); // Assuming this method converts ObjectId to String
        tweetDto.setContent(tweet.getContent());
        tweetDto.setUid(tweet.getUid());
        tweetDto.setImageLinks(tweet.getImageLinks());
        tweetDto.setVideoLinks(tweet.getVideoLinks());
        tweetDto.setUploadDate(tweet.getUploadDate());
        tweetDto.setPersonal(tweet.getPersonal());
        tweetDto.setUserCreate(userService.mapToUserInfoWithFollow(userService.findUser(tweet.getUid()), currentUID));
        tweetDto.setLike(tweet.getUsersLike().contains(currentUID));
        if(!tweet.getGroupId().isEmpty()){
            Group temp = groupService.findById(tweet.getGroupId());
            tweetDto.setGroupName(temp.getGroupName());
        }
        if(tweet.getReplyTo() != null){
            tweetDto.setReplyToUser(userService.mapToUserInfoWithFollow(userService.findUser(tweet.getReplyTo()), currentUID));
        }
        if(tweet.getRepost()!= null){
            Tweet repost = getTweetById(tweet.getRepost());
            tweetDto.setRepostTweet(mapToTweetWithUserInfo(repost, currentUID));
        }
        tweetDto.setRepost(isRepostTweet(currentUID, tweet.getId().toString()));
        tweetDto.setTotalRepost(countRepost(tweet.getId().toString()));
        tweetDto.setCommentTweetId(tweet.getCommentTweetId());
        tweetDto.setTotalLike(tweet.getUsersLike().size());
        tweetDto.setTotalComment(countComment(tweet.getId().toString()));
        tweetDto.setTotalQuote(countQuote(tweet.getId().toString()));
        tweetDto.setTotalBookmark(countBookmark(tweet.getId().toString()));
        tweetDto.setBookmark(bookmarkService.isBookmarkTweet(tweet.getId().toString(), currentUID));
        // Set other properties as needed
        return tweetDto;
    }

}
