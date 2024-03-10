package com.example.twitterbe.api;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.GroupRequest;
import com.example.twitterbe.dto.GroupResponse;
import com.example.twitterbe.service.GroupService;
import com.example.twitterbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/group")
public class GroupController {

    private GroupService groupService;
    private UserService userService;

    @Autowired
    public GroupController(GroupService service, UserService userService) {
        this.groupService = service;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAll(){
        List<GroupResponse> responses = new ArrayList<>();
        groupService.getListGroup().forEach(element->{
            GroupResponse temp = new GroupResponse();
            temp.groupMapToResponse(element);
            responses.add(temp);
        });
        return new ResponseEntity<List<GroupResponse>>(responses,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupRequest group){
        Group newGroup = group.mapToGroup();
        List<User> members = new ArrayList<>();
        group.getGroupMemberIds().forEach(element->{
            members.add(userService.findUser(element));
        });
        User owner = userService.findUser(group.getGroupOwnerId());
        newGroup.setGroupMembers(members);
        newGroup.setGroupOwner(owner);
        return new ResponseEntity<Group>(groupService.createGroup(newGroup),HttpStatus.OK);
    }
}
