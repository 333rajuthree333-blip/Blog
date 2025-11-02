const { GoogleGenerativeAI } = require('@google/generative-ai');

class AIService {
    constructor() {
        this.genAI = new GoogleGenerativeAI(process.env.GOOGLE_GEMINI_API_KEY || 'your-gemini-api-key');
        this.model = this.genAI.getGenerativeModel({ model: 'gemini-pro' });
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

            const result = await this.model.generateContent(fullPrompt);
            const response = await result.response;
            const text = response.text();

            // Try to parse JSON response
            try {
                const parsed = JSON.parse(text);
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
                return {
                    success: false,
                    error: 'Failed to parse AI response. Please try again.',
                    rawResponse: text
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
}

module.exports = new AIService();
