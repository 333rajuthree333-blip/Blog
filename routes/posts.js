const express = require('express');
const router = express.Router();
const pool = require('../config/database');
const aiService = require('../services/aiService');

// Generate blog post using AI
router.post('/generate', async (req, res) => {
    try {
        const { prompt, author } = req.body;

        if (!prompt) {
            return res.status(400).json({ error: 'Prompt is required' });
        }

        const aiResult = await aiService.generateBlogPost(prompt);

        if (!aiResult.success) {
            return res.status(500).json({ error: aiResult.error });
        }

        // Save the generated post to database
        const postData = aiResult.data;
        const result = await pool.query(`
            INSERT INTO blog_posts (title, content, excerpt, author, published, created_at, updated_at, is_ai_generated, ai_prompt)
            VALUES ($1, $2, $3, $4, false, NOW(), NOW(), true, $5)
            RETURNING *
        `, [postData.title, postData.content, postData.excerpt, author || 'AI Assistant', prompt]);

        // Add tags
        if (postData.tags && Array.isArray(postData.tags)) {
            for (const tag of postData.tags) {
                await pool.query(`
                    INSERT INTO blog_post_tags (post_id, tag) VALUES ($1, $2)
                `, [result.rows[0].id, tag]);
            }
        }

        res.json({
            ...result.rows[0],
            tags: postData.tags
        });
    } catch (error) {
        console.error('Error generating blog post:', error);
        res.status(500).json({ error: 'Failed to generate blog post' });
    }
});

// Get all published posts
router.get('/', async (req, res) => {
    try {
        const result = await pool.query(`
            SELECT id, title, content, excerpt, author, featured_image, created_at, updated_at, view_count
            FROM blog_posts
            WHERE published = true
            ORDER BY created_at DESC
        `);
        res.json(result.rows);
    } catch (error) {
        console.error('Error fetching posts:', error);
        res.status(500).json({ error: 'Failed to fetch posts', details: error.message });
    }
});

// Get single post by ID
router.get('/:id', async (req, res) => {
    try {
        const { id } = req.params;

        // Update view count
        await pool.query('UPDATE blog_posts SET view_count = view_count + 1 WHERE id = $1', [id]);

        const result = await pool.query(`
            SELECT * FROM blog_posts WHERE id = $1 AND published = true
        `, [id]);

        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Post not found' });
        }

        res.json(result.rows[0]);
    } catch (error) {
        console.error('Error fetching post:', error);
        res.status(500).json({ error: 'Failed to fetch post' });
    }
});

// Search posts
router.get('/search/:keyword', async (req, res) => {
    try {
        const { keyword } = req.params;
        const result = await pool.query(`
            SELECT id, title, content, excerpt, author, featured_image, created_at, view_count
            FROM blog_posts
            WHERE published = true AND (title ILIKE $1 OR content ILIKE $1)
            ORDER BY created_at DESC
        `, [`%${keyword}%`]);

        res.json(result.rows);
    } catch (error) {
        console.error('Error searching posts:', error);
        res.status(500).json({ error: 'Failed to search posts' });
    }
});

// Get posts by tag
router.get('/tag/:tag', async (req, res) => {
    try {
        const { tag } = req.params;
        const result = await pool.query(`
            SELECT p.id, p.title, p.content, p.excerpt, p.author, p.featured_image, p.created_at, p.view_count
            FROM blog_posts p
            JOIN blog_post_tags pt ON p.id = pt.post_id
            WHERE p.published = true AND pt.tag = $1
            ORDER BY p.created_at DESC
        `, [tag]);

        res.json(result.rows);
    } catch (error) {
        console.error('Error fetching posts by tag:', error);
        res.status(500).json({ error: 'Failed to fetch posts by tag' });
    }
});

// Create new post
router.post('/', async (req, res) => {
    try {
        const { title, content, excerpt, author, tags, published } = req.body;

        const result = await pool.query(`
            INSERT INTO blog_posts (title, content, excerpt, author, published, created_at, updated_at)
            VALUES ($1, $2, $3, $4, $5, NOW(), NOW())
            RETURNING *
        `, [title, content, excerpt, author, published || false]);

        // Add tags if provided
        if (tags && Array.isArray(tags)) {
            for (const tag of tags) {
                await pool.query(`
                    INSERT INTO blog_post_tags (post_id, tag) VALUES ($1, $2)
                `, [result.rows[0].id, tag]);
            }
        }

        res.status(201).json(result.rows[0]);
    } catch (error) {
        console.error('Error creating post:', error);
        res.status(500).json({ error: 'Failed to create post' });
    }
});

// Update post
router.put('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const { title, content, excerpt, author, tags, published } = req.body;

        const result = await pool.query(`
            UPDATE blog_posts
            SET title = $1, content = $2, excerpt = $3, author = $4, published = $5, updated_at = NOW()
            WHERE id = $6
            RETURNING *
        `, [title, content, excerpt, author, published, id]);

        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Post not found' });
        }

        // Update tags
        await pool.query('DELETE FROM blog_post_tags WHERE post_id = $1', [id]);
        if (tags && Array.isArray(tags)) {
            for (const tag of tags) {
                await pool.query(`
                    INSERT INTO blog_post_tags (post_id, tag) VALUES ($1, $2)
                `, [id, tag]);
            }
        }

        res.json(result.rows[0]);
    } catch (error) {
        console.error('Error updating post:', error);
        res.status(500).json({ error: 'Failed to update post' });
    }
});

// Delete post
router.delete('/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const result = await pool.query('DELETE FROM blog_posts WHERE id = $1 RETURNING *', [id]);

        if (result.rows.length === 0) {
            return res.status(404).json({ error: 'Post not found' });
        }

        res.json({ message: 'Post deleted successfully' });
    } catch (error) {
        console.error('Error deleting post:', error);
        res.status(500).json({ error: 'Failed to delete post' });
    }
});

module.exports = router;
