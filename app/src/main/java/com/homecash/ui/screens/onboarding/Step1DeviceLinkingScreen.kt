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
 * Stitch Screen 1_3 Parity - Device & Permission Linking (Step 1 of 3)
 * Exact layout, typography and multi-device connection view.
 */
@Composable
fun Step1DeviceLinkingScreen(
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val view = LocalView.current
    var isDeviceLinked by remember { mutableStateOf(true) }

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
                            text = "ربط الأجهزة والأذونات",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(width = 22.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(Primary))
                            Box(modifier = Modifier.size(width = 8.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(SurfaceContainer))
                            Box(modifier = Modifier.size(width = 8.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(SurfaceContainer))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
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
                            onNext()
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
                            Text("المتابعة للخطوة 2 من 3", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Interactive Step Progress & Status Pill (Stitch 1_3 lines 4-18)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("١", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("ربط الأجهزة والأذونات", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("الخطوة ١ من ٣ لإعداد دفترك المالي", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFEEF1FF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Primary))
                            Text("جاهز للربط", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            }

            // Intro Pitch & Warm Graphic Banner (Stitch 1_3 lines 20-36)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F2FF)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDFE1F9)))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DevicesOther, contentDescription = null, tint = Primary, modifier = Modifier.size(26.dp))
                    }
                    Column {
                        Text("مزامنة أجهزتك المتعددة", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "اربط هواتفك في مساحة دفتر واحدة؛ يقوم كل هاتف بفرز رسائل شرائحه ومحافظه كاش بشكل مستقل وبخصوصية تامة.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Section 1: Your Account Devices
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text("أجهزة حسابك", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Text("جهازان متصلان", fontSize = 11.sp, color = TextSecondary)
            }

            // Device 1: Current Device
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEF1FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.StayCurrentPortrait, contentDescription = null, tint = Primary, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text("Vivo V2430 (هاتفك الحالي)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("٣ خطوط ومحافظ مسجلة محلياً", fontSize = 11.sp, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 2.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GainEmerald))
                                Text("نشط الآن في هذه الجلسة", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GainEmerald)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Primary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("هذا الجهاز", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Device 2: Other Connected Device
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Smartphone, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(24.dp))
                        }
                        Column {
                            Text("Redmi Note 12 (كاشير فرعي)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("شريحتان (فودافون وإتصالات مصر)", fontSize = 11.sp, color = TextSecondary)
                            Text("آخر نشاط: اليوم ١٠:١٥ ص", fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFEEF1FF))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                            Text("متزامن", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            }

            // Link Device Button
            OutlinedButton(
                onClick = {
                    AppSoundManager.playClick(view)
                    isDeviceLinked = true
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Primary.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF8F9FE))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AddLink, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    Text("ربط هذا الجهاز بالحساب الموحد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                }
            }

            // Trust note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF4F2FF))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    Text(
                        "يمكنك ربط أكثر من هاتف بنفس الحساب بأمان مشفر، وكل هاتف يتابع محافظه الذاتية تلقائياً دون اختلاط البيانات.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
