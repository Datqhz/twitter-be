package com.example.twitterbe.service;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.repository.GroupRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class GroupService {

    private GroupRepository repository;
    private MongoTemplate mongoTemplate;

    @Autowired
    public GroupService(GroupRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Group createGroup(Group group){
        return repository.insert(group);
    }
    public List<Group> getListGroup(){
        return repository.findAll();
    }
    public List<Group> getGroupJoined(String uid){
        Query userQuery = new Query(Criteria.where("uid").is(uid));
        User user = mongoTemplate.findOne(userQuery, User.class);
        Query groupQuery = new Query(Criteria.where("groupMembers.$id").is(new ObjectId(user.getId().toString())));
        return mongoTemplate.find(groupQuery, Group.class);
    }
    public Group findById(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));
        return mongoTemplate.findOne(query, Group.class);
    }
    public List<Group> findGroupContain(String s){
        Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
        Criteria criteria = Criteria.where("groupName").regex(pattern);
        Query query = new Query(criteria);
        List<Group> groups = mongoTemplate.find(query, Group.class);
        return groups;
    }
}
