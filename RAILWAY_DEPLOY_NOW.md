# 🚀 Railway Deployment - এখনই শুরু করুন!

## ✅ সব প্রস্তুত! এখন শুধু এই steps follow করুন:

### Step 1: Railway Login করুন

Terminal খুলে এই command টি run করুন:

```bash
cd /home/kali/Downloads/blogwebsite
railway login
```

এটি একটা browser window খুলবে। সেখানে:
1. Railway.app-এ sign up/login করুন (GitHub দিয়ে login সবচেয়ে সহজ)
2. "Authorize Railway" ক্লিক করুন
3. Terminal এ ফিরে আসুন

### Step 2: Railway Project তৈরি করুন

```bash
railway init
```

প্রশ্ন আসলে:
- Project name: **blog-website** (অথবা আপনার পছন্দ মতো)
- "Create empty project" সিলেক্ট করুন

### Step 3: Environment Variables সেট করুন

Railway Dashboard খুলুন এবং Variables section এ যান:

```bash
railway open
```

এই variables গুলো add করুন:

#### Database:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://ep-fragrant-sea-a1edrh7r-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=npg_7uAziTVoml6R
```

#### API Keys:
```
OPENROUTER_API_KEY=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
CHATBOT_API_KEY=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
```

#### Security:
```
JWT_SECRET=railway-blog-secure-jwt-secret-key-2024-change-this
ADMIN_USERNAME=admin
ADMIN_PASSWORD=SecureAdminPass123!
```

#### Spring Settings:
```
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
FILE_UPLOAD_DIR=/app/uploads
```

### Step 4: Deploy করুন! 🚀

```bash
railway up
```

অথবা quick deploy script:

```bash
./deploy.sh
```

### Step 5: Deployment দেখুন

```bash
# Live logs দেখুন
railway logs

# Service status
railway status

# Website খুলুন
railway open
```

---

## 🎯 Quick Commands Reference:

```bash
# Login
railway login

# Project create
railway init

# Deploy
railway up

# Logs
railway logs

# Open dashboard
railway open

# Add variable
railway variables set KEY=VALUE

# Check status
railway status
```

---

## ✅ Deployment Checklist:

- [x] Git repository initialized
- [x] Railway CLI installed
- [x] Configuration files ready (railway.toml, nixpacks.toml)
- [x] Application.properties updated for Railway
- [ ] Railway login করতে হবে (আপনার করা বাকি)
- [ ] Project create করতে হবে
- [ ] Environment variables set করতে হবে
- [ ] Deploy command run করতে হবে

---

## 📞 Help:

যদি কোনো সমস্যা হয়:

1. **Build failed?** → `mvn clean package -DskipTests` local এ test করুন
2. **Environment variables missing?** → Railway dashboard check করুন
3. **Database connection error?** → Neon database active আছে কিনা verify করুন

---

## 🎉 Success Indicators:

Deploy successful হলে দেখবেন:
- ✅ Build completed
- ✅ Deployment live
- ✅ Public URL পাবেন: `https://your-app.railway.app`
- ✅ Website accessible

Happy Deploying! 🚀
