package com.blog.repository;

import com.blog.model.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    Page<BlogPost> findByPublishedTrue(Pageable pageable);
    
    Page<BlogPost> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM BlogPost p WHERE p.published = true AND p.createdAt >= :thirtyDaysAgo ORDER BY p.viewCount DESC, p.createdAt DESC")
    List<BlogPost> findTrendingPosts(@Param("thirtyDaysAgo") java.time.LocalDateTime thirtyDaysAgo, Pageable pageable);

    @Query("SELECT p FROM BlogPost p WHERE p.published = true AND p.createdAt >= :sevenDaysAgo ORDER BY p.viewCount DESC, p.createdAt DESC")
    List<BlogPost> findWeeklyPopularPosts(@Param("sevenDaysAgo") java.time.LocalDateTime sevenDaysAgo, Pageable pageable);
    
    @Query("SELECT p FROM BlogPost p WHERE p.published = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.titleBn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.contentBn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.titleHi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.contentHi) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.excerpt) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           ":keyword MEMBER OF p.tags)")
    Page<BlogPost> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM BlogPost p WHERE p.published = true AND :tag MEMBER OF p.tags")
    Page<BlogPost> findByTag(@Param("tag") String tag, Pageable pageable);
    
    List<BlogPost> findByAuthor(String author);
    
    Long countByPublishedTrue();
    
    // Find top 10 posts by view count
    List<BlogPost> findTop10ByPublishedTrueOrderByViewCountDesc();
}
