package com.example.twitterbe.repository;

import com.example.twitterbe.collection.Notification;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, ObjectId> {
    List<Notification> findNotificationsByUsersNotifyContains(String uid);
}
