#!/bin/bash

# Railway Deployment Script
# এই script run করার আগে নিশ্চিত করুন:
# 1. Railway CLI installed আছে
# 2. Railway login করা আছে

echo "🚂 Railway Deployment শুরু হচ্ছে..."
echo ""

# Check if Railway CLI is installed
if ! command -v railway &> /dev/null
then
    echo "❌ Railway CLI পাওয়া যায়নি!"
    echo "Install করুন: npm i -g @railway/cli"
    exit 1
fi

echo "✅ Railway CLI পাওয়া গেছে"
echo ""

# Check if logged in
echo "🔐 Railway login checking..."
railway whoami &> /dev/null
if [ $? -ne 0 ]; then
    echo "❌ আপনি Railway-তে login করেননি!"
    echo "Login করুন: railway login"
    exit 1
fi

echo "✅ Railway login verified"
echo ""

# Build locally first (optional)
echo "🔨 Local build test করছি..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed! Code ঠিক করুন এবং আবার try করুন।"
    exit 1
fi

echo "✅ Build successful"
echo ""

# Deploy to Railway
echo "🚀 Railway-এ deploy করছি..."
railway up

echo ""
echo "🎉 Deployment complete!"
echo ""
echo "📊 Logs দেখতে: railway logs"
echo "🌐 Site খুলতে: railway open"
echo "📈 Status দেখতে: railway status"
