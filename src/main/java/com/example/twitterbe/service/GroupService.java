package com.example.twitterbe.service;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.GroupResponse;
import com.example.twitterbe.dto.UserInfoWithFollow;
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
    private UserService userService;
    private MongoTemplate mongoTemplate;

    @Autowired
    public GroupService(GroupRepository repository, MongoTemplate mongoTemplate, UserService userService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.userService = userService;
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
    public List<String> getAllGroupIdJoined(String uid){
        Query userQuery = new Query(Criteria.where("uid").is(uid));
        User user = mongoTemplate.findOne(userQuery, User.class);
        Query groupQuery = new Query(Criteria.where("groupMembers.$id").is(new ObjectId(user.getId().toString())));
        return mongoTemplate.find(groupQuery, Group.class).stream().map(
                (e)-> e.getGroupId().toString()
        ).toList();
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
    public void joinGroup(String groupId, String currentUid){
        Group group = findById(groupId);
        List<User> members = group.getGroupMembers();
        members.add(userService.findUser(currentUid));
        group.setGroupMembers( members);
        repository.save(group);
    }
    public void leaveGroup(String groupId, String currentUid){
        Group group = findById(groupId);
        List<User> members = group.getGroupMembers();
        members.remove(userService.findUser(currentUid));
        group.setGroupMembers(members);
        repository.save(group);
    }

    public List<UserInfoWithFollow> getAllMemberInGroup(String groupId, String currentUid){
        Group group = findById(groupId);
        List<User> members = group.getGroupMembers();
        return members.stream().map((e)->userService.mapToUserInfoWithFollow(e, currentUid)).toList();
    }
    public GroupResponse mapToGroupResponse(Group group, String currentUid){
        GroupResponse response = new GroupResponse();
        response.setGroupIdAsString(group.getGroupId().toString());
        response.setGroupName(group.getGroupName());
        response.setGroupImg(group.getGroupImg());
        response.setReview(group.getReview());
        response.setCreateDate(group.getCreateDate());
        response.setRulesContent(group.getRulesContent());
        response.setRulesName(group.getRulesName());
        response.setGroupOwner(userService.mapToUserInfoWithFollow(group.getGroupOwner(), currentUid));
        response.setGroupMembers(group.getGroupMembers().stream()
                .map((e)->userService.mapToUserInfoWithFollow(e, currentUid)).toList());
        response.setIsJoined(false);
        for (User user : group.getGroupMembers()){
            if (user.getUid().equals(currentUid)){
                response.setIsJoined(true);
            }
        }
        return response;
    }
}
