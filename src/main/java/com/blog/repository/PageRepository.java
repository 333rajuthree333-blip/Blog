package com.blog.repository;

import com.blog.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    
    Optional<Page> findBySlug(String slug);
    
    List<Page> findByPublishedTrueOrderByDisplayOrderAsc();
    
    List<Page> findByPublishedTrueAndShowInFooterTrueOrderByDisplayOrderAsc();
    
    Boolean existsBySlug(String slug);
}
