package com.example.twitterbe.repository;

import com.example.twitterbe.collection.Follow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends MongoRepository<Follow, ObjectId> {

}
