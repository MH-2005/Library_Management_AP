# راهنمای آپلود پروژه در گیت‌هاب

این فایل شامل دستورات لازم برای آپلود پروژه در گیت‌هاب است.

## مراحل ایجاد مخزن و آپلود پروژه

### ۱. ساخت مخزن (Repository) در گیت‌هاب

1. وارد حساب کاربری خود در گیت‌هاب شوید
2. روی دکمه "+" در گوشه بالا سمت راست کلیک کنید و "New repository" را انتخاب کنید
3. نام مخزن را "Library_Management_System" وارد کنید
4. توضیحات را به دلخواه وارد کنید، مثلاً: "A comprehensive Java-based library management system"
5. مخزن را عمومی (Public) یا خصوصی (Private) تنظیم کنید
6. گزینه "Initialize this repository with a README" را انتخاب نکنید (چون ما قبلاً یک README داریم)
7. روی دکمه "Create repository" کلیک کنید

### ۲. آماده‌سازی پروژه محلی با گیت

در ترمینال یا خط فرمان، در پوشه پروژه خود دستورات زیر را اجرا کنید:

```bash
# مقداردهی اولیه گیت
git init

# اضافه کردن تمام فایل‌ها
git add .

# ثبت اولین کامیت
git commit -m "Initial commit: Library Management System"

# ست کردن شاخه اصلی به main (اگر لازم است)
git branch -M main

# اضافه کردن مخزن راه دور (remote)
git remote add origin https://github.com/YOUR_USERNAME/Library_Management_System.git

# آپلود تغییرات
git push -u origin main
```

به جای `YOUR_USERNAME` نام کاربری خود در گیت‌هاب را قرار دهید.

### ۳. بررسی نهایی

1. مطمئن شوید تمام فایل‌های مهم آپلود شده‌اند
2. به مخزن گیت‌هاب خود در مرورگر مراجعه کنید و پروژه را بررسی کنید

## دستورات مفید گیت

```bash
# مشاهده وضعیت فعلی
git status

# مشاهده تاریخچه کامیت‌ها
git log

# ایجاد یک شاخه جدید
git checkout -b new-branch-name

# جابجایی بین شاخه‌ها
git checkout branch-name

# دریافت تغییرات از مخزن راه دور
git pull origin main

# آپلود تغییرات به مخزن راه دور
git push origin main
```

## تنظیمات اضافی (اختیاری)

### تنظیم GitHub Pages برای نمایش مستندات

1. در صفحه مخزن خود، به تب "Settings" بروید
2. در منوی سمت چپ، "Pages" را انتخاب کنید
3. در بخش "Source"، شاخه "main" را انتخاب کنید
4. روی "Save" کلیک کنید
5. پس از چند دقیقه، مستندات شما در آدرس `https://YOUR_USERNAME.github.io/Library_Management_System` قابل دسترس خواهد بود

### افزودن موضوعات (Topics) به مخزن

1. در صفحه اصلی مخزن، کنار توضیحات، روی چرخ‌دنده کلیک کنید
2. موضوعات مرتبط را اضافه کنید، مثلاً: `java`, `library-management`, `oop`, `inheritance`
3. روی "Save changes" کلیک کنید 