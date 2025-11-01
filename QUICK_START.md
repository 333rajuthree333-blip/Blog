# 🚀 Quick Start - AI Blog Website

## ⚡ 3-Minute Setup

### 1. Configure Neon Database

Edit `src/main/resources/application.properties`:

```properties
# For Neon Cloud Database
spring.datasource.url=jdbc:postgresql://ep-your-project.neon.tech/blogdb
spring.datasource.username=neondb_owner
spring.datasource.password=your_password

# For Local PostgreSQL/Neon
# spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
# spring.datasource.username=postgres
# spring.datasource.password=postgres
```

### 2. Add Gemini API Key

Get free API key: https://makersuite.google.com/app/apikey

```properties
gemini.api.key=AIzaSy_your_api_key_here
```

### 3. Run Application

```bash
cd /home/kali/Downloads/blogwebsite
mvn spring-boot:run
```

### 4. Open Browser

Go to: http://localhost:8080

## 🎯 Test Features

### ✅ AI Blog Generation
1. Click "AI Generate"
2. Enter: "Write about artificial intelligence in healthcare"
3. Click "Generate Blog Post"
4. Publish or edit the generated post

### ✅ Manual Post Creation
1. Click "Create"
2. Fill in title, content, author
3. Upload an image (optional)
4. Add tags (comma-separated)
5. Check "Publish immediately"
6. Click "Create Post"

### ✅ Browse Posts
1. Click "Posts"
2. Search by keyword
3. Click any post to read
4. View AI-generated badge on AI posts

## 🔧 MCP Tools Setup (Cursor)

Update `/home/kali/Downloads/mcp_config.json`:

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

Get API key: https://console.neon.tech/app/settings/api-keys

## 📋 All Features Included

✅ **Database Features**
- Neon PostgreSQL integration
- Automatic schema creation
- Local & cloud database support
- MCP tools integration for Cursor

✅ **AI Features**
- Blog post generation using Google Gemini
- Automatic title generation
- Excerpt generation
- Tag suggestions
- AI-powered content creation

✅ **Blog Features**
- Create/Read/Update/Delete posts
- Image upload (10MB max)
- Multiple images per post
- Featured image support
- Tag system
- Search functionality
- View counter
- Draft/Publish status
- Author attribution

✅ **UI Features**
- Modern, responsive design
- Beautiful gradient hero section
- Grid layout for posts
- Modal for post details
- Loading indicators
- Toast notifications
- Pagination
- Mobile-friendly

✅ **Technical Features**
- RESTful API
- Input validation
- Error handling
- File upload with validation
- CORS enabled
- JPA auditing (created/updated timestamps)
- Prepared for authentication

## 📁 Project Structure

```
blogwebsite/
├── src/main/java/com/blog/
│   ├── BlogWebsiteApplication.java     # Main application
│   ├── controller/                      # REST API endpoints
│   │   ├── BlogPostController.java      # Post CRUD & AI generation
│   │   └── FileUploadController.java    # Image upload
│   ├── model/                           # Database entities
│   │   ├── BlogPost.java                # Blog post model
│   │   ├── User.java                    # User model
│   │   ├── Comment.java                 # Comment model
│   │   └── Category.java                # Category model
│   ├── repository/                      # Data access layer
│   ├── service/                         # Business logic
│   │   ├── BlogPostService.java         # Post operations
│   │   ├── GeminiService.java           # AI integration
│   │   └── FileStorageService.java      # File management
│   └── dto/                             # Data transfer objects
└── src/main/resources/
    ├── application.properties           # Configuration
    └── static/                          # Frontend files
        ├── index.html                   # Main page
        ├── css/style.css                # Styling
        └── js/app.js                    # JavaScript logic
```

## 🔑 API Endpoints

### Posts
- `GET /api/posts` - List published posts
- `GET /api/posts/{id}` - Get single post
- `POST /api/posts` - Create post manually
- `POST /api/posts/generate` - Generate with AI
- `PUT /api/posts/{id}` - Update post
- `PATCH /api/posts/{id}/publish` - Toggle publish
- `DELETE /api/posts/{id}` - Delete post
- `GET /api/posts/search?keyword=...` - Search
- `GET /api/posts/tag/{tag}` - Filter by tag
- `GET /api/posts/top` - Top posts by views

### Upload
- `POST /api/upload/image` - Upload image
- `GET /api/upload/images/{fileName}` - Get image
- `DELETE /api/upload/images/{fileName}` - Delete image

## 💡 Tips

1. **Database**: Schema auto-creates on first run
2. **Images**: Stored in `./uploads` directory
3. **AI**: Be specific in prompts for better results
4. **Tags**: Comma-separated, e.g., "java, spring, tutorial"
5. **MCP**: Restart Cursor after updating config

## 🐛 Troubleshooting

**Cannot connect to database?**
- Check connection string
- Verify Neon database is running
- Test with: `psql -h host -U user -d blogdb`

**Gemini API error?**
- Verify API key is valid
- Check quota at console.cloud.google.com
- Ensure key has Gemini API enabled

**Port 8080 in use?**
- Change in application.properties: `server.port=8081`

## 📚 Documentation

- Full README: `README.md`
- Setup Guide: `SETUP_GUIDE.md`
- Environment Example: `.env.example`

## 🎉 You're Ready!

Your AI-powered blog website is complete with:
- ✅ Java Spring Boot backend
- ✅ Neon PostgreSQL database
- ✅ Google Gemini AI integration
- ✅ Image upload functionality
- ✅ Modern responsive UI
- ✅ RESTful API
- ✅ MCP tools support

Start creating amazing blog content with AI! 🚀
