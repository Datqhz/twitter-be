package com.example.twitterbe.service;

import com.example.twitterbe.collection.Bookmark;
import com.example.twitterbe.collection.Tweet;
import com.example.twitterbe.dto.BookmarkWithTweet;
import com.example.twitterbe.repository.BookmarkRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    private MongoTemplate mongoTemplate;
    private BookmarkRepository bookmarkRepository;

    @Autowired
    public BookmarkService(MongoTemplate mongoTemplate, BookmarkRepository bookmarkRepository) {
        this.mongoTemplate = mongoTemplate;
        this.bookmarkRepository = bookmarkRepository;
    }

    public void save(Bookmark bookmark){
        bookmarkRepository.save(bookmark);
    }
    public void remove(String uid, String tweetId){
        Query query = new Query(Criteria.where("uid").is(uid).and("tweetId").is(tweetId));;
        Bookmark bookmark = mongoTemplate.findOne(query, Bookmark.class);
        assert bookmark != null;
        bookmarkRepository.delete(bookmark);
    }

    public boolean isBookmarkTweet(String tweetId, String uid){
        Query query = new Query(Criteria.where("uid").is(uid).and("tweetId").is(tweetId));;
        Bookmark bookmark = mongoTemplate.findOne(query, Bookmark.class);
        return bookmark != null;
    }
    public List<Bookmark> getListBookmarkOfUid(String uid){
        Query query = new Query(Criteria.where("uid").is(uid)).with(Sort.by(Sort.Direction.DESC, "dateBookmark"));
        return mongoTemplate.find(query, Bookmark.class);
    }
    public void clearAllBookmark(String uid){
        Query query = new Query(Criteria.where("uid").is(uid));
        mongoTemplate.remove(query, Bookmark.class);
    }
}
