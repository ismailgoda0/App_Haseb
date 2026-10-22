package com.homecash.ui.screens.settings

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.notifications.AppNotificationCenter
import com.homecash.core.theme.*
import com.homecash.ui.viewmodel.SettingsViewModel

/**
 * Exact Stitch Screen _17 Parity — General Settings (الإعدادات العامة — دفتر المحافظ)
 * Replicates stitch_\_17 layout:
 * - Top header with Back + Amber Demo Mode Pill ("وضع تجريبي — بيانات وهمية") + Action
 * - Profile Card with Storefront Icon, Store Name, Cashier, Phone, Email, Merchant Badge & Edit Modal
 * - Group 1: الحساب (الملف الشخصي، نوع الاستخدام، الأجهزة المرتبطة)
 * - Group 2: الخطوط والمحافظ (إدارة الخطوط، قواعد ونسب العمولات)
 * - Group 3: قراءة الرسائل (قراءة تلقائية مع سويتش، مراجعة الرسائل، مطابقة الرصيد)
 * - Group 4: التقارير والبيانات (النسخ والتصدير، إقفال الوردية، فريق العمل)
 * - Group 5: المظهر والأمان (اللغة والمظهر، قفل التطبيق، إخفاء الأرصدة)
 * - Group 6: حول التطبيق (الدعم الفني، سياسة الخصوصية، إصدار التطبيق، تسجيل الخروج)
 * - Fast Simulation Triggers
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogout: () -> Unit,
    onNavigateToShiftClose: () -> Unit = {},
    onNavigateToFlagResolution: () -> Unit = {}
) {
    val context = LocalContext.current
    val view = LocalView.current
    val user by viewModel.user.collectAsState()
    val isSimRunning by viewModel.isSimulationRunning.collectAsState()

    var showEditStoreModal by remember { mutableStateOf(false) }
    var storeNameState by remember { mutableStateOf(user?.businessName ?: "محل الباشا فون") }
    var cashierNameState by remember { mutableStateOf(user?.name ?: "محمد فوزي") }

    var autoReadSms by remember { mutableStateOf(true) }
    var appLockEnabled by remember { mutableStateOf(true) }
    var hideBalancesDefault by remember { mutableStateOf(false) }

    var showToastMessage by remember { mutableStateOf<String?>(null) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showFastSimSheet by remember { mutableStateOf(false) }

    LaunchedEffect(showToastMessage) {
        if (showToastMessage != null) {
            kotlinx.coroutines.delay(2500)
            showToastMessage = null
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = Surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "الإعدادات",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Stitch Demo Mode Pill (stitch_\_17)
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFFFF7ED),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD9A8))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSimRunning) Color(0xFFF59E0B) else Color.Gray)
                            )
                            Text(
                                text = if (isSimRunning) "وضع تجريبي: محاكاة 3 هواتف" else "المحاكاة متوقفة",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFFDE68A),
                                modifier = Modifier.clickable {
                                    AppSoundManager.click(view)
                                    viewModel.toggleSimulation(!isSimRunning)
                                    showToastMessage = if (!isSimRunning) "تم تشغيل محاكاة الهواتف بنجاح" else "تم إيقاف المحاكاة مؤقتاً"
                                }
                            ) {
                                Text(
                                    text = if (isSimRunning) "إنهاء التجربة" else "تشغيل التجربة",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78350F),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Card (stitch_\_17)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFE5E6FF))
                                        .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = storeNameState,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "/ $cashierNameState",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }

                                    Text(
                                        text = "01023455842 • basha.phone@gmail.com",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Primary.copy(alpha = 0.1f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.2f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Store, null, tint = Primary, modifier = Modifier.size(13.dp))
                                            Text(
                                                text = "محل / تاجر",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Primary
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    AppSoundManager.click(view)
                                    showEditStoreModal = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFFF4F2FF),
                                    contentColor = Primary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDFE2F0))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("تعديل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Interactive Demo Simulation Button (Sandbox)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                AppSoundManager.click(view)
                                showFastSimSheet = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF1FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Bolt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(
                                        text = "لوحة التحكم السريعة في المحاكاة (Sandbox)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "إرسال عمليات كاش فورية واختبار الإشعارات والأصوات بيدك",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = Primary, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                // Group 1: الحساب
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.ManageAccounts,
                        title = "الحساب"
                    ) {
                        SettingsNavRow(
                            icon = Icons.Default.Person,
                            title = "الملف الشخصي",
                            subtitle = "تعديل بيانات صاحب المحل والاسم التجاري",
                            onClick = {
                                AppSoundManager.click(view)
                                showEditStoreModal = true
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Badge,
                            title = "نوع الاستخدام",
                            subtitle = "محل / تاجر (مفعل)",
                            badgeText = "محل",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "نمط التاجر والمحل مفعل بجميع المزايا"
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Devices,
                            title = "الأجهزة المرتبطة",
                            subtitle = "هاتف سامسونج A54 وجهاز كاشير إضافي",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "جهازان متصلان ومتزامنان لحظياً مع الحساب"
                            }
                        )
                    }
                }

                // Group 2: الخطوط والمحافظ
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.SimCard,
                        title = "الخطوط والمحافظ"
                    ) {
                        SettingsNavRow(
                            icon = Icons.Default.AccountBalanceWallet,
                            iconContainerColor = Primary,
                            iconColor = Color.White,
                            title = "إدارة الخطوط والمحافظ",
                            subtitle = "تهيئة شرائح SIM 1 و SIM 2 ومحافظ الكاش",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "الشرائح المكتشفة: فودافون كاش (SIM 1) • اتصالات كاش (SIM 2)"
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Percent,
                            title = "قواعد ونسب العمولات",
                            subtitle = "تحديد نسب السحب والإيداع وأرباح المحل",
                            badgeText = "تاجر فقط",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "نسبة العمولة الافتراضية المطبقة: 1.0%"
                            }
                        )
                    }
                }

                // Group 3: قراءة الرسائل
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.Sms,
                        title = "قراءة الرسائل"
                    ) {
                        SettingsToggleRow(
                            icon = Icons.Default.Sync,
                            title = "قراءة الرسائل تلقائياً",
                            subtitle = if (autoReadSms) "مفعّلة — يتم رصد إشعارات التحويل لحظياً" else "متوقفة — يتطلب تسجيل المعاملات يدوياً",
                            subtitleColor = if (autoReadSms) SuccessGreen else TextSecondary,
                            checked = autoReadSms,
                            onCheckedChange = {
                                AppSoundManager.click(view)
                                autoReadSms = it
                                showToastMessage = if (it) "تم تفعيل قراءة الرسائل اللحظية" else "تم إيقاف القراءة التلقائية"
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.MarkChatUnread,
                            title = "مراجعة الرسائل",
                            subtitle = "تدقيق الرسائل المشكوك فيها أو غير المكتملة",
                            badgeText = "3 معلقة",
                            badgeBg = Color(0xFFFFFBEB),
                            badgeColor = Color(0xFFB45309),
                            onClick = {
                                AppSoundManager.click(view)
                                onNavigateToFlagResolution()
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Radar,
                            title = "مطابقة الرصيد",
                            subtitle = "كاشف الفجوات وفحص تسلسل الأرصدة",
                            onClick = {
                                AppSoundManager.click(view)
                                onNavigateToFlagResolution()
                            }
                        )
                    }
                }

                // Group 4: التقارير والبيانات
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.Analytics,
                        title = "التقارير والبيانات"
                    ) {
                        SettingsNavRow(
                            icon = Icons.Default.FileDownload,
                            title = "النسخ والتصدير",
                            subtitle = "تصدير Excel أو PDF ومشاركة كبسولة واتساب",
                            onClick = {
                                AppSoundManager.cashRegister(view)
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "تقرير كشف حساب كامل — دفتر المحافظ HomeCash\nإجمالي العمليات: 41 معاملة\nصافي السيولة: 27,700 ج.م")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "تصدير التقرير"))
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.PointOfSale,
                            title = "إقفال الوردية",
                            subtitle = "مطابقة الخزينة النقدية واعتماد إقفال اليومية",
                            badgeText = "تاجر",
                            onClick = {
                                AppSoundManager.click(view)
                                onNavigateToShiftClose()
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Groups,
                            title = "فريق العمل والصلاحيات",
                            subtitle = "تحديد صلاحيات الكاشير والاطلاع على الأرباح",
                            badgeText = "محل",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "صلاحيات الكاشير: عرض العمليات وتقفيل الوردية"
                            }
                        )
                    }
                }

                // Group 5: المظهر والأمان
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.Shield,
                        title = "المظهر والأمان"
                    ) {
                        SettingsNavRow(
                            icon = Icons.Default.Palette,
                            title = "اللغة والمظهر",
                            subtitle = "العربية • المظهر الفاتح (Light Mode)",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "اللغة: العربية (افتراضي)"
                            }
                        )
                        SettingsToggleRow(
                            icon = Icons.Default.Lock,
                            title = "قفل التطبيق",
                            subtitle = "حماية الدفتر برمز PIN أو البصمة",
                            checked = appLockEnabled,
                            onCheckedChange = {
                                AppSoundManager.click(view)
                                appLockEnabled = it
                                showToastMessage = if (it) "تم تفعيل القفل بالبصمة ورمز PIN" else "تم تعطيل قفل التطبيق"
                            }
                        )
                        SettingsToggleRow(
                            icon = Icons.Default.VisibilityOff,
                            title = "إخفاء الأرصدة افتراضياً",
                            subtitle = "تمويه المبالغ النقدية في الشاشة الرئيسية",
                            checked = hideBalancesDefault,
                            onCheckedChange = {
                                AppSoundManager.click(view)
                                hideBalancesDefault = it
                                showToastMessage = if (it) "تم تفعيل إخفاء الأرصدة الحساسة" else "تم إظهار الأرصدة كاملة"
                            }
                        )
                    }
                }

                // Group 6: حول التطبيق
                item {
                    SettingsGroupSection(
                        icon = Icons.Default.Info,
                        title = "حول التطبيق"
                    ) {
                        SettingsNavRow(
                            icon = Icons.Default.SupportAgent,
                            title = "الدعم الفني المباشر",
                            subtitle = "تواصل مع فريق الدعم والمساعدة",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "جاري تحويلك لممثل الدعم الفني الفوري..."
                            }
                        )
                        SettingsNavRow(
                            icon = Icons.Default.Policy,
                            title = "سياسة الخصوصية وشروط الاستخدام",
                            subtitle = "معالجة الرسائل محلياً على جهازك بأمان تام",
                            onClick = {
                                AppSoundManager.click(view)
                                showToastMessage = "البيانات مشفرة ومحفوظة بالكامل على هاتفك دون مشاركة طرف ثالث"
                            }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFECECFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Verified, null, tint = Primary, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(
                                        text = "إصدار التطبيق",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "دفتر المحافظ HomeCash v1.0.0",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color(0xFFECECFF)
                            ) {
                                Text(
                                    text = "v1.0.0",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Logout Button Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AppSoundManager.click(view)
                                    showLogoutConfirm = true
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ErrorContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                                }
                                Column {
                                    Text(
                                        text = "تسجيل الخروج",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = ErrorRed
                                    )
                                    Text(
                                        text = "الخروج من الحساب والعودة لشاشة الدخول",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowBackIos, null, tint = ErrorRed, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Interactive Toast
            AnimatedVisibility(
                visible = showToastMessage != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF181A2C),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFDFE0FF), modifier = Modifier.size(16.dp))
                        Text(
                            text = showToastMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal: Edit Store Details
    if (showEditStoreModal) {
        AlertDialog(
            onDismissRequest = { showEditStoreModal = false },
            title = { Text("تعديل بيانات المحل والكاشير", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = storeNameState,
                        onValueChange = { storeNameState = it },
                        label = { Text("اسم المتجر") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = cashierNameState,
                        onValueChange = { cashierNameState = it },
                        label = { Text("اسم الكاشير المسؤول") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AppSoundManager.click(view)
                        showEditStoreModal = false
                        showToastMessage = "تم حفظ بيانات المتجر بنجاح"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حفظ البيانات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStoreModal = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Modal: Fast Simulation Sandbox Sheet
    if (showFastSimSheet) {
        AlertDialog(
            onDismissRequest = { showFastSimSheet = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Bolt, null, tint = Primary)
                    Text("إرسال عملية وهمية فورية", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اضغط على أي عملية لإطلاق إشعار حقيقي فوري وسماع صوت الكاشير:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Button(
                        onClick = {
                            AppSoundManager.cashRegister(view)
                            AppNotificationCenter.post(
                                title = "وارد كاش: +500.00 ج.م ⚡ فودافون كاش",
                                message = "تم استلام 500 ج.م من 01012345678 على خط المحل الأساسي (SIM 1). الرصيد الجديد: 15,950 ج.م",
                                category = "وارد"
                            )
                            showFastSimSheet = false
                            showToastMessage = "تم إرسال عملية وارد فودافون كاش 500 ج.م!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VodafoneRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+500 ج.م — وارد فودافون كاش")
                    }

                    Button(
                        onClick = {
                            AppSoundManager.cashRegister(view)
                            AppNotificationCenter.post(
                                title = "صادر كاش: -1,000.00 ج.م ⚡ إنستاباي InstaPay",
                                message = "تم تحويل 1,000 ج.م إلى حساب 01098765432 عبر إنستاباي. عمولة الكاشير: +10.00 ج.م",
                                category = "صادر"
                            )
                            showFastSimSheet = false
                            showToastMessage = "تم إرسال عملية تحويل إنستاباي 1,000 ج.م!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = InstaPayPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("-1,000 ج.م — صادر إنستاباي InstaPay")
                    }

                    Button(
                        onClick = {
                            AppSoundManager.click(view)
                            AppNotificationCenter.post(
                                title = "تنبيه فلاج: اختلاف رصيد بمقدار 100 ج.م",
                                message = "رصيد رسالة اتصالات كاش يختلف عن الرصيد الدفتري المسجل.",
                                category = "مراجعة"
                            )
                            showFastSimSheet = false
                            showToastMessage = "تم إنشاء فلاج اختلاف رصيد تجريبي!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("⚠️ فلاج: محاكاة اختلاف رصيد 100 ج.م")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFastSimSheet = false }) {
                    Text("إغلاق")
                }
            }
        )
    }

    // Modal: Logout Confirm
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("تأكيد تسجيل الخروج", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في تسجيل الخروج والعودة لشاشة الدخول؟") },
            confirmButton = {
                Button(
                    onClick = {
                        AppSoundManager.click(view)
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("نعم، تسجيل الخروج")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SettingsGroupSection(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    iconContainerColor: Color = Color(0xFFECECFF),
    iconColor: Color = Primary,
    title: String,
    subtitle: String,
    badgeText: String? = null,
    badgeBg: Color = Color(0xFFECECFF),
    badgeColor: Color = Primary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconContainerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = badgeBg
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    subtitleColor: Color = TextSecondary,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFECECFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = subtitleColor
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFC5C5D7)
            )
        )
    }
}
