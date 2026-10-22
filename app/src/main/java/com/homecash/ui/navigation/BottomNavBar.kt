package com.homecash.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.core.common.AppSoundManager
import com.homecash.core.theme.*

data class NavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val NAV_ITEMS = listOf(
    NavItem("الرئيسية", Screen.Dashboard.route, Icons.Default.Home),
    NavItem("المعاملات", Screen.Transactions.route, Icons.AutoMirrored.Filled.ReceiptLong),
    NavItem("التقارير", Screen.Reports.route, Icons.Default.Analytics),
    NavItem("الإعدادات", Screen.Settings.route, Icons.Default.Settings)
)

@Composable
fun HomeCashBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val view = LocalView.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
        color = Surface,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top border
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NAV_ITEMS.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                AppSoundManager.click(view)
                                onNavigate(item.route)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryLight else Surface)
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) Primary else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Primary else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
