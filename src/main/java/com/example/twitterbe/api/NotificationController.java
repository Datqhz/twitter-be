package com.example.twitterbe.api;

import com.example.twitterbe.dto.GroupResponse;
import com.example.twitterbe.dto.NotifyWithTweet;
import com.example.twitterbe.exception.InternalException;
import com.example.twitterbe.security.CustomPrincipal;
import com.example.twitterbe.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1/notification")
public class NotificationController {

    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotifyWithTweet>> getNotificationOfUID(@AuthenticationPrincipal CustomPrincipal customPrincip){
        List<NotifyWithTweet> data = new ArrayList<>();
        try{
            data = notificationService.getAllNotification(customPrincip.getUid());
        }catch (Exception e){
            throw new InternalException("Internal Server Error " + e.getMessage());
        }
        return new ResponseEntity<List<NotifyWithTweet>>(data, HttpStatus.OK);
    }

}
