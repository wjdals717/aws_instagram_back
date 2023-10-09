package com.toyproject.instagram.controller;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import com.toyproject.instagram.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @PostMapping("/feed")
    public ResponseEntity<?> uploadFeed(UploadFeedReqDto uploadFeedReqDto) { //JSON 아니라서 RequestBody붙이면 안됨, 현재 formData로 받음
        feedService.upload(uploadFeedReqDto);
        return ResponseEntity.ok().body(null);
    }
}
