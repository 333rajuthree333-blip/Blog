package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollCreateRequest {

    private String question;
    private String description;
    private List<String> options;
    private Boolean allowMultipleVotes;
    private String expiresAt; // ISO date string
}
