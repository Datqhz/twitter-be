package com.example.twitterbe.repository;

import com.example.twitterbe.collection.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    @Query("{'uid': ?0}")
    public User findUserByUID(String uid);
}
