package com.example.twitterbe.api;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import com.example.twitterbe.dto.GroupRequest;
import com.example.twitterbe.dto.GroupResponse;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.GroupService;
import com.example.twitterbe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.GeoNearOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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
    public ResponseEntity<List<GroupResponse>> getAll(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<GroupResponse> responses = new ArrayList<>();
        groupService.getListGroup().forEach(element->{
            responses.add(groupService.mapToGroupResponse(element, customPrincipal.getUid()));
        });
        return new ResponseEntity<List<GroupResponse>>(responses,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupRequest group){
        Group newGroup = group.mapToGroup();
        newGroup.setCreateDate(new Date());
        List<User> members = new ArrayList<>();
        group.getGroupMemberIds().forEach(element->{
            members.add(userService.findUser(element));
        });
        User owner = userService.findUser(group.getGroupOwnerId());
        newGroup.setGroupMembers(members);
        newGroup.setGroupOwner(owner);
        return new ResponseEntity<Group>(groupService.createGroup(newGroup),HttpStatus.CREATED);
    }
    @PutMapping("/join")
    public ResponseEntity<String> joinGroup(@RequestBody String groupId, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        groupService.joinGroup(groupId, customPrincipal.getUid());
        return new ResponseEntity<String>("join group successful", HttpStatus.OK);
    }
    @PutMapping("/leave")
    public ResponseEntity<String> leaveGroup(@RequestBody String groupId, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        groupService.leaveGroup(groupId, customPrincipal.getUid());
        return new ResponseEntity<String>("leave group successful", HttpStatus.OK);
    }


    // find all group joined
    @GetMapping("/joined")
    public ResponseEntity<List<GroupResponse>> getJoined(@AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<GroupResponse> responses = new ArrayList<>();
        groupService.getGroupJoined(customPrincipal.getUid()).forEach(element->{
            responses.add(groupService.mapToGroupResponse(element, customPrincipal.getUid()));
        });
        return new ResponseEntity<List<GroupResponse>>(responses,HttpStatus.OK);
    }
    @GetMapping("/find")
    public ResponseEntity<List<GroupResponse>> findGroupRegex(@RequestParam String regex, @AuthenticationPrincipal CustomPrincipal customPrincipal){
        List<GroupResponse> responses = new ArrayList<>();
        System.out.println("regex: " + regex);
        groupService.findGroupContain(regex).forEach(element->{
            responses.add(groupService.mapToGroupResponse(element, customPrincipal.getUid()));
        });
        return new ResponseEntity<List<GroupResponse>>(responses,HttpStatus.OK);
    }
}
