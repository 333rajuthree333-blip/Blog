package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 10)
    private String content;

    private String excerpt;
    
    private String author;
    
    private List<String> tags;
    
    private String featuredImage;
    
    private Boolean published;
}
