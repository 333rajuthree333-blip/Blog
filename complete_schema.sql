-- =============================================
-- TECHSCI BLOG - COMPLETE DATABASE SCHEMA
-- All Tables with Full Structure
-- =============================================

-- 1. BLOG_POSTS TABLE
-- Stores all blog articles and posts
CREATE TABLE blog_posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT,
    excerpt TEXT,
    author VARCHAR(255) DEFAULT 'Anonymous',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    view_count BIGINT DEFAULT 0,
    is_ai_generated BOOLEAN DEFAULT FALSE,
    ai_prompt TEXT,
    featured_image VARCHAR(1000),
    -- Multi-language support
    title_bn VARCHAR(500),
    content_bn TEXT,
    excerpt_bn TEXT,
    title_hi VARCHAR(500),
    content_hi TEXT,
    excerpt_hi TEXT
);

-- 2. BLOG_COMMENTS TABLE
-- Stores user comments on blog posts
CREATE TABLE blog_comments (
    id BIGSERIAL PRIMARY KEY,
    author_name VARCHAR(255) NOT NULL,
    author_email VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    post_title VARCHAR(255),
    upvotes INTEGER DEFAULT 0,
    approved BOOLEAN DEFAULT TRUE,
    author_website VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id BIGINT,
    ip_address VARCHAR(45),
    FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE
);

-- 3. NEWSLETTER_SUBSCRIPTIONS TABLE
-- Stores email newsletter subscribers
CREATE TABLE newsletter_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    confirmed BOOLEAN DEFAULT FALSE,
    confirmation_token VARCHAR(100),
    ip_address VARCHAR(45),
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_email_sent TIMESTAMP,
    subscription_source VARCHAR(50),
    confirmed_at TIMESTAMP
);

-- 4. BLOG_TAGS TABLE (for future use)
-- Stores tag relationships
CREATE TABLE blog_tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. BLOG_POST_TAGS TABLE (for future use)
-- Many-to-many relationship between posts and tags
CREATE TABLE blog_post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES blog_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES blog_tags(id) ON DELETE CASCADE
);

-- =============================================
-- INDEXES FOR PERFORMANCE OPTIMIZATION
-- =============================================

-- Blog Posts Indexes
CREATE INDEX idx_blog_posts_published ON blog_posts(published);
CREATE INDEX idx_blog_posts_created_at ON blog_posts(created_at);
CREATE INDEX idx_blog_posts_author ON blog_posts(author);
CREATE INDEX idx_blog_posts_published_at ON blog_posts(published_at);
CREATE INDEX idx_blog_posts_view_count ON blog_posts(view_count DESC);
CREATE INDEX idx_blog_posts_ai_generated ON blog_posts(is_ai_generated);

-- Blog Comments Indexes
CREATE INDEX idx_blog_comments_post_id ON blog_comments(post_id);
CREATE INDEX idx_blog_comments_parent_comment_id ON blog_comments(parent_comment_id);
CREATE INDEX idx_blog_comments_created_at ON blog_comments(created_at);
CREATE INDEX idx_blog_comments_approved ON blog_comments(approved);
CREATE INDEX idx_blog_comments_upvotes ON blog_comments(upvotes DESC);
CREATE INDEX idx_blog_comments_author_email ON blog_comments(author_email);

-- Newsletter Indexes
CREATE INDEX idx_newsletter_email ON newsletter_subscriptions(email);
CREATE INDEX idx_newsletter_active ON newsletter_subscriptions(active);
CREATE INDEX idx_newsletter_confirmed ON newsletter_subscriptions(confirmed);
CREATE INDEX idx_newsletter_subscribed_at ON newsletter_subscriptions(subscribed_at);
CREATE INDEX idx_newsletter_source ON newsletter_subscriptions(subscription_source);

-- =============================================
-- USEFUL VIEWS FOR ANALYTICS
-- =============================================

-- View for published posts with comment counts
CREATE OR REPLACE VIEW posts_with_comments AS
SELECT
    p.*,
    COALESCE(comment_counts.total_comments, 0) as comment_count
FROM blog_posts p
LEFT JOIN (
    SELECT post_id, COUNT(*) as total_comments
    FROM blog_comments
    WHERE approved = true
    GROUP BY post_id
) comment_counts ON p.id = comment_counts.post_id
WHERE p.published = true;

-- View for newsletter analytics
CREATE OR REPLACE VIEW newsletter_analytics AS
SELECT
    COUNT(*) as total_subscribers,
    COUNT(CASE WHEN confirmed = true THEN 1 END) as confirmed_subscribers,
    COUNT(CASE WHEN active = true AND confirmed = true THEN 1 END) as active_confirmed,
    COUNT(CASE WHEN subscribed_at >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as new_subscribers_30d
FROM newsletter_subscriptions;

-- =============================================
-- SAMPLE DATA FOR TESTING
-- =============================================

-- Sample blog post
INSERT INTO blog_posts (title, content, excerpt, author, published, published_at, view_count)
VALUES (
    'The Future of Artificial Intelligence',
    'Artificial Intelligence is transforming our world...',
    'Explore the latest developments in AI technology',
    'Admin',
    true,
    CURRENT_TIMESTAMP,
    250
);

-- Sample comment
INSERT INTO blog_comments (author_name, author_email, content, post_id, post_title, approved)
VALUES (
    'Alice Johnson',
    'alice@example.com',
    'Excellent article! Very insightful analysis of AI trends.',
    1,
    'The Future of Artificial Intelligence',
    true
);

-- Sample newsletter subscriber
INSERT INTO newsletter_subscriptions (email, name, active, confirmed, subscription_source)
VALUES (
    'subscriber@example.com',
    'John Doe',
    true,
    true,
    'homepage'
);

-- =============================================
-- COMMON QUERY PATTERNS
-- =============================================

-- Get recent published posts
-- SELECT * FROM blog_posts WHERE published = true ORDER BY published_at DESC LIMIT 10;

-- Get comments for a specific post
-- SELECT * FROM blog_comments WHERE post_id = ? AND approved = true ORDER BY created_at ASC;

-- Get trending posts (last 30 days, most viewed)
-- SELECT * FROM blog_posts
-- WHERE published = true AND created_at >= (CURRENT_TIMESTAMP - INTERVAL '30 days')
-- ORDER BY view_count DESC LIMIT 5;

-- Get newsletter subscriber count
-- SELECT COUNT(*) FROM newsletter_subscriptions WHERE active = true AND confirmed = true;

-- Get posts by author
-- SELECT * FROM blog_posts WHERE author = ? AND published = true ORDER BY created_at DESC;

-- Get top commented posts
-- SELECT p.*, COUNT(c.id) as comment_count
-- FROM blog_posts p
-- LEFT JOIN blog_comments c ON p.id = c.post_id AND c.approved = true
-- WHERE p.published = true
-- GROUP BY p.id
-- ORDER BY comment_count DESC LIMIT 5;
