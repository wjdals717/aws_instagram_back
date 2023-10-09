package com.toyproject.instagram.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedImg {
    private int feedImgId;
    private int feedId;
    private String originFileName;
    private String saveFileName;
    private List<FeedImg> feedImgList;
}
