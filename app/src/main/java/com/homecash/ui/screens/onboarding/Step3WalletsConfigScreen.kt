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
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.theme.*
import com.homecash.ui.components.ProviderLogo

/**
 * Stitch Screen 3_3_2 Parity - Discovered SIM Lines & Wallets Configuration (Step 3 of 3)
 * Exact layout of stitch_\3_3_2:
 * Discovered SIM lines, carrier matching from SMS, initial balance editing,
 * and completion trigger to open the Dashboard.
 */
@Composable
fun Step3WalletsConfigScreen(
    onBack: () -> Unit,
    onComplete: (sim1Balance: Double, sim2Balance: Double, instapayBalance: Double) -> Unit
) {
    val view = LocalView.current
    var sim1BalanceText by remember { mutableStateOf("15450") }
    var sim2BalanceText by remember { mutableStateOf("9200") }
    var instapayBalanceText by remember { mutableStateOf("24800") }
    var sim1Name by remember { mutableStateOf("شريحة المحل الأساسية") }
    var sim2Name by remember { mutableStateOf("شريحة المبيعات 2") }

    Scaffold(
        topBar = {
            Surface(
                color = Surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
            ) {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
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

                        Text(
                            text = "الخطوة 3 من 3 — خطوطك ومحافظك",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEEF1FF))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("100%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { 1.0f },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                            color = Primary,
                            trackColor = Color(0xFFE2E5F2)
                        )
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
                            AppSoundManager.playCashChime(view)
                            val b1 = sim1BalanceText.toDoubleOrNull() ?: 15450.0
                            val b2 = sim2BalanceText.toDoubleOrNull() ?: 9200.0
                            val b3 = instapayBalanceText.toDoubleOrNull() ?: 24800.0
                            onComplete(b1, b2, b3)
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
                            Text("إتمام التهيئة وفتح لوحة التحكم 🚀", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
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

            // Discovered Lines Header (Stitch 3_3_2 lines 150-157)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.SimCard, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Text("الخطوط المكتشفة", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text("3 خطوط مسجلة", fontSize = 11.sp, color = TextSecondary)
            }

            // Card 1: SIM 1 Vodafone Cash (Stitch 3_3_2 lines 159-189)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Text("الشريحة 1 · 0101 234 5678", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEEF1FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("على هذا الهاتف", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8F9FE))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProviderLogo(providerId = "vodafone_cash", size = 24.dp)
                            Text("فودافون كاش", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("·", fontSize = 12.sp, color = TextMuted)
                            Text("وجدنا 42 رسالة من فودافون كاش", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    OutlinedTextField(
                        value = sim1Name,
                        onValueChange = { sim1Name = it },
                        label = { Text("اسم الخط") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = sim1BalanceText,
                        onValueChange = { sim1BalanceText = it },
                        label = { Text("الرصيد الحالي (ج.م)") },
                        trailingIcon = { Text("ج.م", fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("من آخر رسالة بتاريخ اليوم — يمكنك تعديل الرصيد الافتتاحي للدرج", fontSize = 10.sp, color = TextMuted)
                }
            }

            // Card 2: SIM 2 Etisalat Cash (Stitch 3_3_2 lines 191-210)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Text("الشريحة 2 · 0111 987 6543", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFEEF1FF))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("على هذا الهاتف", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8F9FE))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProviderLogo(providerId = "etisalat_cash", size = 24.dp)
                            Text("اتصالات كاش", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("·", fontSize = 12.sp, color = TextMuted)
                            Text("وجدنا 18 رسالة من اتصالات كاش", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    OutlinedTextField(
                        value = sim2Name,
                        onValueChange = { sim2Name = it },
                        label = { Text("اسم الخط") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = sim2BalanceText,
                        onValueChange = { sim2BalanceText = it },
                        label = { Text("الرصيد الحالي (ج.م)") },
                        trailingIcon = { Text("ج.م", fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Card 3: InstaPay IPN Bank Account
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            Text("حساب بنكي · إنستاباي IPN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(Color(0xFFF3EBF9))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("شبكة لحظية", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF28004D))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF8F9FE))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ProviderLogo(providerId = "instapay", size = 24.dp)
                            Text("إنستاباي InstaPay", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("·", fontSize = 12.sp, color = TextMuted)
                            Text("تحويلات بنكية فورية بدون عمولة", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    OutlinedTextField(
                        value = instapayBalanceText,
                        onValueChange = { instapayBalanceText = it },
                        label = { Text("الرصيد الافتتاحي (ج.م)") },
                        trailingIcon = { Text("ج.م", fontWeight = FontWeight.Bold, color = Primary, modifier = Modifier.padding(end = 12.dp)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
