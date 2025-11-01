-- =============================================
-- TECHSCI BLOG DATABASE TABLES
-- Manual SQL Scripts for Table Creation
-- =============================================

-- Create blog_comments table
CREATE TABLE IF NOT EXISTS blog_comments (
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

-- Create index on post_id for faster queries
CREATE INDEX IF NOT EXISTS idx_blog_comments_post_id ON blog_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_blog_comments_parent_comment_id ON blog_comments(parent_comment_id);
CREATE INDEX IF NOT EXISTS idx_blog_comments_created_at ON blog_comments(created_at);
CREATE INDEX IF NOT EXISTS idx_blog_comments_approved ON blog_comments(approved);

-- =============================================
-- NEWSLETTER SUBSCRIPTIONS TABLE
-- =============================================

-- Create newsletter_subscriptions table
CREATE TABLE IF NOT EXISTS newsletter_subscriptions (
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

-- Create indexes for newsletter table
CREATE INDEX IF NOT EXISTS idx_newsletter_email ON newsletter_subscriptions(email);
CREATE INDEX IF NOT EXISTS idx_newsletter_active ON newsletter_subscriptions(active);
CREATE INDEX IF NOT EXISTS idx_newsletter_confirmed ON newsletter_subscriptions(confirmed);
CREATE INDEX IF NOT EXISTS idx_newsletter_subscribed_at ON newsletter_subscriptions(subscribed_at);

-- =============================================
-- BLOG POSTS TABLE (if not exists)
-- =============================================

-- Create blog_posts table (basic structure)
CREATE TABLE IF NOT EXISTS blog_posts (
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

-- Create indexes for blog_posts
CREATE INDEX IF NOT EXISTS idx_blog_posts_published ON blog_posts(published);
CREATE INDEX IF NOT EXISTS idx_blog_posts_created_at ON blog_posts(created_at);
CREATE INDEX IF NOT EXISTS idx_blog_posts_author ON blog_posts(author);
CREATE INDEX IF NOT EXISTS idx_blog_posts_published_at ON blog_posts(published_at);

-- =============================================
-- SAMPLE DATA INSERTION (Optional)
-- =============================================

-- Insert sample blog post
INSERT INTO blog_posts (title, content, excerpt, author, published, published_at, view_count)
VALUES (
    'Welcome to TechSci Blog',
    'Welcome to our Technology & Science blog. Here you will find the latest updates on AI, machine learning, quantum computing, and space exploration.',
    'Your source for technology and science news',
    'Admin',
    true,
    CURRENT_TIMESTAMP,
    150
) ON CONFLICT DO NOTHING;

-- Insert sample comment
INSERT INTO blog_comments (author_name, author_email, content, post_id, post_title, approved)
VALUES (
    'John Doe',
    'john@example.com',
    'Great article! Very informative and well-written.',
    1,
    'Welcome to TechSci Blog',
    true
) ON CONFLICT DO NOTHING;

-- Insert sample newsletter subscription
INSERT INTO newsletter_subscriptions (email, name, active, confirmed, subscription_source)
VALUES (
    'admin@techsci-blog.com',
    'Admin User',
    true,
    true,
    'system'
) ON CONFLICT (email) DO NOTHING;

-- =============================================
-- USEFUL QUERIES FOR MANAGEMENT
-- =============================================

-- Get all comments for a specific post
-- SELECT * FROM blog_comments WHERE post_id = ? AND approved = true ORDER BY created_at ASC;

-- Get newsletter subscribers count
-- SELECT COUNT(*) as total_subscribers FROM newsletter_subscriptions WHERE active = true AND confirmed = true;

-- Get recent comments
-- SELECT * FROM blog_comments WHERE approved = true ORDER BY created_at DESC LIMIT 10;

-- Get popular posts by views
-- SELECT * FROM blog_posts WHERE published = true ORDER BY view_count DESC LIMIT 5;

-- Get trending posts (last 30 days)
-- SELECT * FROM blog_posts WHERE published = true AND created_at >= (CURRENT_TIMESTAMP - INTERVAL '30 days') ORDER BY view_count DESC LIMIT 5;
