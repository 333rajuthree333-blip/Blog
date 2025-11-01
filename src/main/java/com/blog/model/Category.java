package com.blog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    @PreUpdate
    private void generateSlug() {
        if (name != null) {
            this.slug = name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
        }
    }
}
