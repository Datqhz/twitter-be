package com.example.twitterbe.dto;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.collection.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupResponse {
    private String groupIdAsString;
    private String groupName;
    private UserInfoWithFollow groupOwner;
    private List<String> rulesName;
    private List<String>  rulesContent;
    private Date createDate;
    private String review;
    private String groupImg;

    private List<UserInfoWithFollow> groupMembers;
    private Boolean isJoined;



    //    public void groupMapToResponse(Group group){
//        this.groupIdAsString = group.getGroupId().toString();
//        this.groupName = group.getGroupName();
//        this.groupOwner = group.getGroupOwner();
//        this.rulesName = group.getRulesName();
//        this.rulesContent = group.getRulesContent();
//        this.createDate = group.getCreateDate();
//        this.review = group.getReview();
//        this.groupImg = group.getGroupImg();
//        this.groupMembers = group.getGroupMembers();
//    }
}
