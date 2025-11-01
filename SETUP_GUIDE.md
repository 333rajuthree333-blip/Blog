# Quick Setup Guide

## Step-by-Step Setup

### 1. Install Prerequisites

```bash
# Check Java version (requires Java 17+)
java -version

# Check Maven
mvn -version

# Install Node.js for MCP tools (if not installed)
# Follow instructions at https://nodejs.org/
```

### 2. Setup Neon Database

#### Cloud Setup (Recommended)

1. Go to https://neon.tech and sign up
2. Create a new project
3. Create a database named `blogdb`
4. Copy your connection details
5. Get your API key from: https://console.neon.tech/app/settings/api-keys

#### Local Setup

```bash
# Install Neon CLI
npm install -g @neondatabase/cli

# Initialize Neon
neon init

# Start Neon
neon start
```

### 3. Configure MCP Tools in Cursor

1. Open `/home/kali/Downloads/mcp_config.json`
2. Replace `<YOUR_NEON_API_KEY>` with your actual Neon API key:

```json
{
  "mcpServers": {
    "neon": {
      "command": "npx",
      "args": [
        "-y",
        "@neondatabase/mcp-server-neon",
        "start",
        "neon_api_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
      ]
    }
  }
}
```

3. Restart Cursor to load MCP tools

### 4. Get Google Gemini API Key

1. Visit https://makersuite.google.com/app/apikey
2. Click "Create API Key"
3. Copy your API key

### 5. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Neon Database (Cloud)
spring.datasource.url=jdbc:postgresql://ep-xyz-abc-123.us-east-2.aws.neon.tech/blogdb
spring.datasource.username=neondb_owner
spring.datasource.password=your_password_here

# OR Neon Database (Local)
# spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
# spring.datasource.username=postgres
# spring.datasource.password=postgres

# Google Gemini API
gemini.api.key=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

# JWT Secret (generate a random secure string)
jwt.secret=change-this-to-a-very-long-random-secure-string-with-at-least-256-bits
```

### 6. Build and Run

```bash
# Navigate to project directory
cd /home/kali/Downloads/blogwebsite

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 7. Access the Application

Open your browser and go to: http://localhost:8080

## Testing the Setup

### 1. Check Database Connection

The application will automatically create tables on startup. Check logs for:
```
Hibernate: create table blog_posts ...
```

### 2. Test AI Generation

1. Click "AI Generate" in the navigation
2. Enter a prompt like: "Write a blog post about Java Spring Boot"
3. Click "Generate Blog Post"
4. Wait for the AI to generate content

### 3. Create a Manual Post

1. Click "Create" in the navigation
2. Fill in the form
3. Upload an image (optional)
4. Click "Create Post"

### 4. Browse Posts

1. Click "Posts" in the navigation
2. View all published posts
3. Click on a post to view details

## Using MCP Tools in Cursor

With MCP tools configured, you can use Neon commands directly in Cursor:

```
# List your Neon projects
@mcp list_projects

# Describe a project
@mcp describe_project

# Run SQL queries
@mcp run_sql

# Get connection string
@mcp get_connection_string
```

## Common Commands

### Start Application
```bash
mvn spring-boot:run
```

### Build JAR
```bash
mvn clean package
```

### Run JAR
```bash
java -jar target/blog-website-1.0.0.jar
```

### View Logs
```bash
tail -f logs/spring-boot-application.log
```

### Check Database
```bash
# Using psql (if PostgreSQL client installed)
psql -h localhost -U postgres -d blogdb -c "SELECT * FROM blog_posts;"
```

## Environment Variables (Alternative Configuration)

Instead of editing `application.properties`, you can use environment variables:

```bash
export NEON_DB_URL="jdbc:postgresql://your-project.neon.tech/blogdb"
export NEON_DB_USERNAME="neondb_owner"
export NEON_DB_PASSWORD="your_password"
export GEMINI_API_KEY="your_gemini_api_key"
export JWT_SECRET="your_jwt_secret"

mvn spring-boot:run
```

## Troubleshooting

### Issue: Cannot connect to database

**Solution:**
1. Verify connection string is correct
2. Check if Neon database is running
3. Verify firewall/network settings
4. Test connection using `psql` or database client

### Issue: Gemini API not working

**Solution:**
1. Verify API key is correct
2. Check you have API quota remaining
3. Ensure you're using the correct endpoint
4. Check logs for detailed error messages

### Issue: Image upload not working

**Solution:**
1. Create `uploads` directory: `mkdir uploads`
2. Check directory permissions: `chmod 755 uploads`
3. Verify file size is under 10MB

### Issue: Port 8080 already in use

**Solution:**
Change port in `application.properties`:
```properties
server.port=8081
```

## Next Steps

1. ✅ Setup complete!
2. 📝 Create your first blog post
3. 🤖 Try AI-generated content
4. 🖼️ Upload some images
5. 🎨 Customize the design
6. 🔐 Add authentication (optional)
7. 🚀 Deploy to production (optional)

## Production Deployment

For production deployment:

1. Change `spring.jpa.hibernate.ddl-auto=update` to `validate`
2. Set strong JWT secret
3. Configure CORS for your domain
4. Use environment variables for sensitive data
5. Enable HTTPS
6. Set up proper logging
7. Configure backup for database
8. Use a reverse proxy (nginx/Apache)

## Support

If you encounter issues:
1. Check logs in console
2. Review `application.properties` configuration
3. Verify all API keys are correct
4. Check database connection
5. Review the full README.md for detailed documentation

Happy blogging! 🎉
