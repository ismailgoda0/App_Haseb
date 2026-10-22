package com.homecash.ui.screens.shift

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.notifications.AppNotificationCenter
import com.homecash.core.theme.*
import com.homecash.ui.viewmodel.ShiftClosingViewModel
import kotlin.math.abs

/**
 * Exact Stitch Parity for stitch_\shift_closing_1 and stitch_\shift_closing_2
 * "تقفيل الوردية والدرج النقدي"
 * - Cashier indicator & active drawer pill
 * - Actual cash drawer entry
 * - Mathematical ledger reconciliation
 * - 100% parity audit status banner
 * - Lock & WhatsApp share action buttons
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftClosingScreen(
    viewModel: ShiftClosingViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val expectedCash by viewModel.expectedCash.collectAsState()
    val actualCash by viewModel.actualCash.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isClosedSuccess by viewModel.shiftClosedSuccess.collectAsState()

    // Initialize with expected cash if blank for convenient demo cashier workflow
    LaunchedEffect(expectedCash) {
        if (actualCash.isBlank() && expectedCash > 0) {
            viewModel.onActualCashChange(String.format(java.util.Locale.US, "%.2f", expectedCash))
        }
    }

    val actualVal = actualCash.toDoubleOrNull() ?: 0.0
    val diff = actualVal - expectedCash
    val isPerfectMatch = abs(diff) < 0.01

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
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            text = "تقفيل الوردية والدرج النقدي",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "الكاشير: أحمد محمود",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Shift Header Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFDFE0FF).copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Primary)
                    )
                    Text(
                        text = "وردية نشطة • منذ 7 ساعات و 30 دقيقة (بدأت 08:00 ص)",
                        fontSize = 11.sp,
                        color = Color(0xFF1B33BC),
                        maxLines = 1
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "الدرج المفتوح #3",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            // Actual Cash Card
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "النقدية الفعلية بالدرج الآن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "قم بعد النقود وأدخل الإجمالي",
                        fontSize = 11.sp,
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
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = actualCash,
                            onValueChange = { viewModel.onActualCashChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = {
                                Text(
                                    text = "ج.م",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(start = 12.dp)
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF4F2FF),
                                unfocusedContainerColor = Color(0xFFF4F2FF),
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color(0xFFC5C5D7)
                            )
                        )

                        Text(
                            text = "يتم مطابقة هذا الرقم تلقائياً مع حركة الوارد والصادر النقدي اليومي.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Math Summary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column {
                    Text(
                        text = "تسوية الدرج الحسابية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "تفاصيل المقبوضات والمدفوعات النقدية المسجلة للوردية",
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
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryRow("رصيد بداية الوردية (العهدة الافتتاحية)", "5,000.00 ج.م", TextPrimary)
                        SummaryRow("(+) مقبوضات نقدية من العملاء (تحويلات صادرة)", "+14,200.00 ج.م", Color(0xFF2E7D32))
                        SummaryRow("(-) مدفوعات نقدية للعملاء (سحوبات واردة)", "-8,750.00 ج.م", ErrorRed)
                        SummaryRow("(+) عمولات الخدمة المحصلة نقداً في الدرج", "+185.00 ج.م", Primary)

                        HorizontalDivider(color = Color(0xFFECECFF))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "النقدية المحسوبة المتوقعة بالدرج:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = CurrencyFormatter.format(expectedCash),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Primary
                            )
                        }
                    }
                }
            }

            // Status Banner
            if (isPerfectMatch) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDFE2F0).copy(alpha = 0.5f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "الدرج مطابق بنسبة 100%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "لا يوجد أي عجز أو زيادة نقدية مسجلة في هذه الوردية.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (diff > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (diff > 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (diff > 0) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = if (diff > 0) "يوجد زيادة نقدية في الدرج: +${CurrencyFormatter.format(diff)}" else "يوجد عجز نقدي في الدرج: ${CurrencyFormatter.format(diff)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (diff > 0) SuccessGreen else ErrorRed
                            )
                            Text(
                                text = "يرجى مراجعة إيصالات التحويل والسحوبات قبل التأكيد.",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        AppSoundManager.cashRegister(view)
                        viewModel.submitShiftClose {
                            AppNotificationCenter.post(
                                title = "تم إقفال الوردية بنجاح",
                                message = "تم ترحيل إجمالي ${CurrencyFormatter.format(actualVal)} إلى تقرير اليوم النهائي.",
                                category = "الوردية"
                            )
                            onBack()
                        }
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
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = "تأكيد إقفال الوردية وترحيل الحساب",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        AppSoundManager.click(view)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                """
                                تقرير إقفال وردية — HomeCash
                                ---------------------------
                                الكاشير: أحمد محمود
                                الدرج المفتوح: #3
                                النقدية الفعلية: ${CurrencyFormatter.format(actualVal)}
                                النقدية المتوقعة: ${CurrencyFormatter.format(expectedCash)}
                                الفارق: ${if (diff >= 0) "+${CurrencyFormatter.format(diff)}" else CurrencyFormatter.format(diff)}
                                تم الترحيل والمطابقة آلياً عبر تطبيق دفتر المحافظ.
                                """.trimIndent()
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير الوردية"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFDFE2F0),
                        contentColor = Primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = "مشاركة تقرير الوردية PDF / واتساب للمدير",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = valueColor
        )
    }
}
