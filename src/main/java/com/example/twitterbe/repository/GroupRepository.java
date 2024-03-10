package com.example.twitterbe.repository;

import com.example.twitterbe.collection.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends MongoRepository<Group, ObjectId> {

}
