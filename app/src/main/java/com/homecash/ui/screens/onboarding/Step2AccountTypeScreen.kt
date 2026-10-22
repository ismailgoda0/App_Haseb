package com.homecash.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.theme.*

/**
 * Stitch Screen 2_3 Parity - Account Type Selection (Step 2 of 3)
 * Exactly replicates stitch_\2_3 with Personal vs Merchant cards,
 * business store name input, commission percentage, and progress meter.
 */
@Composable
fun Step2AccountTypeScreen(
    onBack: () -> Unit,
    onNext: (isBusiness: Boolean, storeName: String, commissionRate: Double) -> Unit
) {
    val view = LocalView.current
    var isBusiness by remember { mutableStateOf(true) }
    var storeName by remember { mutableStateOf("محل الباشا فون") }
    var commissionRateText by remember { mutableStateOf("1.0") }

    Scaffold(
        topBar = {
            Surface(
                color = Surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            AppSoundManager.playClick(view)
                            onBack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "الخطوة 2 من 3 — ما نوع استخدامك؟",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(width = 10.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(Primary))
                            Box(modifier = Modifier.size(width = 22.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(Primary))
                            Box(modifier = Modifier.size(width = 8.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(SurfaceContainer))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF1FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = Surface,
                modifier = Modifier.fillMaxWidth().shadow(8.dp)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            AppSoundManager.playClick(view)
                            val rate = commissionRateText.toDoubleOrNull() ?: 1.0
                            onNext(isBusiness, storeName, rate)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("حفظ ومتابعة إلى خطوة المحافظ (3 من 3)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White)
                        }
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
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Stepper Progress Header (Stitch 2_3 lines 5-16)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الخطوة 2 من 3", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                    Text("66% مكتمل", fontSize = 12.sp, color = TextSecondary)
                }
                LinearProgressIndicator(
                    progress = { 0.66f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Primary,
                    trackColor = Color(0xFFE2E5F2)
                )
                Text(
                    "حدد طبيعة حسابك لتخصيص الواجهة والحسابات والتقارير الفورية بدقة.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // Card 1: Personal (Stitch 2_3 lines 20-50)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        AppSoundManager.playClick(view)
                        isBusiness = false
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (!isBusiness) Primary else BorderSubtle)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF4F2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (!isBusiness) Primary else TextSecondary)
                            }
                            Column {
                                Text("شخصي", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("لمتابعة مصروفاتي ومحافظي اليومية", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        RadioButton(
                            selected = !isBusiness,
                            onClick = {
                                AppSoundManager.playClick(view)
                                isBusiness = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8F9FE))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "تتبع المصروفات الشهرية والميزانيات",
                            "تصنيف تلقائي لتحويلات البنوك والمحافظ",
                            "تقارير ملخصة وسهلة بدون تعقيدات محاسبية"
                        ).forEach { bullet ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Text(bullet, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Card 2: Merchant / Business (Stitch 2_3 lines 52-87)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        AppSoundManager.playClick(view)
                        isBusiness = true
                    },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isBusiness) Primary else BorderSubtle)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFDFE0FF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                Text("موصى به لأصحاب الأنشطة", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }

                        RadioButton(
                            selected = isBusiness,
                            onClick = {
                                AppSoundManager.playClick(view)
                                isBusiness = true
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Primary)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEF1FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Primary)
                        }
                        Column {
                            Text("محل / تاجر / شركة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("لمتابعة العمليات، العمولات، وصافي الأرباح", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF4F2FF))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "احتساب فوري لعمولات السحب والإيداع",
                            "إقفال الوردية وجرد الدرج ومطابقة الرصيد",
                            "كاشف الفجوات وتتبع عدة شرائح SIM متزامنة"
                        ).forEach { bullet ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text(bullet, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Info note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF4F2FF))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text(
                        "يمكنك التبديل بين وضع التاجر والوضع الشخصي لاحقاً من الإعدادات في أي وقت دون أي فقدان لبياناتك المسجلة.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // Business Form inputs
            if (isBusiness) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("بيانات النشاط التجاري", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                        OutlinedTextField(
                            value = storeName,
                            onValueChange = { storeName = it },
                            label = { Text("اسم المحل أو الكاشير") },
                            leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = Primary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = commissionRateText,
                            onValueChange = { commissionRateText = it },
                            label = { Text("نسبة عمولة الكاشير الافتراضية (%)") },
                            trailingIcon = { Text("%", fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(end = 12.dp)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
