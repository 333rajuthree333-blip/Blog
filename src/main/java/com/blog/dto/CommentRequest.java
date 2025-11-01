package com.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Author name is required")
    @Size(min = 2, max = 100, message = "Author name must be between 2 and 100 characters")
    private String authorName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String authorEmail;

    @NotBlank(message = "Comment content is required")
    @Size(min = 10, max = 2000, message = "Comment must be between 10 and 2000 characters")
    private String content;

    @NotBlank(message = "Post ID is required")
    private String postId;

    private String authorWebsite;

    // For replies (optional)
    private String parentCommentId;
}
