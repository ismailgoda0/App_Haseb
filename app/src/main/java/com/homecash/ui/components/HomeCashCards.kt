package com.homecash.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.common.DateFormatter
import com.homecash.core.simulation.SimulationEvent
import com.homecash.core.theme.*
import com.homecash.data.local.entity.FlagEntity
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.data.local.entity.WalletEntity

/**
 * Helper to extract carrier display name and brand color from a transaction
 */
fun getCarrierInfo(tx: TransactionEntity): Pair<String, Color> {
    val name = when {
        tx.category.contains("فودافون", ignoreCase = true) || tx.walletId.contains("voda", ignoreCase = true) -> "فودافون كاش"
        tx.category.contains("إنستا", ignoreCase = true) || tx.walletId.contains("insta", ignoreCase = true) -> "إنستاباي InstaPay"
        tx.category.contains("اتصالات", ignoreCase = true) || tx.walletId.contains("etisalat", ignoreCase = true) -> "اتصالات كاش"
        tx.category.contains("أورنج", ignoreCase = true) || tx.walletId.contains("orange", ignoreCase = true) -> "أورنج كاش"
        tx.category.contains("وي", ignoreCase = true) || tx.walletId.contains("we", ignoreCase = true) -> "WE Pay"
        tx.category.isNotBlank() -> tx.category
        else -> "محفظة المحل"
    }
    val color = when {
        name.contains("فودافون") -> VodafoneRed
        name.contains("إنستا") -> InstaPayPurple
        name.contains("اتصالات") -> EtisalatGreen
        name.contains("أورنج") -> OrangeBrand
        name.contains("وي") -> WePurple
        else -> Primary
    }
    return Pair(name, color)
}

/**
 * Stitch Official Design Parity - Total Balance Card
 * Gradient: 135deg linear (#4F63E8 -> #2A38A8)
 */
@Composable
fun TotalBalanceCard(
    totalBalance: Double,
    todayInflow: Double,
    todayOutflow: Double,
    isBusinessMode: Boolean,
    onShiftCloseClick: () -> Unit
) {
    var isBalanceVisible by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4F63E8), Color(0xFF2A38A8)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        // Decorative background blur circles
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = (-30).dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 25.dp, y = 25.dp)
                .clip(CircleShape)
                .background(GainEmerald.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header: Title + Eye icon + Business Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "إجمالي الرصيد في كل المحافظ",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = { isBalanceVisible = !isBalanceVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "تبديل إظهار الرصيد",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBusinessMode) "محل / تاجر" else "شخصي",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Hero Balance (36sp)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isBalanceVisible) CurrencyFormatter.formatInteger(totalBalance, showCurrency = false) else "••••••••",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "ج.م",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Wallet Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "وارد: +${CurrencyFormatter.formatInteger(todayInflow, showCurrency = true)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "صادر: -${CurrencyFormatter.formatInteger(todayOutflow, showCurrency = true)}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Stitch Quick Actions Row: 3 Columns
 */
@Composable
fun QuickActionsRow(
    onQuickTransferClick: () -> Unit,
    onManualTxClick: () -> Unit,
    onShiftCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary)
                .clickable(onClick = onQuickTransferClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
                Text("تحويل سريع", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryLight)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .clickable(onClick = onManualTxClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                Text("معاملة يدوية", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFDDE4FF))
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .clickable(onClick = onShiftCloseClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Sync, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(19.dp))
                Text("تقفيل وردية", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Stitch Attention Warning Flag Card
 */
@Composable
fun FlagAlertCard(
    flag: FlagEntity,
    onResolveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WarningContainer)
            .border(1.dp, WarningBorder, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(22.dp))
                }

                Column {
                    Text(text = flag.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = flag.explanation, fontSize = 11.sp, color = TextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(AccentAmber)
                    .clickable(onClick = onResolveClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("معالجة الفجوة", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowBackIos, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                }
            }
        }
    }
}

/**
 * Stitch Cashflow Analytics Card
 */
@Composable
fun CashflowAnalyticsCard(
    todayInflow: Double,
    todayOutflow: Double,
    commissionEarnings: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                    }

                    Column {
                        Text("حركة السيولة والعمولات", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("وارد ${CurrencyFormatter.formatInteger(todayInflow)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GainEmerald)
                            Text("•", fontSize = 11.sp, color = OutlineVariant)
                            Text("صادر ${CurrencyFormatter.formatInteger(todayOutflow)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = LossCoral)
                            Text("•", fontSize = 11.sp, color = OutlineVariant)
                            Text("أرباح: +${CurrencyFormatter.formatInteger(commissionEarnings)} ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(SurfaceSubtle)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(100.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(GainEmerald))
                        Text("مباشر", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth().height(90.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    drawLine(color = Color(0xFFE5E9F8), start = Offset(0f, h * 0.2f), end = Offset(w, h * 0.2f), strokeWidth = 1f)
                    drawLine(color = Color(0xFFE5E9F8), start = Offset(0f, h * 0.6f), end = Offset(w, h * 0.6f), strokeWidth = 1f)
                    drawLine(color = Color(0xFFE5E9F8), start = Offset(0f, h), end = Offset(w, h), strokeWidth = 1f)

                    val inPath = Path().apply {
                        moveTo(0f, h * 0.85f)
                        cubicTo(w * 0.3f, h * 0.4f, w * 0.6f, h * 0.6f, w, h * 0.15f)
                    }
                    drawPath(path = inPath, color = GainEmerald, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))

                    val outPath = Path().apply {
                        moveTo(0f, h * 0.95f)
                        cubicTo(w * 0.35f, h * 0.65f, w * 0.7f, h * 0.45f, w, h * 0.35f)
                    }
                    drawPath(path = outPath, color = Primary, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("09:00 ص", "12:00 م", "03:00 م", "06:00 م", "الآن").forEach { t ->
                    Text(text = t, fontSize = 10.sp, color = TextMuted)
                }
            }
        }
    }
}

/**
 * Stitch Horizontal Wallet Card (Width: 290.dp)
 */
@Composable
fun WalletCard(
    wallet: WalletEntity,
    onClick: () -> Unit
) {
    val carrierColor = when {
        wallet.providerName.contains("فودافون", ignoreCase = true) -> VodafoneRed
        wallet.providerName.contains("إنستا", ignoreCase = true) || wallet.providerName.contains("Insta", ignoreCase = true) -> InstaPayPurple
        wallet.providerName.contains("اتصالات", ignoreCase = true) -> EtisalatGreen
        wallet.providerName.contains("أورنج", ignoreCase = true) -> OrangeBrand
        wallet.providerName.contains("وي", ignoreCase = true) -> WePurple
        else -> Primary
    }

    Card(
        modifier = Modifier
            .width(290.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF8FF)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProviderLogo(
                        providerId = wallet.providerName,
                        size = 40.dp,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = wallet.providerName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(carrierColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (wallet.providerName.contains("إنستا")) "حساب بنكي" else "SIM 1",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = carrierColor
                                )
                            }
                        }
                        Text("01012345678", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(SuccessContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(GainEmerald))
                        Text("نشطة", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GainEmerald)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("الرصيد اللحظي الحالي", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = CurrencyFormatter.formatInteger(wallet.currentBalance, showCurrency = false),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text("ج.م", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(12.dp))
                            Text("وارد اليوم", fontSize = 10.sp, color = TextSecondary)
                        }
                        Text("+12,400 ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GainEmerald, modifier = Modifier.padding(top = 2.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = LossCoral, modifier = Modifier.size(12.dp))
                            Text("صادر اليوم", fontSize = 10.sp, color = TextSecondary)
                        }
                        Text("-6,200 ج.م", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LossCoral, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                Text("آخر تحديث من رسالة SMS الآن", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

/**
 * Stitch Transaction Item (Screen _15)
 */
@Composable
fun TransactionItem(
    tx: TransactionEntity,
    onClick: () -> Unit,
    onCounterpartyClick: () -> Unit
) {
    val isInflow = tx.direction == "IN"
    val (carrierName, _) = getCarrierInfo(tx)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isInflow) SuccessContainer else ErrorContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isInflow) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isInflow) GainEmerald else LossCoral,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (tx.counterpartyName.isNotBlank()) tx.counterpartyName else if (isInflow) "استلام كاش / وارد" else "تحويل كاش / صادر",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SurfaceContainer)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (tx.isManual) "يدوي" else "SIM 1",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            ProviderLogo(providerId = tx.walletId, size = 16.dp)
                            Text(text = carrierName, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Text("•", fontSize = 10.sp, color = OutlineVariant)
                            Text(
                                text = if (tx.counterpartyNumber.isNotBlank()) tx.counterpartyNumber else "01098765432",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = (if (isInflow) "+" else "-") + CurrencyFormatter.format(tx.amount, showCurrency = true),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isInflow) GainEmerald else LossCoral
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = DateFormatter.formatRelative(tx.occurredAt), fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SurfaceContainer, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(SuccessContainer)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(13.dp))
                        Text(
                            text = "عمولة: +${CurrencyFormatter.format(tx.cashierCommission, showCurrency = true)} (1.0%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GainEmerald
                        )
                    }
                }

                Text(
                    text = "الرصيد بعدها: ${CurrencyFormatter.format(tx.balanceAfter, showCurrency = true)}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Stitch Simulation Live Banner
 */
@Composable
fun SimulationLiveBanner(
    latestEvent: SimulationEvent?,
    isSimulationRunning: Boolean,
    onTriggerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PrimaryLight)
            .border(1.dp, BorderSubtle, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }

                Column {
                    Text("محاكاة 3 هواتف ذكية (تجريبي)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDeep)
                    Text(
                        text = latestEvent?.summary ?: "تصل رسائل وهمية كل 30 ثانية لتجربة الحسابات بدون تكلفة",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary)
                    .clickable(onClick = onTriggerClick)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("محاكاة الآن ⚡", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
