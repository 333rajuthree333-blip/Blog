package com.blog.service;

import com.blog.dto.CommentRequest;
import com.blog.model.BlogComment;
import com.blog.model.BlogPost;
import com.blog.repository.BlogCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlogCommentService {

    private final BlogCommentRepository blogCommentRepository;
    private final BlogPostService blogPostService;

    /**
     * Create a new comment
     */
    public BlogComment createComment(CommentRequest request, String ipAddress) {
        log.info("Creating new comment for post: {}", request.getPostId());

        // Get post details for reference
        BlogPost post = blogPostService.getPostById(Long.parseLong(request.getPostId()));

        BlogComment comment = BlogComment.builder()
                .authorName(request.getAuthorName().trim())
                .authorEmail(request.getAuthorEmail().trim().toLowerCase())
                .content(request.getContent().trim())
                .postId(post.getId())
                .postTitle(post.getTitle())
                .authorWebsite(request.getAuthorWebsite() != null ? request.getAuthorWebsite().trim() : null)
                .ipAddress(ipAddress)
                .approved(true) // Auto-approve for now, can add moderation later
                .upvotes(0)
                .build();

        // Handle parent comment for replies
        if (request.getParentCommentId() != null && !request.getParentCommentId().isEmpty()) {
            comment.setParentCommentId(Long.parseLong(request.getParentCommentId()));
        }

        BlogComment savedComment = blogCommentRepository.save(comment);
        log.info("Comment created with ID: {}", savedComment.getId());

        return savedComment;
    }

    /**
     * Get all approved comments for a post
     */
    @Transactional(readOnly = true)
    public List<BlogComment> getCommentsForPost(Long postId) {
        return blogCommentRepository.findApprovedCommentsByPostId(postId);
    }

    /**
     * Get paginated comments for a post
     */
    @Transactional(readOnly = true)
    public Page<BlogComment> getCommentsForPostPaged(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return blogCommentRepository.findApprovedCommentsByPostIdPaged(postId, pageable);
    }

    /**
     * Count comments for a post
     */
    @Transactional(readOnly = true)
    public Long getCommentCountForPost(Long postId) {
        return blogCommentRepository.countByPostIdAndApprovedTrue(postId);
    }

    /**
     * Upvote a comment
     */
    public BlogComment upvoteComment(Long commentId) {
        BlogComment comment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        comment.setUpvotes(comment.getUpvotes() + 1);
        return blogCommentRepository.save(comment);
    }

    /**
     * Get top comments globally
     */
    @Transactional(readOnly = true)
    public List<BlogComment> getTopComments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return blogCommentRepository.findTopComments(pageable);
    }

    /**
     * Delete a comment (admin function)
     */
    public void deleteComment(Long commentId) {
        blogCommentRepository.deleteById(commentId);
        log.info("Deleted comment with ID: {}", commentId);
    }

    /**
     * Approve/reject comment (moderation)
     */
    public BlogComment moderateComment(Long commentId, boolean approved) {
        BlogComment comment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        comment.setApproved(approved);
        return blogCommentRepository.save(comment);
    }
}
