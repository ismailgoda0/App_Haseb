package com.homecash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homecash.data.repository.ProviderManager

/**
 * Stitch Parity - Official Mobile Wallet / Bank Provider Logo Icon
 * Displays the distinctive carrier logo mark & color badge anywhere in the UI.
 */
@Composable
fun ProviderLogo(
    providerId: String,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    showContainerOnly: Boolean = false
) {
    val isDark = isSystemInDarkTheme()
    val provider = ProviderManager.getById(providerId)
    val colors = provider.getColors(isDark)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(colors.container)
            .border(1.dp, colors.border, shape),
        contentAlignment = Alignment.Center
    ) {
        val iconSize = (size.value * 0.58f).dp

        when (provider.slug) {
            "vodafone" -> {
                // Vodafone Cash Icon Mark
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = provider.nameAr,
                    tint = colors.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            "instapay" -> {
                // InstaPay Lightning Instant Transfer Mark
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = provider.nameAr,
                    tint = colors.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            "etisalat" -> {
                // Etisalat e& Mark
                Text(
                    text = "e&",
                    fontSize = (size.value * 0.42f).sp,
                    fontWeight = FontWeight.Black,
                    color = colors.primary
                )
            }
            "orange" -> {
                // Orange Cash Mark
                Icon(
                    imageVector = Icons.Default.SwapCalls,
                    contentDescription = provider.nameAr,
                    tint = colors.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            "we" -> {
                // WE Pay Mark
                Text(
                    text = "we",
                    fontSize = (size.value * 0.40f).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.primary
                )
            }
            "fawry" -> {
                // Fawry Mark
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = provider.nameAr,
                    tint = colors.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
            "cib" -> {
                Text(
                    text = "CIB",
                    fontSize = (size.value * 0.36f).sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
            "banque_misr", "bm" -> {
                Text(
                    text = "BM",
                    fontSize = (size.value * 0.38f).sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
            else -> {
                // General Banking/Wallet Icon
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = provider.nameAr,
                    tint = colors.primary,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
