package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollVoteRequest {

    private Long optionId;
    private String voterIdentifier; // Optional, will be generated from IP if not provided
}
