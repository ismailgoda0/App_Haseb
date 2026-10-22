package com.homecash.ui.screens.notifications

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.notifications.AppNotification
import com.homecash.core.notifications.AppNotificationCenter
import com.homecash.core.theme.*

/**
 * Exact Stitch Parity for stitch_\_12 (Notifications & Flag Alerts Screen)
 * - Segmented tabs: "تحتاج إجراء" / "تنبيهات عامة"
 * - Batch resolution banner ("حل جماعي: اعتماد الرصيد الجديد لكافة الرسائل المتأخرة")
 * - Action cards: Balance gap, Pending SMS matching, Daily limit warning
 * - Dynamic live notifications list
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onRequestPermission: () -> Unit,
    onNavigateToFlagResolution: () -> Unit = {}
) {
    val view = LocalView.current
    val notifications by AppNotificationCenter.items.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: تحتاج إجراء, 1: تنبيهات عامة
    var isBatchResolved by remember { mutableStateOf(false) }

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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            AppSoundManager.click(view)
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "رجوع",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "الإشعارات وتنبيهات الفلاجات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFEEF1FF)
                    ) {
                        Text(
                            text = if (isBatchResolved) "0 تحتاج إجراء" else "3 تحتاج إجراء",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Tabs (Stitch _12)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF4F2FF))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == 0) Primary else Color.Transparent)
                        .clickable {
                            AppSoundManager.click(view)
                            selectedTab = 0
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBatchResolved) "تحتاج إجراء (0)" else "تحتاج إجراء (3)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedTab == 0) Color.White else Primary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == 1) Primary else Color.Transparent)
                        .clickable {
                            AppSoundManager.click(view)
                            selectedTab = 1
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "تنبيهات عامة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (selectedTab == 1) Color.White else Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedTab == 0) {
                // Action Required Tab
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Batch Resolution Banner
                    if (!isBatchResolved) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF1FF)),
                                modifier = Modifier.fillMaxWidth()
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
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoFixHigh,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Text(
                                            text = "حل جماعي: اعتماد الرصيد الجديد لكافة الرسائل المتأخرة",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF171A2B),
                                            lineHeight = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            AppSoundManager.cashRegister(view)
                                            isBatchResolved = true
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text("تطبيق على الكل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Card 1: Balance Gap
                        item {
                            ActionCard(
                                icon = Icons.Default.Warning,
                                iconBg = Color(0xFFFFDAD6),
                                iconColor = Color(0xFF93000A),
                                title = "فرق في الرصيد — فودافون كاش",
                                subtitle = "فارق 100 ج.م بين رصيد الرسالة والرصيد الحالي",
                                timeText = "منذ 15 دقيقة",
                                buttonText = "حل الفلاج",
                                onClick = {
                                    AppSoundManager.click(view)
                                    onNavigateToFlagResolution()
                                }
                            )
                        }

                        // Card 2: Pending SMS
                        item {
                            ActionCard(
                                icon = Icons.Default.HourglassEmpty,
                                iconBg = Color(0xFFECECFF),
                                iconColor = Primary,
                                title = "عملية معلقة بانتظار SMS",
                                subtitle = "تحويل 2,000 ج.م لم يتم مطابقتها تلقائياً",
                                timeText = "منذ ساعتين",
                                buttonText = "مطابقة يدوية",
                                onClick = {
                                    AppSoundManager.click(view)
                                    isBatchResolved = true
                                }
                            )
                        }

                        // Card 3: Daily Limit
                        item {
                            ActionCard(
                                icon = Icons.Default.TrendingUp,
                                iconBg = Color(0xFFECECFF),
                                iconColor = Primary,
                                title = "اقتراب من الحد اليومي",
                                subtitle = "54,000 من 60,000 ج.م مستخدمة اليوم",
                                timeText = "توصية: إيقاف التحويلات المؤقتة",
                                buttonText = "التفاصيل",
                                onClick = {
                                    AppSoundManager.click(view)
                                }
                            )
                        }
                    } else {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "تم حل جميع الفلاجات بنجاح",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "كافة الأرصدة وحركات الـ SMS مطابقة الآن 100%",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // General Notifications Tab
                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد تنبيهات عامة حالياً",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "ستصلك هنا إشعارات العمليات المكتملة وحالة المزامنة",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(notifications, key = { it.id }) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = if (!item.read) Color(0xFFF0F2FF) else Surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFFE5E9FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Primary
                                        )
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                item.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (!item.read) {
                                                Box(
                                                    Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(Primary)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            item.message,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp,
                                            color = TextSecondary
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "${item.category} • ${item.timeLabel()}",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    subtitle: String,
    timeText: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFECECFF))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = timeText,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEEF1FF),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}
