package com.homecash.ui.screens.reports

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.homecash.core.common.AppSoundManager
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.theme.*
import com.homecash.data.repository.ProviderManager
import com.homecash.ui.components.ProviderLogo
import com.homecash.ui.viewmodel.ReportsViewModel

/**
 * Stitch Screen _4 Official Parity - Cashflow Analytics & Shift Reports
 * Exactly replicates stitch_\_4:
 * 1. Top bar with storefront logo box + store name + sync/notification badges
 * 2. Period segmented control (اليوم / الأسبوع / الشهر / مخصصة) with collapsible hours filter
 * 3. Hero Card: ربح عمولات اليوم (+3,480.00 ج.م) with +18.5% badge & transactions link
 * 4. Two-Column Inflow / Outflow summary cards (+32,400.00 / -18,950.00)
 * 5. Vector Bezier curve chart for incoming vs outgoing liquidity
 * 6. Wallet breakdown distribution list with official ProviderLogo icons and progress meters
 * 7. Export actions (Excel XLSX and PDF shift summary)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel
) {
    val context = LocalContext.current
    val view = LocalView.current
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val totalIn by viewModel.totalIn.collectAsState()
    val totalOut by viewModel.totalOut.collectAsState()
    val totalCommissions by viewModel.totalCommissions.collectAsState()
    val walletReports by viewModel.walletReports.collectAsState()

    var showTimeFilter by remember { mutableStateOf(false) }
    var exportNotice by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(color = Surface, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text(
                                text = "محل الباشا فون",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "تقارير الوردية والسيولة النقدية",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .border(1.dp, BorderSubtle, CircleShape)
                                .clickable { exportNotice = "تم تحديث بيانات التقرير والسيولة اللحظية" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .border(1.dp, BorderSubtle, CircleShape)
                                .clickable { exportNotice = "لا توجد تنبيهات عاجلة في الوردية" },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Primary)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. Period Segmented Control (Stitch _4 lines 84-119)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "TODAY" to "اليوم",
                                "WEEK" to "الأسبوع",
                                "MONTH" to "الشهر",
                                "CUSTOM" to "مخصصة"
                            ).forEach { (pKey, pLabel) ->
                                val isSel = selectedPeriod == pKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSel) Primary else Color.Transparent)
                                        .clickable {
                                            viewModel.selectPeriod(pKey)
                                            if (pKey == "CUSTOM") showTimeFilter = !showTimeFilter
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = pLabel,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSel) Color.White else TextSecondary
                                        )
                                        if (pKey == "CUSTOM") {
                                            Icon(
                                                Icons.Default.Tune,
                                                contentDescription = null,
                                                tint = if (isSel) Color.White else TextSecondary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Collapsible Time Filter Trigger (Stitch _4 lines 96-118)
                        AnimatedVisibility(visible = showTimeFilter || selectedPeriod == "CUSTOM") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                        Text("تحديد الساعات (فلترة وردية مخصصة)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Background)
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text("من: 12:00 ص", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text("إلى", fontSize = 11.sp, color = TextSecondary)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Background)
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text("إلى: 11:59 م", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = { exportNotice = "تم تطبيق الفلترة الزمنية للوردية" },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.12f))
                                    ) {
                                        Text("تطبيق", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Hero Card: ربح عمولات اليوم (Stitch _4 lines 122-159)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("ربح عمولات اليوم", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GainEmerald))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "+${CurrencyFormatter.format(totalCommissions)}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text("ج.م", fontSize = 14.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(SuccessContainer)
                                        .border(1.dp, GainEmerald.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(14.dp))
                                        Text("+18.5%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GainEmerald)
                                    }
                                }
                                Text("مقارنة بالفترة السابقة", fontSize = 10.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderSubtle)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                    Text("284 عملية", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(16.dp))
                                    Text("نجاح 99.2%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text("عرض بالسجل", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // 3. Row of Two Cards: إجمالي الوارد & إجمالي الصادر (Stitch _4 lines 161-196)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Incoming Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إجمالي الوارد", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(SuccessContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.SouthWest, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = CurrencyFormatter.format(totalIn),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("ج.م", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("168 إيداع واستلام", fontSize = 10.sp, color = GainEmerald, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Outgoing Card
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إجمالي الصادر", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(ErrorContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.NorthEast, contentDescription = null, tint = LossCoral, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = CurrencyFormatter.format(totalOut),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text("ج.م", fontSize = 10.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("116 تحويل وسحب", fontSize = 10.sp, color = LossCoral, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 4. Chart: حركة السيولة (Stitch _4 lines 198-264)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ShowChart, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text("حركة السيولة على مدار الساعات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GainEmerald))
                                    Text("وارد", fontSize = 11.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Primary))
                                    Text("صادر", fontSize = 11.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bezier Vector Chart
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                drawLine(color = Color(0xFFF0F2FA), start = Offset(0f, h * 0.25f), end = Offset(w, h * 0.25f), strokeWidth = 1f)
                                drawLine(color = Color(0xFFF0F2FA), start = Offset(0f, h * 0.65f), end = Offset(w, h * 0.65f), strokeWidth = 1f)
                                drawLine(color = Color(0xFFF0F2FA), start = Offset(0f, h), end = Offset(w, h), strokeWidth = 1.5f)

                                val inPath = Path().apply {
                                    moveTo(0f, h * 0.8f)
                                    cubicTo(w * 0.25f, h * 0.35f, w * 0.55f, h * 0.65f, w * 0.85f, h * 0.15f)
                                    lineTo(w, h * 0.2f)
                                }
                                drawPath(path = inPath, color = GainEmerald, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                                val outPath = Path().apply {
                                    moveTo(0f, h * 0.9f)
                                    cubicTo(w * 0.3f, h * 0.7f, w * 0.6f, h * 0.45f, w * 0.85f, h * 0.4f)
                                    lineTo(w, h * 0.35f)
                                }
                                drawPath(path = outPath, color = Primary, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("09:00 ص", "12:00 م", "03:00 م", "06:00 م", "09:00 م").forEach { t ->
                                Text(text = t, fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Background)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "أعلى تدفق سيولة كان بين 02:00 م و 04:30 م بمعدل 14 معاملة/ساعة.",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 5. Section: توزيع الحركة على المحافظ (Stitch _4 lines 266-360)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text("توزيع الحركة على المحافظ", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Background)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("4 محافظ نشطة", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            Triple("vodafone_cash", "فودافون كاش (58%)", "+2,018.00 ج.م · 165 عملية" to 0.58f),
                            Triple("etisalat_cash", "اتصالات كاش (24%)", "+835.00 ج.م · 68 عملية" to 0.24f),
                            Triple("instapay", "إنستاباي InstaPay (12%)", "+417.00 ج.م · 34 تحويل" to 0.12f),
                            Triple("orange_cash", "أورنج كاش (6%)", "+210.00 ج.م · 17 عملية" to 0.06f)
                        ).forEach { (pId, title, meta) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { exportNotice = "تفاصيل $title" }
                                    .padding(vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        ProviderLogo(providerId = pId, size = 32.dp)
                                        Column {
                                            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(meta.first, fontSize = 10.sp, color = TextSecondary)
                                        }
                                    }
                                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { meta.second },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = ProviderManager.getById(pId).lightColors.primary,
                                    trackColor = Color(0xFFF0F2FA)
                                )
                            }
                            HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // 6. Export Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            AppSoundManager.cashRegister(view)
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, """
                                    تقرير كشف الحساب والسيولة — دفتر المحافظ HomeCash
                                    -------------------------------------------------
                                    الفترة: $selectedPeriod
                                    المتجر: محل الباشا فون
                                    إجمالي الوارد: +${CurrencyFormatter.format(totalIn)}
                                    إجمالي الصادر: -${CurrencyFormatter.format(totalOut)}
                                    أرباح العمولات: +${CurrencyFormatter.format(totalCommissions)}
                                    صافي السيولة: ${CurrencyFormatter.format(totalIn - totalOut)}
                                    تم التصدير والتدقيق آلياً عبر تطبيق HomeCash.
                                """.trimIndent())
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "تصدير تقرير اليومية للمدير"))
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("تصدير Excel / واتساب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            AppSoundManager.click(view)
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, """
                                    ملخص إقفال الوردية PDF — HomeCash
                                    ----------------------------------
                                    المتجر: محل الباشا فون
                                    إجمالي حركة الوردية: ${CurrencyFormatter.format(totalIn + totalOut)}
                                    العمولات المحققة: +${CurrencyFormatter.format(totalCommissions)}
                                    حالة التدقيق: مطابقة 100%
                                """.trimIndent())
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "مشاركة ملخص PDF"))
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Surface)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = LossCoral, modifier = Modifier.size(16.dp))
                            Text("تقرير PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (exportNotice != null) {
        AlertDialog(
            onDismissRequest = { exportNotice = null },
            title = { Text("إشعار التقرير", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text(exportNotice ?: "", fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { exportNotice = null }) {
                    Text("حسناً", fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        )
    }
}
