# سیستم مدیریت کتابخانه

سیستم مدیریت کتابخانه یک برنامه جاوا برای مدیریت منابع کتابخانه‌ای، کاربران و عملیات امانت است. این سیستم قابلیت‌های متنوعی را برای مدیریت کتاب‌ها، پایان‌نامه‌ها، کتاب‌های گنجینه، گزارش‌گیری و غیره فراهم می‌کند.

## ویژگی‌ها

- **مدیریت منابع**: افزودن، حذف و جستجوی انواع منابع (کتاب، پایان‌نامه، کتاب گنجینه، کتاب فروشی)
- **مدیریت کاربران**: افزودن و حذف انواع کاربران (دانشجو، استاد، کارمند، مدیر)
- **مدیریت امانت**: امانت گرفتن، برگرداندن منابع و محاسبه جریمه‌ها
- **مدیریت دسته‌بندی‌ها**: ایجاد و مدیریت دسته‌بندی‌های منابع
- **گزارش‌گیری**: گزارش‌های متنوع از وضعیت منابع، امانت‌ها، محبوبیت منابع و...

## ساختار پروژه

پروژه از کلاس‌های مختلفی تشکیل شده است:

- **Main**: نقطه ورود برنامه و کلاس اصلی
- **CommandHandeling**: پردازش دستورات کاربر
- **User** و زیرکلاس‌های آن: مدیریت کاربران مختلف
- **Resource** و زیرکلاس‌های آن: مدیریت انواع منابع
- **Library**: مدیریت کتابخانه‌ها و عملیات مربوط به آن‌ها
- **Category**: مدیریت دسته‌بندی‌های منابع

## نحوه استفاده

برنامه از طریق خط فرمان کار می‌کند و دستورات به فرمت خاصی وارد می‌شوند:

```
command#param1|param2|...
```

برای مثال:

```
add-library#admin#AdminPass#lib1#Central Library#2022#50#Tehran
```

برای پایان دادن به برنامه، دستور `finish` را وارد کنید.

## دستورات اصلی

### مدیریت کتابخانه
- `add-library#adminId#adminPass#libraryId#libraryName#year#desks#address`
- `add-category#adminId#adminPass#categoryId#categoryName#parentCategoryId`

### مدیریت کاربران
- `add-student#adminId#adminPass#studentId#studentPass#firstName#lastName#nationalCode#birthYear#address`
- `add-manager#adminId#adminPass#managerId#managerPass#firstName#lastName#nationalCode#birthYear#address#libraryId`
- `add-staff#adminId#adminPass#staffId#staffPass#firstName#lastName#nationalCode#birthYear#address#professor|staff`
- `remove-user#adminId#adminPass#userIdToRemove`

### مدیریت منابع
- `add-book#managerId#managerPass#bookId#title#author#publisher#year#copies#category#libraryId`
- `add-thesis#managerId#managerPass#thesisId#title#author#studentName#year#category#libraryId`
- `add-ganjineh-book#managerId#managerPass#bookId#title#author#publisher#year#donator#category#libraryId`
- `add-selling-book#managerId#managerPass#bookId#title#author#publisher#year#copies#price#discount#category#libraryId`
- `remove-resource#managerId#managerPass#resourceId#libraryId`

### عملیات امانت/برگشت/خرید
- `borrow#userId#userPass#libraryId#resourceId#date#time`
- `return#userId#userPass#libraryId#resourceId#date#time`
- `buy#userId#userPass#libraryId#resourceId`
- `read#userId#userPass#libraryId#resourceId#date#time`

### سایر عملیات
- `add-comment#userId#userPass#libraryId#resourceId#commentText`
- `search#searchPhrase`
- `search-user#userId#userPass#searchPhrase`

### گزارش‌گیری
- `library-report#managerId#managerPass#libraryId`
- `category-report#managerId#managerPass#categoryId#libraryId`
- `report-passed-deadline#managerId#managerPass#libraryId#date#time`
- `report-penalties-sum#adminId#adminPass`
- `report-sell#managerId#managerPass#libraryId`
- `report-most-popular#managerId#managerPass#libraryId`

## بهینه‌سازی‌های اخیر

- بهبود کارایی با استفاده از ساختارهای داده مناسب مانند HashMap به جای HashSet
- بهبود ساختار کد و جداسازی مسئولیت‌ها
- بهبود مدیریت خطاها و استثناها

## مشارکت

لطفاً فایل [CONTRIBUTING.md](CONTRIBUTING.md) را برای جزئیات مربوط به نحوه مشارکت در این پروژه مطالعه کنید.

## لایسنس

این پروژه تحت لایسنس MIT منتشر شده است. برای جزئیات بیشتر به فایل [LICENSE](LICENSE) مراجعه کنید.
