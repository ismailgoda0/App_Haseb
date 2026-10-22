package com.homecash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.homecash.core.common.CurrencyFormatter
import com.homecash.core.common.DateFormatter
import com.homecash.core.theme.*
import com.homecash.domain.model.CounterpartySummary

@Composable
fun CounterpartySummaryDialog(
    summary: CounterpartySummary,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SecondaryColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = summary.counterpartyName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = NeutralDark
                            )
                            Text(
                                text = summary.counterpartyNumber,
                                fontSize = 12.sp,
                                color = NeutralVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = OutlineVariantColor.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))

                // Summary Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreenBg),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("استلمت منه", fontSize = 11.sp, color = SuccessGreen)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                CurrencyFormatter.format(summary.totalReceived),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = ErrorRedBg),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("أرسلت له", fontSize = 11.sp, color = ErrorRed)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                CurrencyFormatter.format(summary.totalSent),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ErrorRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SecondaryColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("صافي العمولات والربح منه", fontSize = 11.sp, color = NeutralVariant)
                            Text(
                                CurrencyFormatter.format(summary.totalCommissions),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDark
                            )
                        }
                        Text(
                            text = "${summary.transactionCount} معاملات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NeutralVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("تاريخ المعاملات معه:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = NeutralDark)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp)
                ) {
                    items(summary.transactions) { tx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${tx.category} • ${DateFormatter.formatRelative(tx.occurredAt)}",
                                    fontSize = 11.sp,
                                    color = NeutralVariant
                                )
                                Text(
                                    text = "كود: ${tx.operationCode}",
                                    fontSize = 10.sp,
                                    color = OutlineColor
                                )
                            }
                            Text(
                                text = (if (tx.direction == "IN") "+" else "-") + CurrencyFormatter.format(tx.amount),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (tx.direction == "IN") SuccessGreen else ErrorRed
                            )
                        }
                        Divider(color = OutlineVariantColor.copy(alpha = 0.2f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("إغلاق كشف الحساب", color = SurfaceWhite, fontSize = 13.sp)
                }
            }
        }
    }
}
