# 02 — المعمارية ونموذج البيانات (Architecture & Data Models)

تعتمد معمارية تطبيق **HomeCash** على مبادئ الـ **Clean Architecture** الحديثة مع نمط **Local-First** (المحلي أولاً) لضمان العمل التام حتى بدون اتصال بالإنترنت، مع المزامنة التلقائية السحابية فور توفر الشبكة.

---

## 1. شجرة حزم المشروع (Package Structure)

```
com.homecash/
├── HomeCashApp.kt                    # فئة التطبيق الرئيسية وتهيئة قواعد البيانات
├── MainActivity.kt                   # النشاط الأساسي، ملاحة Jetpack Compose، وطلب الأذونات
├── core/
│   ├── theme/                        # ألوان الديزاين سيستم المعتمد (#4F63E8, RTL, الخطوط)
│   ├── common/                       # منسقات العملات (ج.م) والتواريخ والأرقام
│   └── simulation/                   # محرك المحاكاة التجريبي لـ 3 هواتف ذكية
│       ├── MockEgyptianSmsTemplates.kt
│       ├── MockDataGenerator.kt
│       └── MockSmsSimulationService.kt
├── data/
│   ├── local/                        # التخزين المحلي باستخدام Room SQLite
│   │   ├── entity/                   # الجداول (User, Device, Line, Wallet, Transaction, Flag, ...)
│   │   ├── dao/                      # واجهات الاستعلامات التفاعلية Flow & Suspend
│   │   └── AppDatabase.kt            # نقطة الوصول الموحدة لقاعدة البيانات
│   ├── remote/                       # عميل المزامنة السحابية لقاعدة البيانات عبر الإنترنت
│   │   └── CloudSyncApi.kt
│   └── repository/                   # مستودع البيانات الموحد (Single Source of Truth)
│       └── HomeCashRepository.kt
├── domain/
│   ├── model/                        # كائنات النطاق المجردة (CounterpartySummary, ...)
│   └── parser/                       # محرك استخراج وتحليل رسائل SMS المصرية ومكافحة النصب
│       ├── SmsParserEngine.kt
│       ├── AntiFraudValidator.kt
│       └── ParsedSmsResult.kt
├── receiver/                         # مستقبل رسائل الـ SMS للنظام
│   └── SmsReceiver.kt
└── ui/
    ├── navigation/                   # شاشات ومسارات الملاحة والشريط السفلي
    ├── components/                   # المكونات المشتركة (كروت المحافظ، المعاملات، الفلاجات)
    ├── viewmodel/                    # طبقة إدارة الحالة (StateFlow & ViewModel)
    └── screens/                      # الشاشات التنفيذية للواجهة
        ├── auth/                     # شاشة الدخول وحساب التجربة
        ├── dashboard/                # لوحة التحكم الرئيسية (الوضع التجاري والشخصي)
        ├── transactions/             # سجل الحركات والبحث وكشف الحساب
        ├── reports/                  # التقارير المالية والسيولة والتصدير
        ├── settings/                 # إدارة الحساب، الأجهزة، والأمان
        ├── shift/                    # تقفيل الوردية والدرج
        └── flags/                    # حل الفلاجات والفروق المالية
```

---

## 2. نماذج البيانات (Data Entities Schema)

### 2.1 جدول المستخدمين (`UserEntity`)
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | المعرف الفريد (الافتراضي: "admin") |
| `email` | String | بريد الحساب |
| `name` | String | اسم صاحب الحساب |
| `mode` | String | وضع الواجهة: `"business"` (تجاري) أو `"personal"` (شخصي) |
| `businessName` | String | اسم النشاط أو المحل (مثل: محل الباشا فون) |
| `businessType` | String | نوع النشاط (محل موبايل، كشك، تاجر، صيانة) |
| `monthlyBudget` | Double | الميزانية الشهرية المقررة (للوضع الشخصي) |
| `pinCode` | String? | رمز PIN السري لقفل التطبيق |
| `isDemoMode` | Boolean | حالة تفعيل الوضع التجريبي |

### 2.2 جدول الأجهزة المتصلة (`DeviceEntity`)
يدعم ربط عدد غير محدود من هواتف الكاشيرات والإدارة على نفس الحساب.
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | معرف الجهاز |
| `userId` | String (FK) | معرف المستخدم |
| `deviceName` | String | اسم الجهاز المعروض (مثال: هاتف الفرع الثاني - كاشير 2) |
| `isThisDevice` | Boolean | هل هو الهاتف الحالي الذي يحمل التطبيق |
| `batteryOptDisabled` | Boolean | هل تم استثناء البطارية لضمان القراءة في الخلفية |
| `status` | String | حالة الاتصال: `"ONLINE"`, `"SYNCING"`, `"OFFLINE"` |
| `lastSyncAt` | Long | توقيت آخر مزامنة تم استلامها من الهاتف |

### 2.3 جدول الخطوط وشرائح الـ SIM (`LineEntity`)
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | معرف الشريحة |
| `deviceId` | String (FK) | معرف الهاتف المسجلة عليه الشريحة |
| `phoneNumber` | String | رقم الهاتف (مثال: 01023455842) |
| `carrier` | String | اسم الشبكة: Vodafone, Etisalat, Orange, WE |
| `displayName` | String | اسم وصفي (شريحة كاشير 1) |
| `simSlotIndex` | Int | منفذ الشريحة في الجهاز (Slot 1 / 2) |

### 2.4 جدول المحافظ المالية (`WalletEntity`)
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | معرف المحفظة |
| `lineId` | String (FK) | معرف الخط المرتبطة به |
| `providerName` | String | فودافون كاش، إنستاباي، اتصالات كاش، أورنج كاش، وي باي، البنوك |
| `currentBalance` | Double | الرصيد الحي الحالي للمحفظة |
| `initialBalance` | Double | الرصيد الابتدائي عند فتح الحساب |
| `sendFeePct` | Double | نسبة عمولة التحويل (مثال: 1%) |
| `receiveFeePct` | Double | نسبة عمولة الاستلام (مثال: 0.5%) |
| `colorHex` | String | لون الشركة لتمييز البطاقة بالواجهة |

### 2.5 جدول المعاملات (`TransactionEntity`)
يحتوي فهارس فريدة تمنع تكرار أي رسالة.
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | المعرف الفريد للمعاملة |
| `walletId` | String (FK) | المحفظة المنفذة للعملية |
| `deviceId` | String (FK) | الهاتف الذي استقبل الرسالة |
| `fingerprint` | String (Unique) | بصمة فريدة: `كود العملية` + `المبلغ` + `الرقم` |
| `direction` | String | `"IN"` (وارد/استلام) أو `"OUT"` (صادر/تحويل) |
| `amount` | Double | قيمة المعاملة الصافية |
| `fee` | Double | مصاريف خدمة الشبكة المذكورة بالرسالة |
| `cashierCommission`| Double | عمولة الكاشير أو صافي ربح المحل من العملية |
| `balanceAfter` | Double | رصيد المحفظة بعد العملية كما ورد بالرسالة |
| `counterpartyNumber`| String | رقم هاتف الطرف الآخر (العميل أو المورد) |
| `counterpartyName` | String | اسم الطرف الآخر في دفتر العناوين |
| `operationCode` | String | كود العملية المعتمد من الشركة |
| `occurredAt` | Long | التوقيت الفعلي للعملية |
| `status` | String | `"CONFIRMED"`, `"FLAGGED"`, `"MANUAL"`, `"RECONCILED"` |
| `category` | String | التصنيف: تحويل كاش، فواتير، مشتريات، صيانة، ... |
| `rawText` | String | النص الحرفي لرسالة الـ SMS الأصلية للرجوع إليه |

### 2.6 جدول الفلاجات والتنبيهات التدقيقية (`FlagEntity`)
| الحقل | النوع | الوصف |
| :--- | :--- | :--- |
| `id` | String (PK) | معرف الفلاج |
| `walletId` | String (FK) | المحفظة المعنية |
| `type` | String | نوع الفلاج: `BALANCE_MISMATCH`, `MISSING_TX`, `DUPLICATE`, `UNKNOWN_SMS` |
| `status` | String | `"OPEN"`, `"RESOLVED"`, `"IGNORED"` |
| `title` | String | عنوان التنبيه (مثال: فرق في رصيد فودافون كاش) |
| `explanation` | String | شرح المشكلة والفارق المحسوب |
| `discrepancy` | Double | قيمة الفارق المالي المعلق |
| `resolutionAction` | String? | الإجراء الذي تم اتخاذه للتسوية |

---

## 3. تدفق البيانات (Reactive Data Pipeline)

تستخدم الواجهة نموذج تدفق أحادي الاتجاه (Unidirectional Data Flow):
1. وصول رسالة SMS ← التقاط عبر `SmsReceiver` أو محاكاة عبر `MockSmsSimulationService`.
2. استخراج الحقول والمطابقة عبر `SmsParserEngine`.
3. التحقق من التكرار وبصمة العملية والتحقق من الرصيد داخل `HomeCashRepository`.
4. إدراج العملية وتحديث رصيد المحفظة في Room Database.
5. إصدار تحديث عبر `StateFlow` في `DashboardViewModel` و `TransactionsViewModel`.
6. إعادة رسم الواجهة في Jetpack Compose تلقائياً وفورياً.
7. رفع العملية إلى السحابة عبر `CloudSyncApi`.
