package com.toyproject.instagram.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFeedReqDto {
    private String content;
    private List<MultipartFile> files;      //spring에서 파일을 받을 때, 파일이 여러개이므로 List
}
