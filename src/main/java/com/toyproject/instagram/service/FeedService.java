package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import com.toyproject.instagram.entity.Feed;
import com.toyproject.instagram.entity.FeedImg;
import com.toyproject.instagram.repository.FeedMapper;
import com.toyproject.instagram.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${file.path}")      //yml에서 참조해서 가져옴(공통요소)
    private String filePath;

    private final FeedMapper feedMapper;

    @Transactional(rollbackFor = Exception.class)            //DB에 저장 실패했을 경우 rollback 해주기 위함, 모든 예외가 일어나면 rollback해라, 메소드가 끝나야 트랜젝션이 commit됨
    public void upload(UploadFeedReqDto uploadFeedReqDto) {
        String content = uploadFeedReqDto.getContent();
        List<FeedImg> feedImgList = new ArrayList<>();
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  //현재 자료형 Object
        String username = principalUser.getUsername();

        Feed feed = Feed.builder()
                .content(content)
                .username(username)
                .build();

        feedMapper.saveFeed(feed);

        uploadFeedReqDto.getFiles().forEach(file -> {
            String originName = file.getOriginalFilename();
            String extensionName = originName.substring(originName.lastIndexOf("."));   //"."부터 자르기 -> 확장자명 가져오기 위함
            String saveName = UUID.randomUUID().toString().replaceAll("-","").concat(extensionName); //UUID : 랜덤하게 키값을 생성해 줌
            // 파일의 이미지는 절대 겹칠 수 없도록 함, 타인이 프로필을 조회했을 때 덮어쓰지 않도록...

            Path uploadPath = Paths.get(filePath + "/feed/" + saveName);

            File f = new File(filePath + "/feed");
            if(!f.exists()){    //업로드 하기 전 f의 경로가 존재하지 않을 경우
                f.mkdirs();     //make directorys : 폴더 생성
            }

            try {
                Files.write(uploadPath, file.getBytes());   //file의 byte 데이터들을 uploadPath에 copy
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            feedImgList.add(FeedImg.builder()
                            .feedId(feed.getFeedId())
                            .originFileName(originName)
                            .saveFileName(saveName)
                            .build());
        });

        feedMapper.saveFeedImgList(feedImgList);

    }
}
