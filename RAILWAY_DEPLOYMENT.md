# 🚂 Railway Deployment Guide

আপনার Blog Website Railway.app-এ deploy করার সম্পূর্ণ গাইড।

## ✅ Prerequisites (আগে থেকে যা লাগবে)

- ✓ GitHub account
- ✓ Railway account (free)
- ✓ Git installed
- ✓ আপনার Neon Database connection string
- ✓ OpenRouter API keys

## 📦 Step 1: Project Setup

### 1.1 Git Repository তৈরি করুন (যদি না থাকে)

```bash
cd /home/kali/Downloads/blogwebsite

# Git initialize
git init

# সব ফাইল add করুন
git add .

# First commit
git commit -m "Initial commit: Blog website ready for Railway deployment"
```

### 1.2 GitHub-এ Push করুন

```bash
# GitHub-এ নতুন repository তৈরি করুন (github.com)
# তারপর:
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

## 🚀 Step 2: Railway Deployment

### 2.1 Railway CLI Install করুন

```bash
npm i -g @railway/cli
```

### 2.2 Railway Login করুন

```bash
railway login
```
(Browser খুলবে, login করুন)

### 2.3 Project Create করুন

```bash
# আপনার project directory থেকে
cd /home/kali/Downloads/blogwebsite

# নতুন Railway project তৈরি
railway init

# প্রশ্ন আসবে:
# ✓ "Create a new project" সিলেক্ট করুন
# ✓ Project name: blog-website (অথবা আপনার পছন্দ মতো)
```

### 2.4 GitHub Connect করুন (Optional but Recommended)

Railway dashboard থেকে:
1. আপনার project খুলুন
2. "Connect to GitHub" ক্লিক করুন
3. Repository select করুন
4. Auto-deploy enable করুন

## 🔐 Step 3: Environment Variables Setup

Railway dashboard-এ যান এবং **Variables** section-এ এই environment variables গুলো add করুন:

### Database Configuration:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-fragrant-sea-a1edrh7r-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=npg_7uAziTVoml6R
```

### API Keys:
```
OPENROUTER_API_KEY=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
CHATBOT_API_KEY=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
```

### JWT Configuration:
```
JWT_SECRET=your-super-secure-random-jwt-secret-key-change-this
JWT_EXPIRATION=86400000
```

### Admin Credentials:
```
ADMIN_USERNAME=admin
ADMIN_PASSWORD=secure_admin_password_123
```

### File Upload:
```
FILE_UPLOAD_DIR=/app/uploads
```

### Spring Configuration:
```
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
```

## 🚀 Step 4: Deploy করুন!

### Option A: CLI দিয়ে (Quick)

```bash
railway up
```

এটি automatically:
- ✓ Code push করবে
- ✓ Maven build করবে
- ✓ JAR file তৈরি করবে
- ✓ Deploy করবে
- ✓ URL দেবে

### Option B: GitHub Auto-Deploy (Recommended)

যদি GitHub connect করে থাকেন:
```bash
git add .
git commit -m "Ready for Railway deployment"
git push origin main
```

Railway automatically deploy করবে! 🎉

## 🌐 Step 5: Domain & Access

Deploy শেষ হলে:

1. **Public URL পাবেন**: `https://your-app.railway.app`
2. **Custom Domain** add করতে পারবেন (Railway dashboard থেকে)
3. **Logs দেখুন**: `railway logs`

## 🔍 Step 6: Verify Deployment

```bash
# Logs দেখুন
railway logs

# Service status চেক করুন
railway status

# আপনার site খুলুন
railway open
```

## ⚙️ Useful Railway Commands

```bash
# Current status
railway status

# Live logs দেখুন
railway logs

# Environment variables দেখুন
railway variables

# Service restart করুন
railway restart

# Shell access (debugging)
railway shell

# Project info
railway whoami
```

## 🐛 Troubleshooting

### Build Failed?

```bash
# Local-এ test করুন
mvn clean package -DskipTests

# যদি success হয়, তাহলে push করুন
git push origin main
```

### Database Connection Issue?

1. Railway Variables check করুন
2. Neon database active আছে কিনা verify করুন
3. Connection string সঠিক আছে কিনা check করুন

### Application Not Starting?

```bash
# Logs দেখুন
railway logs

# Common issues:
# - PORT variable missing (already configured in application.properties)
# - Database connection failed
# - Missing environment variables
```

### Memory Issues?

Railway free tier:
- 512MB RAM
- 1GB Disk

যদি memory error আসে:
```bash
# Java heap size কমান (nixpacks.toml update করুন)
cmd = "java -Xmx400m -Dserver.port=$PORT -jar target/blog-website-1.0.0.jar"
```

## 💰 Railway Free Tier Limits

- 💵 **$5 credit/month** (প্রায় 500-1000 ঘন্টা)
- 💾 **512 MB RAM**
- 💽 **1 GB Disk**
- ⚡ **No sleep mode** (always active!)
- 🌐 **Custom domains free**

## 🔄 Update/Redeploy

### Manual Update:
```bash
# Code change করার পর
git add .
git commit -m "Your changes"
git push origin main
```
(Auto-deploy করবে যদি GitHub connected থাকে)

### Force Redeploy:
```bash
railway up --detach
```

## 🎯 Environment Variables Update করতে

### CLI দিয়ে:
```bash
railway variables set KEY=VALUE
```

### Dashboard দিয়ে:
1. Railway.app dashboard যান
2. Your project → Variables
3. Add/Edit করুন
4. Service automatically restart হবে

## 📊 Monitoring

Railway dashboard-এ পাবেন:
- ✅ Real-time metrics
- ✅ CPU/Memory usage
- ✅ Network traffic
- ✅ Build history
- ✅ Deployment logs

## 🔒 Security Tips

1. ⚠️ **Git-এ sensitive data commit করবেন না!**
2. ✅ সব credentials Railway Variables-এ রাখুন
3. ✅ `.gitignore` চেক করুন
4. ✅ Production-এ strong JWT_SECRET ব্যবহার করুন
5. ✅ Admin password change করুন

## 📚 Additional Resources

- Railway Docs: https://docs.railway.app
- Railway Community: https://discord.gg/railway
- Neon Database: https://neon.tech/docs

## ✅ Deployment Checklist

প্রতি deployment এর আগে check করুন:

- [ ] Git repository updated
- [ ] All environment variables set in Railway
- [ ] Database connection tested
- [ ] API keys valid
- [ ] `.gitignore` properly configured
- [ ] Build succeeds locally (`mvn clean package`)
- [ ] No hardcoded secrets in code

## 🎉 Success!

একবার deploy হয়ে গেলে আপনার blog website live থাকবে:
- ✅ 24/7 available
- ✅ No sleep mode
- ✅ Automatic SSL
- ✅ Fast global CDN
- ✅ Auto-scaling

Happy Blogging! 🚀
