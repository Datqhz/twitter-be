package com.example.twitterbe.service;

import com.example.twitterbe.collection.Group;
import com.example.twitterbe.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    private GroupRepository repository;

    @Autowired
    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public Group createGroup(Group group){
        return repository.insert(group);
    }
    public List<Group> getListGroup(){
        return repository.findAll();
    }
}
