package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.model.Page;
import com.blog.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PageController {

    private final PageService pageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Page>>> getAllPages() {
        try {
            List<Page> pages = pageService.getPublishedPages();
            return ResponseEntity.ok(ApiResponse.success(pages));
        } catch (Exception e) {
            log.error("Error fetching pages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching pages: " + e.getMessage()));
        }
    }

    @GetMapping("/footer")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFooterPages(@RequestParam(defaultValue = "en") String lang) {
        try {
            List<Page> pages = pageService.getFooterPages();
            
            List<Map<String, Object>> translatedPages = pages.stream().map(page -> {
                Map<String, Object> pageData = new java.util.HashMap<>();
                pageData.put("id", page.getId());
                pageData.put("slug", page.getSlug());
                
                // Language-specific title
                switch (lang.toLowerCase()) {
                    case "bn":
                        pageData.put("title", page.getTitle() != null ? page.getTitle() : page.getTitleEn());
                        break;
                    case "hi":
                        pageData.put("title", page.getTitleHi() != null ? page.getTitleHi() : page.getTitleEn());
                        break;
                    default:
                        pageData.put("title", page.getTitleEn() != null ? page.getTitleEn() : page.getTitle());
                        break;
                }
                
                return pageData;
            }).collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success(translatedPages));
        } catch (Exception e) {
            log.error("Error fetching footer pages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching pages: " + e.getMessage()));
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPageBySlug(@PathVariable String slug, @RequestParam(defaultValue = "en") String lang) {
        try {
            Page page = pageService.getPageBySlug(slug);
            
            Map<String, Object> pageData = new java.util.HashMap<>();
            pageData.put("id", page.getId());
            pageData.put("slug", page.getSlug());
            
            // Language-specific fields
            switch (lang.toLowerCase()) {
                case "bn":
                    pageData.put("title", page.getTitle() != null ? page.getTitle() : page.getTitleEn());
                    pageData.put("content", page.getContentBn() != null ? page.getContentBn() : page.getContent());
                    break;
                case "hi":
                    pageData.put("title", page.getTitleHi() != null ? page.getTitleHi() : page.getTitleEn());
                    pageData.put("content", page.getContentHi() != null ? page.getContentHi() : page.getContent());
                    break;
                default:
                    pageData.put("title", page.getTitleEn() != null ? page.getTitleEn() : page.getTitle());
                    pageData.put("content", page.getContent() != null ? page.getContent() : page.getContentBn());
                    break;
            }
            
            pageData.put("metaDescription", page.getMetaDescription());
            pageData.put("published", page.getPublished());
            pageData.put("createdAt", page.getCreatedAt());
            pageData.put("updatedAt", page.getUpdatedAt());
            
            return ResponseEntity.ok(ApiResponse.success(pageData));
        } catch (Exception e) {
            log.error("Error fetching page {}", slug, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Page not found: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Page>> createPage(@Valid @RequestBody Page page) {
        try {
            Page savedPage = pageService.createPage(page);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Page created successfully", savedPage));
        } catch (Exception e) {
            log.error("Error creating page", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating page: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Page>> updatePage(
            @PathVariable Long id,
            @Valid @RequestBody Page page
    ) {
        try {
            Page updatedPage = pageService.updatePage(id, page);
            return ResponseEntity.ok(ApiResponse.success("Page updated successfully", updatedPage));
        } catch (Exception e) {
            log.error("Error updating page {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating page: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePage(@PathVariable Long id) {
        try {
            pageService.deletePage(id);
            return ResponseEntity.ok(ApiResponse.success("Page deleted successfully", null));
        } catch (Exception e) {
            log.error("Error deleting page {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting page: " + e.getMessage()));
        }
    }
}
