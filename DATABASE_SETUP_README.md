# TechSci Blog - Database Setup Manual

This guide shows how to manually create all database tables for the TechSci Blog application.

## Files Created

1. `manual_tables.sql` - Basic table creation queries
2. `database_setup.sql` - Complete setup with sample data
3. `complete_schema.sql` - Full schema with indexes and views

## Step-by-Step Setup

### 1. Connect to Your Database

Connect to your Neon PostgreSQL database using any PostgreSQL client:

```bash
# Using psql
psql "postgresql://[username]:[password]@[host]/[database]"

# Or use Neon console at https://console.neon.tech
```

### 2. Run Table Creation

Copy and paste the SQL from `manual_tables.sql` or `complete_schema.sql`:

```sql
-- Run these commands in your database console

-- Create blog_posts table
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

-- Create blog_comments table
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

-- Create newsletter_subscriptions table
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

-- Create indexes for performance
CREATE INDEX idx_blog_comments_post_id ON blog_comments(post_id);
CREATE INDEX idx_newsletter_email ON newsletter_subscriptions(email);
-- ... add other indexes
```

### 3. Add Sample Data (Optional)

```sql
-- Add a sample blog post
INSERT INTO blog_posts (title, content, excerpt, author, published, published_at, view_count)
VALUES (
    'Welcome to TechSci Blog',
    'Welcome to our Technology & Science blog...',
    'Your source for technology and science news',
    'Admin',
    true,
    CURRENT_TIMESTAMP,
    150
);

-- Add a sample comment
INSERT INTO blog_comments (author_name, author_email, content, post_id, post_title, approved)
VALUES (
    'John Doe',
    'john@example.com',
    'Great article! Very informative.',
    1,
    'Welcome to TechSci Blog',
    true
);

-- Add a newsletter subscriber
INSERT INTO newsletter_subscriptions (email, name, active, confirmed, subscription_source)
VALUES (
    'subscriber@example.com',
    'John Doe',
    true,
    true,
    'homepage'
);
```

### 4. Verify Setup

Run these queries to verify everything is working:

```sql
-- Check tables exist
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public'
AND table_name IN ('blog_posts', 'blog_comments', 'newsletter_subscriptions');

-- Check sample data
SELECT COUNT(*) as posts_count FROM blog_posts;
SELECT COUNT(*) as comments_count FROM blog_comments;
SELECT COUNT(*) as subscribers_count FROM newsletter_subscriptions;
```

## Alternative: Auto Creation (Recommended)

Instead of manual creation, you can let Spring Boot auto-create tables:

1. Keep `spring.jpa.hibernate.ddl-auto=update` in `application.properties`
2. Start the application with `mvn spring-boot:run`
3. Tables will be created automatically

## Database Configuration

Update your `application.properties`:

```properties
# Neon PostgreSQL Database
spring.datasource.url=jdbc:postgresql://your-host/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## Features Implemented

✅ **Blog Posts** - Full CRUD with multilingual support
✅ **Comments System** - User comments with upvoting
✅ **Newsletter** - Email subscription management
✅ **Search** - Smart search with AI suggestions
✅ **Analytics** - View tracking and trending posts
✅ **User Management** - Admin authentication
✅ **Chatbot** - AI-powered assistant

## API Endpoints Available

- `GET /api/posts` - Get blog posts
- `POST /api/posts/comments` - Add comment
- `POST /api/posts/newsletter/subscribe` - Subscribe to newsletter
- `GET /api/posts/trending` - Get trending posts
- `GET /api/posts/search/smart` - Smart search

Your TechSci Blog is now fully set up with manual database creation! 🚀
