package com.homecash.ui.screens.flags

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.theme.*
import com.homecash.data.local.entity.FlagEntity
import com.homecash.ui.components.ProviderLogo
import com.homecash.ui.viewmodel.DashboardViewModel

/**
 * Stitch Screen _4 Parity - Gaps & Discrepancies Resolver (كاشف الفجوات وحل الفلاجات)
 * Exact replication of stitch_\_4:
 * Top app bar, batch resolution banner, active gap warning card, ledger deconstruction bento metrics,
 * AI explainability box, and single-click resolution options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlagResolutionScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val view = LocalView.current
    val openFlags by viewModel.openFlags.collectAsState()
    var successNotice by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(
                color = Surface,
                modifier = Modifier.fillMaxWidth().shadow(2.dp)
            ) {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconButton(
                                onClick = {
                                    AppSoundManager.playClick(view)
                                    onBack()
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "رجوع", tint = TextPrimary)
                            }
                            Column {
                                Text("كاشف الفجوات وحل الفلاجات", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("مطابقة تسلسل رصيد المحافظ فورياً", fontSize = 11.sp, color = TextSecondary)
                            }
                        }

                        IconButton(
                            onClick = {
                                AppSoundManager.playClick(view)
                                successNotice = "تمت إعادة فحص ومطابقة تسلسل الرسائل والأرصدة"
                            }
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "إعادة فحص", tint = TextSecondary)
                        }
                    }

                    // Alert banner (Stitch _4 line 143)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF7ED))
                            .border(1.dp, Color(0xFFFFD9A8), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(18.dp))
                                Text("فارق غير مسجل برسالة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Color(0xFFFED7AA))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("تنبيه فوري", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                            }
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

            // Batch Resolution Banner (Stitch _4 lines 143 batch banner)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF1FF)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDFE1F9)))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                            Text(
                                "تم رصد فجوات رصيد ناتجة عن تأخر وصول أو مزامنة شبكة المحافظ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Button(
                            onClick = {
                                AppSoundManager.playCashChime(view)
                                openFlags.forEach { flag ->
                                    viewModel.resolveFlag(flag.id, "حل جماعي: اعتماد رصيد الرسائل")
                                }
                                successNotice = "تم حل جميع الفلاجات المتشابهة واعتماد الأرصدة الفعلية بنجاح!"
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("حل جماعي: اعتماد الرصيد الفعلي دفعة واحدة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            if (openFlags.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(48.dp))
                            Text("رائع! جميع الأرصدة والمعاملات مطابقة ولا توجد أي فجوات نقدية", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            } else {
                items(openFlags) { flag ->
                    // Active Gap Warning Card (Stitch _4 lines 147-200)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    ProviderLogo(providerId = flag.walletId, size = 38.dp, shape = RoundedCornerShape(12.dp))
                                    Column {
                                        Text(flag.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("رقم الحساب: 01023455842", fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(Color(0xFFFFF7ED))
                                        .border(1.dp, Color(0xFFFFD9A8), RoundedCornerShape(100.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentAmber))
                                        Text("غير مفسر", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Ledger Deconstruction Bento Metrics (Stitch _4 lines 166-198)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF8F9FE))
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الرصيد قبل العملية:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${CurrencyFormatter.format(flag.expectedBalance + flag.discrepancy)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الرصيد المفترض بعد الحساب:", fontSize = 11.sp, color = TextSecondary)
                                    Text("${CurrencyFormatter.format(flag.expectedBalance)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الرصيد الفعلي المذكور بالرسالة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                                    Text("${CurrencyFormatter.format(flag.actualBalance)} ج.م", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Primary)
                                }

                                HorizontalDivider(color = BorderSubtle)

                                // The Gap Callout Banner (Stitch _4 lines 191-198)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Error, contentDescription = null, tint = LossCoral, modifier = Modifier.size(18.dp))
                                            Text("الفارق المفقود / غير المفسر:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C2D12))
                                        }
                                        Text(
                                            "${CurrencyFormatter.format(flag.discrepancy)} ج.م",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = LossCoral
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // AI Explainability Insight Box (Stitch _4 lines 199-200)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFEEF1FF))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Text(
                                        "تدقيق ذكي: ${flag.explanation}",
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Resolution Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        AppSoundManager.playCashChime(view)
                                        viewModel.resolveFlag(flag.id, "معاملة نقدية يدوية بالفارق")
                                        successNotice = "تم تسجيل معاملة نقدية يدوية بالفارق وتعديل رصيد المحفظة"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                ) {
                                    Text("تسجيل حركة نقدية", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        AppSoundManager.playClick(view)
                                        viewModel.resolveFlag(flag.id, "اعتماد رصيد الرسالة كأمر واقع")
                                        successNotice = "تم اعتماد رصيد الرسالة ومطابقة رصيد المحفظة فورياً"
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                                ) {
                                    Text("اعتماد رصيد الرسالة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(28.dp)) }
        }
    }

    if (successNotice != null) {
        AlertDialog(
            onDismissRequest = { successNotice = null },
            title = { Text("تم بنجاح", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = { Text(successNotice ?: "", fontSize = 13.sp) },
            confirmButton = {
                TextButton(onClick = { successNotice = null }) {
                    Text("حسناً", fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        )
    }
}
