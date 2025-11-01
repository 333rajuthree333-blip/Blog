package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollResponse {

    private Long id;
    private String question;
    private String description;
    private Boolean active;
    private Boolean allowMultipleVotes;
    private Integer totalVotes;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean hasExpired;
    private Boolean isAvailable;
    private List<PollOptionResponse> options;
    private Boolean userHasVoted;
    private Integer userSelectedOptionId;
}
