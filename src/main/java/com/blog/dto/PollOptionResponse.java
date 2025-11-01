package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionResponse {

    private Long id;
    private String optionText;
    private String description;
    private Integer voteCount;
    private Double percentage;
}
