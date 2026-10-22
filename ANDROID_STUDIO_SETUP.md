# HomeCash — إعداد Android Studio وSupabase

## فتح المشروع
1. فك ضغط الأرشيف وافتح المجلد الذي يحتوي `settings.gradle.kts` في Android Studio.
2. ثبّت JDK المتوافق مع Gradle/Android Gradle Plugin وAndroid SDK 36 من SDK Manager.
3. أنشئ ملف خصائص Gradle للمستخدم وليس داخل المستودع:
   - Windows: `%USERPROFILE%\.gradle\gradle.properties`
   - macOS/Linux: `~/.gradle/gradle.properties`

   وأضف القيم الحقيقية التالية:

   ```properties
   HOME_CASH_SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
   HOME_CASH_SUPABASE_ANON_KEY=sb_publishable_...
   ```

4. شغّل محتوى `supabase/schema.sql` في SQL Editor بعد مراجعة المخطط.
5. فعّل Email provider، وحدد تأكيد البريد وروابط استعادة كلمة المرور وحدود الطلبات والحماية من إساءة الاستخدام من لوحة Supabase.
6. نفّذ Sync Gradle ثم `Build > Make Project`.

## الأمان
- لا تضع `service_role` أو كلمة مرور PostgreSQL أو مفاتيح مزودي الخدمة في APK أو المستودع.
- لا تعتمد على تحديد معدل الطلبات من الواجهة فقط؛ إعدادات Supabase/الخادم هي المرجع.
- اختبر RLS بحسابين منفصلين قبل إدخال بيانات حقيقية.
- التطبيق يمنع النسخ الاحتياطي التلقائي، ويمنع cleartext traffic، ولا يرسل بيانات مالية إلى عنوان Firebase تجريبي.
- توجد خطوة إعداد أولي تطلب صلاحيات SMS بشكل سياقي وتتيح اختيار نوع الحساب وإنشاء أول جهاز وخط ومحفظة محليًا. صلاحيات الإشعارات/الاستثناء من تحسين البطارية والاكتشاف التلقائي للـSIM تحتاج استكمالًا.

## حالة الوظائف بصراحة
- البريد/كلمة المرور وإنشاء الحساب وطلب استعادة كلمة المرور تستخدم Supabase Auth REST عند ضبط القيم أعلاه.
- `admin/admin` وضع Demo محلي منفصل فقط.
- Google OAuth يستخدم PKCE وcallback أصليًا عبر `com.homecash://auth/callback`. في Google Cloud اجعل عنوان إعادة التوجيه المسموح به هو `https://YOUR_PROJECT_REF.supabase.co/auth/v1/callback`، وفي Supabase أضف `com.homecash://auth/callback` إلى Redirect URLs وفعّل Google Provider. إدارة الجلسة الدائمة وتجديد refresh token غير مكتملة؛ قد يلزم تسجيل الدخول مجددًا بعد إعادة تشغيل التطبيق.
- مزامنة السجلات السحابية غير مفعّلة عمدًا حتى يتم ربط JWT وسياسات RLS ومخطط البيانات واستراتيجية معالجة التعارضات.
- ليست كل شاشات Stitch منقولة إلى Compose بعد. المراجع محفوظة في `design-reference/stitch/` لتسهيل إكمال النقل.
- لم يُؤكّد البناء النهائي لأن تنزيل Gradle من `services.gradle.org` فشل في بيئة العمل الحالية. نفّذ build محليًا قبل النشر.
