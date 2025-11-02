const axios = require('axios');

class AIService {
    constructor() {
        this.apiKey = process.env.OPENROUTER_API_KEY || 'your-openrouter-api-key';
        this.baseURL = 'https://openrouter.ai/api/v1';
    }

    async generateBlogPost(prompt) {
        try {
            const fullPrompt = `Write a comprehensive blog post based on this prompt: "${prompt}"

Please provide:
1. An engaging title
2. A brief excerpt (2-3 sentences)
3. The full blog post content (800-1200 words)
4. 5-8 relevant tags

Format your response as JSON with the following structure:
{
    "title": "Your Title Here",
    "excerpt": "Your excerpt here...",
    "content": "Your full blog post content here...",
    "tags": ["tag1", "tag2", "tag3", "tag4", "tag5"]
}`;

            const response = await this.callOpenRouter(fullPrompt);

            // Try to parse JSON response
            try {
                // Clean the response text (remove markdown code blocks if present)
                let cleanText = response.replace(/```json\s*/g, '').replace(/```\s*$/g, '').trim();

                const parsed = JSON.parse(cleanText);
                return {
                    success: true,
                    data: {
                        title: parsed.title,
                        excerpt: parsed.excerpt,
                        content: parsed.content,
                        tags: parsed.tags || [],
                        aiGenerated: true,
                        aiPrompt: prompt
                    }
                };
            } catch (parseError) {
                // If JSON parsing fails, try to extract information from text
                console.error('JSON parse error:', parseError);
                return {
                    success: false,
                    error: 'Failed to parse AI response. Please try again.',
                    rawResponse: response
                };
            }
        } catch (error) {
            console.error('Error generating blog post:', error);
            return {
                success: false,
                error: 'Failed to generate blog post. Please try again.'
            };
        }
    }

    async callOpenRouter(prompt) {
        try {
            const response = await axios.post(`${this.baseURL}/chat/completions`, {
                model: "deepseek/deepseek-chat",
                messages: [
                    {
                        role: "user",
                        content: prompt
                    }
                ]
            }, {
                headers: {
                    'Authorization': `Bearer ${this.apiKey}`,
                    'HTTP-Referer': 'https://your-blog-site.com',
                    'X-Title': 'Blog Website',
                    'Content-Type': 'application/json'
                }
            });

            return response.data.choices[0].message.content;
        } catch (error) {
            console.error('OpenRouter API error:', error.response?.data || error.message);
            throw new Error('Failed to call OpenRouter API');
        }
    }
}

module.exports = new AIService();
