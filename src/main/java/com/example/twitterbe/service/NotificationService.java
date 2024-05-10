package com.example.twitterbe.service;

import com.example.twitterbe.collection.Notification;
import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.NotifyWithTweet;
import com.example.twitterbe.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;
    private MongoTemplate mongoTemplate;
    private TweetService tweetService;
    @Autowired
    public NotificationService(NotificationRepository notificationRepository, MongoTemplate mongoTemplate, TweetService tweetService) {
        this.notificationRepository = notificationRepository;
        this.mongoTemplate = mongoTemplate;
        this.tweetService = tweetService;
    }

    public void notify(Notification notification){ // 1 all, 2 tweet owner
        notificationRepository.save(notification);
    }


    public List<NotifyWithTweet> getAllNotification(String currentUID){
        List<Notification> notificationList  = notificationRepository.findNotificationsByUsersNotifyContains(currentUID);
        return notificationList.stream().map(element->mapToNotifyWithTweet(element, currentUID)).toList();
    }

     public NotifyWithTweet mapToNotifyWithTweet(Notification notification, String currentUID){
        NotifyWithTweet notifyWithTweet = new NotifyWithTweet();
        Tweet tweet = tweetService.getTweetById(notification.getTweetId());
        notifyWithTweet.setTweet(tweetService.mapToTweetWithUserInfo(tweet, currentUID));
        if(tweet.getRepost()!=null){
            if (tweet.getRepost().equals(currentUID) && tweet.getContent().equals("")){
                notifyWithTweet.setType(2);
            }else {
                if(tweet.getRepost().equals(currentUID) && (!tweet.getImageLinks().isEmpty() || !tweet.getVideoLinks().isEmpty()) ){
                    notifyWithTweet.setType(1);
                }else {
                    notifyWithTweet.setType(3);
                }
            }
        }else {
            if(tweet.getReplyTo()!=null){
                if(tweet.getReplyTo().equals(currentUID)){
                    notifyWithTweet.setType(1);
                }
            }else {
                notifyWithTweet.setType(3);
            }
        }

        return notifyWithTweet;
     }
    

}
