package com.homecash.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.notifications.AppNotificationCenter
import com.homecash.core.common.DateFormatter
import com.homecash.core.theme.*
import com.homecash.data.local.entity.TransactionEntity
import com.homecash.ui.components.*
import com.homecash.ui.viewmodel.DashboardViewModel

/**
 * Stitch Official Design Parity - Main Dashboard (Screen _14)
 * Header with storefront logo & live SMS sync badge,
 * Total Balance card (135deg gradient), Quick action 3-col bar,
 * Attention warning flag card, Cashflow analytics graph,
 * Linked wallets horizontal carousel, and Recent transactions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToShiftClose: () -> Unit,
    onNavigateToFlagResolution: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onCounterpartyClick: (String) -> Unit
) {
    val user by viewModel.user.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val todayInflow by viewModel.todayInflow.collectAsState()
    val todayOutflow by viewModel.todayOutflow.collectAsState()
    val todayCommissions by viewModel.todayCommissions.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val openFlags by viewModel.openFlags.collectAsState()
    val latestSimEvent by viewModel.latestSimEvent.collectAsState()
    val isSimRunning by viewModel.isSimulationRunning.collectAsState()
    val appNotifications by AppNotificationCenter.items.collectAsState()

    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showQuickTransferSheet by remember { mutableStateOf(false) }

    val isBusinessMode = (user?.mode ?: "business") == "business"

    Scaffold(
        topBar = {
            // Stitch Screen _14 Header
            Surface(
                color = Surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand: Storefront box + Title + Live Sync Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isBusinessMode) (user?.businessName ?: "محل الباشا فون") else "حاسب",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                // Green pulse sync pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(SuccessContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(GainEmerald)
                                        )
                                        Text(
                                            text = "متزامن مع SMS",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GainEmerald
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "آخر تحديث: الآن • 3 هواتف متزامنة",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Right Actions: Search + Notification Bell with Badge
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .border(1.dp, BorderSubtle, CircleShape)
                                .clickable(onClick = onNavigateToTransactions),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Surface)
                                .border(1.dp, BorderSubtle, CircleShape)
                                .clickable(onClick = onNavigateToNotifications),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "الإشعارات",
                                tint = TextSecondary,
                                modifier = Modifier.size(19.dp)
                            )
                            if (appNotifications.any { !it.read } || openFlags.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .clip(CircleShape)
                                        .background(LossCoral),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (appNotifications.count { !it.read } + openFlags.size).coerceAtMost(99).toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. Stitch Aggregated Balance Card (135deg gradient)
            item {
                TotalBalanceCard(
                    totalBalance = totalBalance,
                    todayInflow = todayInflow,
                    todayOutflow = todayOutflow,
                    isBusinessMode = isBusinessMode,
                    onShiftCloseClick = onNavigateToShiftClose
                )
            }

            // 2. Stitch Quick Actions 3-Column Bar
            item {
                QuickActionsRow(
                    onQuickTransferClick = { showQuickTransferSheet = true },
                    onManualTxClick = onNavigateToTransactions,
                    onShiftCloseClick = onNavigateToShiftClose
                )
            }

            // 3. Attention / Flag Alert Card (If pending)
            if (openFlags.isNotEmpty()) {
                item {
                    FlagAlertCard(
                        flag = openFlags.first(),
                        onResolveClick = onNavigateToFlagResolution
                    )
                }
            }

            // 4. Live Simulation Banner (Multi-Device Active Simulation)
            item {
                SimulationLiveBanner(
                    latestEvent = latestSimEvent,
                    isSimulationRunning = isSimRunning,
                    onTriggerClick = { viewModel.triggerManualSimulation() }
                )
            }

            // 5. Cashflow Analytics & Trend Graph Section
            item {
                CashflowAnalyticsCard(
                    todayInflow = todayInflow,
                    todayOutflow = todayOutflow,
                    commissionEarnings = todayCommissions
                )
            }

            // 6. Linked Wallets Horizontal Carousel
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "المحافظ والخطوط",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${wallets.size} محافظ نشطة ومقترنة بشرائح الكاشير",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(PrimaryLight)
                                .clickable { /* Add wallet */ }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                                Text("إضافة محفظة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(wallets) { wallet ->
                            WalletCard(
                                wallet = wallet,
                                onClick = { /* View wallet detail */ }
                            )
                        }
                    }
                }
            }

            // 7. Recent Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أحدث المعاملات اللحظية",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToTransactions) {
                        Text("عرض الكل", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Recent Transactions List
            items(recentTransactions.take(8)) { tx ->
                TransactionItem(
                    tx = tx,
                    onClick = { selectedTransaction = tx },
                    onCounterpartyClick = {
                        val cp = if (tx.counterpartyName.isNotBlank()) tx.counterpartyName else tx.counterpartyNumber
                        if (cp.isNotBlank()) {
                            onCounterpartyClick(cp)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Stitch Transaction Details Dialog (Screen _18)
    if (selectedTransaction != null) {
        val tx = selectedTransaction!!
        val isInflow = tx.direction == "IN"

        AlertDialog(
            onDismissRequest = { selectedTransaction = null },
            shape = RoundedCornerShape(26.dp),
            containerColor = Surface,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "تفاصيل المعاملة",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = DateFormatter.formatRelative(tx.occurredAt),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(onClick = { selectedTransaction = null }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hero Amount Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceContainerLow)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("قيمة المعاملة", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = (if (isInflow) "+" else "-") + CurrencyFormatter.format(tx.amount, showCurrency = true),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInflow) GainEmerald else LossCoral
                            )
                        }
                    }

                    // Key details rows
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("نوع العملية:", fontSize = 12.sp, color = TextSecondary)
                        Text(if (isInflow) "إيداع كاش / وارد" else "تحويل كاش / صادر", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Divider(color = SurfaceContainer, thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الطرف / المحفظة:", fontSize = 12.sp, color = TextSecondary)
                        Text("${getCarrierInfo(tx).first} (${tx.counterpartyNumber})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Divider(color = SurfaceContainer, thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("العمولة المحصلة:", fontSize = 12.sp, color = TextSecondary)
                        Text("+${CurrencyFormatter.format(tx.cashierCommission, showCurrency = true)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GainEmerald)
                    }
                    Divider(color = SurfaceContainer, thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الرصيد بعدها:", fontSize = 12.sp, color = TextSecondary)
                        Text(CurrencyFormatter.format(tx.balanceAfter, showCurrency = true), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Divider(color = SurfaceContainer, thickness = 1.dp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("كود العملية المرجعي:", fontSize = 12.sp, color = TextSecondary)
                        Text(tx.operationCode.ifBlank { "TXN-9847120" }, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                    }

                    // Raw SMS Snippet Box
                    Text("نص رسالة الـ SMS الأصلية:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLow)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = tx.rawText,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedTransaction = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("إغلاق التفاصيل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Stitch Quick Transfer Sheet Dialog (Screen _15)
    if (showQuickTransferSheet) {
        var transferAmount by remember { mutableStateOf("1000") }
        var transferPhone by remember { mutableStateOf("01012345678") }
        var isCashIn by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showQuickTransferSheet = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = Surface,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("تحويل سريع جديد", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("تنفيذ فوري وتوثيق الحركة", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    IconButton(onClick = { showQuickTransferSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Type selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCashIn) Primary else SurfaceContainerLow)
                                .clickable { isCashIn = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "إيداع للعميل (وارد)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCashIn) Color.White else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isCashIn) Primary else SurfaceContainerLow)
                                .clickable { isCashIn = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "سحب للعميل (صادر)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isCashIn) Color.White else TextSecondary
                            )
                        }
                    }

                    // Recipient Phone Input
                    Text("رقم هاتف المستلم (010/011/012/015)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    OutlinedTextField(
                        value = transferPhone,
                        onValueChange = { transferPhone = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedContainerColor = Surface
                        )
                    )

                    // Amount Input
                    Text("المبلغ (ج.م)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    OutlinedTextField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceContainerLow,
                            focusedContainerColor = Surface
                        )
                    )

                    // Quick Amount Chips: 500, 1000, 2000, 5000
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("500", "1000", "2000", "5000").forEach { chipAmount ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SurfaceContainerLow)
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                    .clickable { transferAmount = chipAmount }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$chipAmount ج.م", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }

                    // Commission Preview Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryLight)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("عمولة المحل المستحقة:", fontSize = 11.sp, color = TextPrimary)
                            val amt = transferAmount.toDoubleOrNull() ?: 0.0
                            val comm = maxOf(5.0, amt * 0.01)
                            Text("+${CurrencyFormatter.format(comm)} ج.م", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.triggerManualSimulation()
                        showQuickTransferSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("تسجيل وتأكيد التحويل", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
