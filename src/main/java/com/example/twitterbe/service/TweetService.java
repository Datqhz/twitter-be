package com.example.twitterbe.service;

import com.example.twitterbe.collection.*;
import com.example.twitterbe.dto.BookmarkWithTweet;
import com.example.twitterbe.dto.TweetWithUserInfo;
import com.example.twitterbe.repository.GroupRepository;
import com.example.twitterbe.repository.TweetRepository;
import com.example.twitterbe.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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

    //    private NotificationService notificationService;


    //Get all tweet created by user has uid
    public List<TweetWithUserInfo> getTweetsOfUserId(String uid, String currentUid){
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

        List<TweetWithUserInfo> result = mongoTemplate.aggregate(aggregation, "tweets", TweetWithUserInfo.class).getMappedResults();
        System.out.println("num tweet: " + result.size());
        result.forEach(element->{
            Tweet tweet = getTweetById(element.getId().toString());
            element.setIdAsString(element.getId().toString());
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(tweet.getUsersLike().size());
            element.setLike(tweet.getUsersLike().contains(currentUid));
            element.setUserCreate(userService.mapToUserInfoWithFollow(element.getUser(), currentUid));
            if(tweet.getRepost() !=null){
                Tweet repost = getTweetById(tweet.getRepost());
                element.setRepostTweet(mapToTweetWithUserInfo(repost, userService.findUser(repost.getUid()), currentUid));
            }
            element.setRepost(isRepostTweet(currentUid, tweet.getId().toString()));
            element.setTotalRepost(countRepost(tweet.getId().toString()));
            if(tweet.getReplyTo()!=null){
                element.setReplyToUser(userService.mapToUserInfoWithFollow(userService.findUser(tweet.getReplyTo()), currentUid));
            }
            element.setTotalQuote(countQuote(tweet.getId().toString()));
            element.setTotalBookmark(countBookmark(tweet.getId().toString()));
            element.setBookmark(bookmarkService.isBookmarkTweet(tweet.getId().toString(), currentUid));
        });
        return result;
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
            Tweet tweet = getTweetById(element.getId().toString());
            element.setIdAsString(element.getId().toString());
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(tweet.getUsersLike().size());
            element.setLike(tweet.getUsersLike().contains(currentUID));
            element.setUserCreate(userService.mapToUserInfoWithFollow(element.getUser(), currentUID));
            if(tweet.getRepost() !=null){
                Tweet repost = getTweetById(tweet.getRepost());
                element.setRepostTweet(mapToTweetWithUserInfo(repost, userService.findUser(repost.getUid()), currentUID));
            }
            element.setRepost(isRepostTweet(currentUID, tweet.getId().toString()));
            element.setTotalRepost(countRepost(tweet.getId().toString()));
            if(tweet.getReplyTo()!=null){
                element.setReplyToUser(userService.mapToUserInfoWithFollow(userService.findUser(tweet.getReplyTo()), currentUID));
            }
            element.setTotalQuote(countQuote(tweet.getId().toString()));
            element.setTotalBookmark(countBookmark(tweet.getId().toString()));
            element.setBookmark(bookmarkService.isBookmarkTweet(tweet.getId().toString(), currentUID));
        });
        return result;
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
            Tweet tweet = getTweetById(element.getId().toString());
            element.setTotalComment(countComment(element.getId().toString()));
            element.setTotalLike(tweet.getUsersLike().size());
            element.setLike(tweet.getUsersLike().contains(uid));
            element.setUserCreate(userService.mapToUserInfoWithFollow(element.getUser(), uid));
            if(tweet.getRepost() !=null){
                Tweet repost = getTweetById(tweet.getRepost());
                element.setRepostTweet(mapToTweetWithUserInfo(repost, userService.findUser(tweet.getUid()), uid));
            }
            element.setRepost(isRepostTweet(uid, tweet.getId().toString()));
            element.setTotalRepost(countRepost(tweet.getId().toString()));
            if(tweet.getReplyTo()!=null){
                element.setReplyToUser(userService.mapToUserInfoWithFollow(userService.findUser(tweet.getReplyTo()), uid));
            }
        });
        return result;
    }
    public List<TweetWithUserInfo> getTweetsOfGroupUserJoin(String uid){

        List<Group> userGroups = groupService.getGroupJoined(uid);
        System.out.println(userGroups.size());

        // Extract group IDs
        List<String> groupIds  = new ArrayList<>();
        userGroups.forEach(element->{
            groupIds.add(element.getGroupId().toString());
        });

        // Find tweets where groupId is in the list of user's group memberships
        Query tweetQuery = new Query(Criteria.where("groupId").in(groupIds)).with(Sort.by(Sort.Order.desc("uploadDate")));;
        List<Tweet> tweets = mongoTemplate.find(tweetQuery, Tweet.class);

        // Map tweets to TweetWithUserInfo DTOs
        return tweets.stream()
                .map(tweet -> mapToTweetWithUserInfo(tweet, userService.findUser(tweet.getUid()), uid))
                .collect(Collectors.toList());
    }

    public TweetWithUserInfo mapToTweetWithUserInfo(Tweet tweet, User user, String currentUID) {
        TweetWithUserInfo tweetDto = new TweetWithUserInfo();
        tweetDto.setId(tweet.getId());
        tweetDto.covertIdToString(); // Assuming this method converts ObjectId to String
        tweetDto.setContent(tweet.getContent());
        tweetDto.setUid(tweet.getUid());
        tweetDto.setImageLinks(tweet.getImageLinks());
        tweetDto.setVideoLinks(tweet.getVideoLinks());
        tweetDto.setUploadDate(tweet.getUploadDate());
        tweetDto.setPersonal(tweet.getPersonal());
        tweetDto.setUser(user); // Set the user create information
        tweetDto.setUserCreate(userService.mapToUserInfoWithFollow(user, currentUID));
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
            tweetDto.setRepostTweet(mapToTweetWithUserInfo(repost, userService.findUser(repost.getUid()), currentUID));
        }
        tweetDto.setRepost(isRepostTweet(currentUID, tweet.getId().toString()));
        tweetDto.setTotalRepost(countRepost(tweet.getId().toString()));
        tweetDto.setCommentTweetId(tweet.getCommentTweetId());
        tweetDto.setTotalLike(tweet.getUsersLike().size());
        tweetDto.setTotalQuote(countQuote(tweet.getId().toString()));
        tweetDto.setTotalBookmark(countBookmark(tweet.getId().toString()));
        tweetDto.setBookmark(bookmarkService.isBookmarkTweet(tweet.getId().toString(), currentUID));
        // Set other properties as needed
        return tweetDto;
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
                .map(tweet -> mapToTweetWithUserInfo(tweet, userService.findUser(tweet.getUid()), uid))
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
            temp.setTweet(mapToTweetWithUserInfo(tweet, userService.findUser(tweet.getUid()), uid));
            rs.add(temp);
        }
        return rs;
    }

}
