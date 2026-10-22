package com.homecash.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.homecash.core.theme.*
import com.homecash.data.repository.ProviderManager
import com.homecash.ui.components.ProviderLogo
import com.homecash.ui.viewmodel.AuthViewModel

/**
 * Stitch Screen _8 Official Parity - Authentication & Cashier Login
 * Exactly replicates stitch_\_8:
 * - Top utilities bar (Language & Theme)
 * - Gradient floating wallet branding + "دفتر المحافظ" + "HomeCash" badge
 * - Login form with "نسيت كلمة المرور؟", person & lock icons, visibility toggle
 * - Primary button #4F63E8 & Google Sign-in button
 * - Supported Wallets horizontal badge strip (+6 Egyptian mobile wallets)
 * - 24/7 Support contact card
 * - Privacy reassurance, without claiming unimplemented encryption or cloud behavior
 * - Multi-step Onboarding setup wizard when logging in as admin
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onInitialSetupRequired: () -> Unit
) {
    val context = LocalContext.current
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.loginError.collectAsState()

    var isPasswordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showAdminOnboarding by remember { mutableStateOf(false) }
    var onboardingStep by remember { mutableStateOf(1) }
    var storeName by remember { mutableStateOf("سوبرماركت الأمل — كاشير 1") }
    var toastNotice by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FE)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ambient background glow (Stitch _8)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .offset(x = 100.dp, y = (-40).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4F63E8).copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-60).dp, y = 40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF702F8A).copy(alpha = 0.06f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Top Utilities Bar (Language + Dark Mode Toggle) - Stitch _8 lines 79-87
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E5F2), RoundedCornerShape(100.dp))
                            .clickable { toastNotice = "Language: English / العربية" }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                            Text("English", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E5F2), CircleShape)
                            .clickable { toastNotice = "تم ضبط وضع العرض" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Branding Header (Stitch _8 lines 90-104)
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Primary.copy(alpha = 0.35f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4F63E8), Color(0xFF8795F8))
                            )
                        )
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "حاسب",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF171A2B),
                        letterSpacing = (-0.3).sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Primary.copy(alpha = 0.12f))
                            .border(1.dp, Primary.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 9.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Haseb",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "تتبع معاملاتك المالية ومحافظك الإلكترونية تلقائياً بأمان وخصوصية تامة",
                    fontSize = 12.sp,
                    color = Color(0xFF5A5E6A),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Login Card (Stitch _8 lines 106-154)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E5F2)))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Username / Email Input
                        Text(
                            text = "اسم المستخدم أو البريد الإلكتروني",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF171A2B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { viewModel.onUsernameChange(it) },
                            placeholder = { Text("الاسم أو البريد الإلكتروني (admin)", fontSize = 13.sp, color = Color(0xFF9EA3B2)) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF757686), modifier = Modifier.size(20.dp))
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8F9FE),
                                unfocusedContainerColor = Color(0xFFF8F9FE),
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color(0xFFE2E5F2)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Row (Label + "نسيت كلمة المرور؟")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "كلمة المرور",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171A2B)
                            )
                            Text(
                                text = "نسيت كلمة المرور؟",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Primary,
                                modifier = Modifier.clickable { showForgotPasswordDialog = true }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            placeholder = { Text("••••••••", fontSize = 13.sp, color = Color(0xFF9EA3B2)) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF757686), modifier = Modifier.size(20.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (isPasswordVisible) Primary else Color(0xFF757686),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8F9FE),
                                unfocusedContainerColor = Color(0xFFF8F9FE),
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color(0xFFE2E5F2)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (error != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "",
                                color = LossCoral,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Primary Login Button (Stitch #4F63E8)
                        Button(
                            onClick = {
                                viewModel.login { isFirstTime ->
                                    if (isFirstTime && username.trim().equals("admin", ignoreCase = true)) {
                                        onboardingStep = 1
                                        showAdminOnboarding = true
                                    } else if (isFirstTime) {
                                        onInitialSetupRequired()
                                    } else onLoginSuccess()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(8.dp, RoundedCornerShape(14.dp), spotColor = Primary.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("تسجيل الدخول", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Icon(Icons.Default.Login, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Divider (Stitch _8 line 139)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E5F2))
                            Text(
                                text = "أو المتابعة باستخدام",
                                fontSize = 11.sp,
                                color = Color(0xFF5A5E6A),
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E5F2))
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Google Sign-in Button (Stitch _8 line 145)
                        OutlinedButton(
                            onClick = { viewModel.startGoogleSignIn(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E5F2)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF8F9FE))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4285F4)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                Text(
                                    text = "تسجيل الدخول بواسطة Google",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF171A2B)
                                )
                            }
                        }
                        TextButton(onClick = { showRegisterDialog = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("ليس لديك حساب؟ إنشاء حساب جديد", color = Primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Supported Wallets Carousel / Badges Card (Stitch _8 lines 158-200)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E5F2)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                Text("الجهات والمحافظ المدعومة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF171A2B))
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(Primary.copy(alpha = 0.10f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("+6 محافظ مصرية", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Carrier Badges with Official Logos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "vodafone_cash" to "فودافون كاش",
                                "instapay" to "إنستاباي InstaPay",
                                "etisalat_cash" to "اتصالات كاش",
                                "orange_cash" to "أورنج كاش",
                                "we_pay" to "وي باي WE Pay",
                                "fawry" to "فوري Fawry"
                            ).forEach { (id, label) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF8F9FE))
                                        .border(1.dp, Color(0xFFE2E5F2), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        ProviderLogo(providerId = id, size = 18.dp)
                                        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF171A2B))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Support Help Card (Stitch _8 lines 202-216)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD6E2FF)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("تحتاج إلى مساعدة؟", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF171A2B))
                                Text("فريق الدعم الفني جاهز لمساعدتك دائماً", fontSize = 10.sp, color = Color(0xFF5A5E6A))
                            }
                        }

                        Button(
                            onClick = { showSupportDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6E2FF)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("تواصل مع الدعم", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                                Icon(Icons.Default.Chat, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer Security & Legal (Stitch _8 lines 218-233)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0xFFEBF1FF))
                        .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                        Text(
                            text = "تُعالج الرسائل على الجهاز افتراضيًا؛ لا تتم المزامنة إلا بعد إعداد الحساب والموافقة",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سياسة الخصوصية", fontSize = 11.sp, color = Color(0xFF5A5E6A), modifier = Modifier.clickable { toastNotice = "راجع سياسة الخصوصية لمعرفة البيانات المحلية والسحابية" })
                    Text("•", fontSize = 11.sp, color = Color(0xFF5A5E6A))
                    Text("شروط الاستخدام", fontSize = 11.sp, color = Color(0xFF5A5E6A), modifier = Modifier.clickable { toastNotice = "حاسب Haseb v1.0" })
                    Text("•", fontSize = 11.sp, color = Color(0xFF5A5E6A))
                    Text("الإصدار 1.0.0", fontSize = 11.sp, color = Color(0xFF5A5E6A))
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("استعادة كلمة المرور", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "أدخل البريد الإلكتروني المرتبط بحسابك في حقل تسجيل الدخول، ثم اضغط إرسال رابط الاستعادة. ستُرسل الرسالة عبر خدمة Supabase بعد إعداد المشروع.",
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showForgotPasswordDialog = false; viewModel.forgotPassword() }) {
                    Text("إرسال رابط الاستعادة", fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        )
    }

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text("إنشاء حساب حاسب", fontWeight = FontWeight.Bold) },
            text = { Text("اكتب بريدك الإلكتروني وكلمة مرور لا تقل عن 8 أحرف في حقول الشاشة، ثم اضغط إنشاء الحساب. قد تحتاج إلى تأكيد بريدك قبل تسجيل الدخول.", fontSize = 13.sp, lineHeight = 20.sp) },
            confirmButton = { TextButton(onClick = { showRegisterDialog = false; viewModel.register() }) { Text("إنشاء الحساب", color = Primary, fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = { showRegisterDialog = false }) { Text("إلغاء") } }
        )
    }

    val authNotice by viewModel.notice.collectAsState()
    if (authNotice != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearNotice() },
            title = { Text("حاسب", fontWeight = FontWeight.Bold) },
            text = { Text(authNotice ?: "", fontSize = 13.sp, lineHeight = 20.sp) },
            confirmButton = { TextButton(onClick = { viewModel.clearNotice() }) { Text("حسنًا", color = Primary) } }
        )
    }

    // Support Contact Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("الدعم الفني والمساعدة", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("فريق حاسب جاهز لمساعدتك على مدار الساعة:", fontSize = 13.sp)
                    Text("قنوات التواصل الرسمية ستظهر بعد إعداد بيانات الدعم الخاصة بالمشروع.", fontSize = 12.sp, lineHeight = 18.sp)
                    Text("يمكنك استخدام البيانات المحلية؛ المزامنة السحابية غير مفعّلة في هذه النسخة.", fontSize = 12.sp, color = GainEmerald)
                }
            },
            confirmButton = {
                Button(onClick = { showSupportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("إغلاق", color = Color.White)
                }
            }
        )
    }

    // =========================================================================
    // Multi-Step Onboarding Setup Wizard for Admin (Brand new account experience)
    // "كل مره يتم ادخال الحساب admin يظهر الخطوات و كاني اول مره ادخل الحساب"
    // =========================================================================
    if (showAdminOnboarding) {
        Dialog(onDismissRequest = { showAdminOnboarding = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(22.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header progress indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "تهيئة الحساب الجديد ($onboardingStep/3)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..3).forEach { step ->
                                Box(
                                    modifier = Modifier
                                        .size(width = if (step == onboardingStep) 20.dp else 8.dp, height = 6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (step <= onboardingStep) Primary else Color(0xFFE2E5F2))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (onboardingStep) {
                        1 -> {
                            // Step 1: Store & Cashier Profile Setup
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "مرحباً بك في حاسب!",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171A2B)
                            )
                            Text(
                                text = "قم بإعداد اسم المحل أو منفذ الكاشير الخاص بك للبدء في تتبع الرسائل",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = storeName,
                                onValueChange = { storeName = it },
                                label = { Text("اسم المحل / منفذ البيع") },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8F9FE))
                                    .border(1.dp, Color(0xFFE2E5F2), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(20.dp))
                                    Text("وضع تجريبي: البيانات المعروضة أمثلة محلية وليست أرصدة حقيقية", fontSize = 11.sp, color = TextPrimary)
                                }
                            }
                        }
                        2 -> {
                            // Step 2: Active Wallets & Opening Balance
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GainEmerald.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GainEmerald, modifier = Modifier.size(28.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "تحديد المحافظ والأرصدة الافتتاحية",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171A2B)
                            )
                            Text(
                                text = "بيانات المحافظ والأرصدة التالية أمثلة للتجربة فقط، ويمكنك استبدالها بإعداداتك",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                            )

                            listOf(
                                "vodafone_cash" to ("فودافون كاش (SIM 1)" to "15,450 ج.م"),
                                "instapay" to ("إنستاباي (حساب بنكي)" to "24,800 ج.م"),
                                "etisalat_cash" to ("اتصالات كاش (SIM 2)" to "9,200 ج.م"),
                                "orange_cash" to ("أورنج كاش" to "6,500 ج.م")
                            ).forEach { (pId, info) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8F9FE))
                                        .border(1.dp, Color(0xFFE2E5F2), RoundedCornerShape(12.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        ProviderLogo(providerId = pId, size = 26.dp)
                                        Text(info.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                    Text(info.second, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = GainEmerald)
                                }
                            }
                        }
                        3 -> {
                            // Step 3: Multi-Device Sync & Simulation Ready
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF702F8A).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Devices, contentDescription = null, tint = Color(0xFF702F8A), modifier = Modifier.size(28.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "محاكاة الأجهزة والرسائل (Demo)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF171A2B)
                            )
                            Text(
                                text = "هذه الأجهزة افتراضية للمعاينة فقط؛ لن تتم مزامنة أي هاتف آخر دون إعداد خدمة سحابية",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFF8F9FE))
                                    .border(1.dp, Color(0xFFE2E5F2), RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                                    Text("الجهاز 1 (الرئيسي): Vivo V2430 (هاتفك الحالي) ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    Text("الجهاز 2: Samsung Galaxy A54 (مبيعات 1) ✓", fontSize = 11.sp, color = TextPrimary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                                    Text("الجهاز 3: Xiaomi Redmi Note 12 (كاشير 2) ✓", fontSize = 11.sp, color = TextPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (onboardingStep > 1) {
                            OutlinedButton(
                                onClick = { onboardingStep-- },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("السابق", fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                if (onboardingStep < 3) {
                                    onboardingStep++
                                } else {
                                    showAdminOnboarding = false
                                    onLoginSuccess()
                                }
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text(
                                text = if (onboardingStep < 3) "التالي" else "بدء الاستخدام 🚀",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
