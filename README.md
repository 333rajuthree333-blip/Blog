# AI-Powered Blog Website

A full-featured blog platform built with Java Spring Boot, PostgreSQL (Neon), and Google Gemini AI for automatic content generation.

## Features

- ✨ **AI-Powered Content Generation** - Automatically generate blog posts using Google Gemini AI
- 📝 **Rich Text Editor** - Create and edit blog posts with ease
- 🖼️ **Image Upload** - Upload and manage images for your blog posts
- 🔍 **Search Functionality** - Search posts by keywords
- 🏷️ **Tag System** - Organize posts with tags
- 📊 **View Counter** - Track post views
- 📱 **Responsive Design** - Beautiful, modern UI that works on all devices
- 🗄️ **Neon Database Integration** - PostgreSQL database with MCP tools support
- 🔐 **Security** - JWT-based authentication (ready for implementation)

## Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL** (Neon Database)
- **Maven**

### AI Integration
- **Google Gemini API** - For AI content generation

### Frontend
- **HTML5**
- **CSS3** (Modern, responsive design)
- **Vanilla JavaScript**
- **Font Awesome Icons**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (Neon account recommended)
- Google Gemini API key
- Node.js (for MCP tools)

## Setup Instructions

### 1. Clone the Repository

```bash
cd /home/kali/Downloads/blogwebsite
```

### 2. Configure Neon Database

#### Option A: Using Neon Cloud

1. Create a Neon account at https://neon.tech
2. Create a new project
3. Copy your connection string
4. Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://your-project.neon.tech/blogdb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### Option B: Using Neon Locally

1. Install Neon locally following: https://neon.tech/docs/get-started-with-neon/local-development
2. Start Neon locally:

```bash
neon init
neon start
```

3. Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Configure MCP Tools (Cursor Integration)

1. Update the MCP config at `/home/kali/Downloads/mcp_config.json`:

```json
{
  "mcpServers": {
    "neon": {
      "command": "npx",
      "args": [
        "-y",
        "@neondatabase/mcp-server-neon",
        "start",
        "YOUR_NEON_API_KEY"
      ]
    }
  }
}
```

2. Get your Neon API key from: https://console.neon.tech/app/settings/api-keys

### 4. Configure Google Gemini API

1. Get a free Gemini API key from: https://makersuite.google.com/app/apikey
2. Update `src/main/resources/application.properties`:

```properties
gemini.api.key=your_gemini_api_key_here
```

### 5. Build the Project

```bash
mvn clean install
```

### 6. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/blog-website-1.0.0.jar
```

The application will start on `http://localhost:8080`

## Database Schema

The application automatically creates the following tables:

### blog_posts
- `id` (PRIMARY KEY)
- `title` - Post title
- `content` - Post content
- `excerpt` - Brief summary
- `author` - Author name
- `featured_image` - Featured image URL
- `published` - Publication status
- `view_count` - Number of views
- `is_ai_generated` - AI-generated flag
- `ai_prompt` - Original AI prompt
- `created_at` - Creation timestamp
- `updated_at` - Last update timestamp
- `published_at` - Publication timestamp

### blog_post_tags
- `post_id` (FOREIGN KEY)
- `tag` - Tag name

### blog_post_images
- `post_id` (FOREIGN KEY)
- `image_url` - Image URL

### users
- `id` (PRIMARY KEY)
- `username` - Unique username
- `email` - User email
- `password` - Encrypted password
- `full_name` - Full name
- `bio` - User bio
- `profile_image` - Profile image URL
- `enabled` - Account status
- `created_at` - Registration date
- `updated_at` - Last update
- `last_login` - Last login timestamp

### comments
- `id` (PRIMARY KEY)
- `content` - Comment content
- `post_id` (FOREIGN KEY) - Related post
- `user_id` (FOREIGN KEY) - Comment author
- `parent_id` (FOREIGN KEY) - Parent comment (for replies)
- `approved` - Approval status
- `created_at` - Creation timestamp

### categories
- `id` (PRIMARY KEY)
- `name` - Category name
- `slug` - URL-friendly slug
- `description` - Category description

## API Endpoints

### Blog Posts

- `GET /api/posts` - Get all published posts (paginated)
- `GET /api/posts/all` - Get all posts including drafts (admin)
- `GET /api/posts/{id}` - Get single post by ID
- `GET /api/posts/search?keyword=...` - Search posts
- `GET /api/posts/tag/{tag}` - Get posts by tag
- `GET /api/posts/top` - Get top posts by views
- `GET /api/posts/stats` - Get statistics
- `POST /api/posts` - Create new post manually
- `POST /api/posts/generate` - Generate post using AI
- `PUT /api/posts/{id}` - Update post
- `PATCH /api/posts/{id}/publish` - Toggle publish status
- `POST /api/posts/{id}/images` - Add image to post
- `PATCH /api/posts/{id}/featured-image` - Set featured image
- `DELETE /api/posts/{id}` - Delete post

### File Upload

- `POST /api/upload/image` - Upload image
- `GET /api/upload/images/{fileName}` - Get/download image
- `DELETE /api/upload/images/{fileName}` - Delete image

## AI Content Generation

### How to Use

1. Navigate to the "AI Generate" section
2. Enter a detailed prompt describing what you want to write about
3. Click "Generate Blog Post"
4. Review the generated content
5. Edit if needed or publish directly

### Example Prompts

- "Write a comprehensive guide about machine learning for beginners"
- "Create a blog post about the benefits of meditation and mindfulness"
- "Write an article comparing React vs Vue.js for web development"
- "Create a tutorial on getting started with Docker containers"

### AI Features

- Automatic title generation
- Content generation (800-1200 words)
- Excerpt generation
- Tag suggestions
- Markdown formatting support

## Image Upload

1. When creating/editing a post, click on the image upload field
2. Select an image (JPEG, PNG, GIF, WebP)
3. Maximum file size: 10MB
4. Images are automatically stored and served

## Configuration Options

### application.properties

```properties
# Server port
server.port=8080

# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=your_username
spring.datasource.password=your_password

# JPA settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload.dir=./uploads

# Gemini API
gemini.api.key=your_api_key
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# CORS
cors.allowed.origins=http://localhost:3000,http://localhost:8080
```

## Project Structure

```
blogwebsite/
├── src/
│   ├── main/
│   │   ├── java/com/blog/
│   │   │   ├── BlogWebsiteApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── BlogPostController.java
│   │   │   │   └── FileUploadController.java
│   │   │   ├── model/
│   │   │   │   ├── BlogPost.java
│   │   │   │   ├── User.java
│   │   │   │   ├── Comment.java
│   │   │   │   └── Category.java
│   │   │   ├── repository/
│   │   │   │   ├── BlogPostRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CommentRepository.java
│   │   │   │   └── CategoryRepository.java
│   │   │   ├── service/
│   │   │   │   ├── BlogPostService.java
│   │   │   │   ├── GeminiService.java
│   │   │   │   └── FileStorageService.java
│   │   │   └── dto/
│   │   │       ├── BlogPostRequest.java
│   │   │       ├── AIGenerateRequest.java
│   │   │       └── ApiResponse.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           ├── index.html
│   │           ├── css/style.css
│   │           └── js/app.js
│   └── test/
├── pom.xml
└── README.md
```

## Testing

### Test AI Generation

```bash
curl -X POST http://localhost:8080/api/posts/generate \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "Write about the future of artificial intelligence",
    "author": "AI Assistant"
  }'
```

### Test Manual Post Creation

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Post",
    "content": "This is the content of my first blog post.",
    "author": "John Doe",
    "tags": ["introduction", "first-post"],
    "published": true
  }'
```

### Test Image Upload

```bash
curl -X POST http://localhost:8080/api/upload/image \
  -F "file=@/path/to/image.jpg"
```

## Troubleshooting

### Database Connection Issues

1. Verify Neon database is running
2. Check connection string in `application.properties`
3. Ensure firewall allows PostgreSQL connection (port 5432)

### Gemini API Issues

1. Verify API key is correct
2. Check API quota at https://console.cloud.google.com
3. Ensure you're using the correct API endpoint

### Image Upload Issues

1. Check `uploads` directory exists and is writable
2. Verify file size is under 10MB
3. Ensure file type is supported (JPEG, PNG, GIF, WebP)

## Future Enhancements

- [ ] User authentication and authorization
- [ ] Comment system
- [ ] Category management
- [ ] RSS feed
- [ ] SEO optimization
- [ ] Social media sharing
- [ ] Newsletter subscription
- [ ] Advanced text editor (WYSIWYG)
- [ ] Multi-language support
- [ ] Dark mode

## License

MIT License

## Support

For issues and questions:
- Check the documentation
- Review API endpoints
- Check application logs in `logs/` directory

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
