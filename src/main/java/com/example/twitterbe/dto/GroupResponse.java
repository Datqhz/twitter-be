package com.example.twitterbe.dto;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.List;

public class GroupResponse {
    private String groupIdAsString;
    private String groupName;
    private User groupOwner;
    private List<String> rulesName;
    private List<String>  rulesContent;
    private Date createDate;
    private String review;
    private String groupImg;

    private List<User> groupMembers;

    public String getGroupIdAsString() {
        return groupIdAsString;
    }

    public void setGroupIdAsString(String groupIdAsString) {
        this.groupIdAsString = groupIdAsString;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public User getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(User groupOwner) {
        this.groupOwner = groupOwner;
    }

    public List<String> getRulesName() {
        return rulesName;
    }

    public void setRulesName(List<String> rulesName) {
        this.rulesName = rulesName;
    }

    public List<String> getRulesContent() {
        return rulesContent;
    }

    public void setRulesContent(List<String> rulesContent) {
        this.rulesContent = rulesContent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public GroupResponse() {
    }

    public GroupResponse(String groupIdAsString, String groupName, User groupOwner, List<String> rulesName, List<String> rulesContent, Date createDate, String review, String groupImg, List<User> groupMembers) {
        this.groupIdAsString = groupIdAsString;
        this.groupName = groupName;
        this.groupOwner = groupOwner;
        this.rulesName = rulesName;
        this.rulesContent = rulesContent;
        this.createDate = createDate;
        this.review = review;
        this.groupImg = groupImg;
        this.groupMembers = groupMembers;
    }

    public void groupMapToResponse(Group group){
        this.groupIdAsString = group.getGroupId().toString();
        this.groupName = group.getGroupName();
        this.groupOwner = group.getGroupOwner();
        this.rulesName = group.getRulesName();
        this.rulesContent = group.getRulesContent();
        this.createDate = group.getCreateDate();
        this.review = group.getReview();
        this.groupImg = group.getGroupImg();
        this.groupMembers = group.getGroupMembers();
    }
}
