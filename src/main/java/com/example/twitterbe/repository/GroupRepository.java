package com.example.twitterbe.repository;

import com.example.twitterbe.collection.Group;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends MongoRepository<Group, ObjectId> {

    // Use the @Query annotation to define your query
//    @Query("{ 'groupMembers': { $elemMatch: { 'uid': ?0 } } }")
//    List<Group> findAllGroupsByMemberId(String uid);
}
