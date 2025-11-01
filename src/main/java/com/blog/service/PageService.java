package com.blog.service;

import com.blog.model.Page;
import com.blog.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PageService {

    private final PageRepository pageRepository;

    public Page createPage(Page page) {
        log.info("Creating new page: {}", page.getTitle());
        return pageRepository.save(page);
    }

    public Page getPageById(Long id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + id));
    }

    public Page getPageBySlug(String slug) {
        return pageRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Page not found with slug: " + slug));
    }

    public List<Page> getAllPages() {
        return pageRepository.findAll();
    }

    public List<Page> getPublishedPages() {
        return pageRepository.findByPublishedTrueOrderByDisplayOrderAsc();
    }

    public List<Page> getFooterPages() {
        return pageRepository.findByPublishedTrueAndShowInFooterTrueOrderByDisplayOrderAsc();
    }

    public Page updatePage(Long id, Page updatedPage) {
        Page existingPage = getPageById(id);
        
        existingPage.setTitle(updatedPage.getTitle());
        existingPage.setSlug(updatedPage.getSlug());
        existingPage.setContent(updatedPage.getContent());
        existingPage.setMetaDescription(updatedPage.getMetaDescription());
        existingPage.setPublished(updatedPage.getPublished());
        existingPage.setShowInFooter(updatedPage.getShowInFooter());
        existingPage.setDisplayOrder(updatedPage.getDisplayOrder());
        
        return pageRepository.save(existingPage);
    }

    public void deletePage(Long id) {
        pageRepository.deleteById(id);
        log.info("Deleted page with id: {}", id);
    }
}
