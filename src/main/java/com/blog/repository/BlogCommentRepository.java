package com.blog.repository;

import com.blog.model.BlogComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {

    // Find all approved comments for a specific post
    @Query("SELECT c FROM BlogComment c WHERE c.postId = :postId AND c.approved = true ORDER BY c.createdAt ASC")
    List<BlogComment> findApprovedCommentsByPostId(@Param("postId") Long postId);

    // Find comments with pagination
    @Query("SELECT c FROM BlogComment c WHERE c.postId = :postId AND c.approved = true ORDER BY c.upvotes DESC, c.createdAt DESC")
    Page<BlogComment> findApprovedCommentsByPostIdPaged(@Param("postId") Long postId, Pageable pageable);

    // Count comments for a post
    Long countByPostIdAndApprovedTrue(Long postId);

    // Find top comments by upvotes
    @Query("SELECT c FROM BlogComment c WHERE c.approved = true ORDER BY c.upvotes DESC, c.createdAt DESC")
    List<BlogComment> findTopComments(Pageable pageable);
}
