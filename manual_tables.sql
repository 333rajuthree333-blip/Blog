-- =============================================
-- MANUAL DATABASE TABLE CREATION
-- Copy and run these queries in your Neon/PostgreSQL console
-- =============================================

-- BLOG COMMENTS TABLE
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
    ip_address VARCHAR(45)
);

-- NEWSLETTER SUBSCRIPTIONS TABLE
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

-- BLOG POSTS TABLE (basic)
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
    featured_image VARCHAR(1000)
);

-- =============================================
-- INDEXES FOR PERFORMANCE
-- =============================================

-- Blog Comments Indexes
CREATE INDEX idx_blog_comments_post_id ON blog_comments(post_id);
CREATE INDEX idx_blog_comments_parent_comment_id ON blog_comments(parent_comment_id);
CREATE INDEX idx_blog_comments_created_at ON blog_comments(created_at);
CREATE INDEX idx_blog_comments_approved ON blog_comments(approved);

-- Newsletter Indexes
CREATE INDEX idx_newsletter_email ON newsletter_subscriptions(email);
CREATE INDEX idx_newsletter_active ON newsletter_subscriptions(active);
CREATE INDEX idx_newsletter_confirmed ON newsletter_subscriptions(confirmed);
CREATE INDEX idx_newsletter_subscribed_at ON newsletter_subscriptions(subscribed_at);

-- Blog Posts Indexes
CREATE INDEX idx_blog_posts_published ON blog_posts(published);
CREATE INDEX idx_blog_posts_created_at ON blog_posts(created_at);
CREATE INDEX idx_blog_posts_author ON blog_posts(author);
CREATE INDEX idx_blog_posts_published_at ON blog_posts(published_at);
