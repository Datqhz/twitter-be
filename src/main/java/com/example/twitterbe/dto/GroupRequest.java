package com.example.twitterbe.dto;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.List;

public class GroupRequest {
    private String groupName;
    private String groupOwnerId;
    private List<String> rulesName;
    private List<String>  rulesContent;
    private Date createDate;
    private String review;
    private String groupImg;
    private List<String> groupMemberIds;
    public Group mapToGroup(){
        Group rs = new Group();
        rs.setGroupImg(this.groupImg);
        rs.setGroupName(this.groupName);
        rs.setReview(this.review);
        rs.setCreateDate(this.createDate);
        rs.setRulesContent(this.rulesContent);
        rs.setRulesName(this.rulesName);
        return rs;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
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

    public List<String> getGroupMemberIds() {
        return groupMemberIds;
    }

    public void setGroupMemberIds(List<String> groupMemberIds) {
        this.groupMemberIds = groupMemberIds;
    }
}
